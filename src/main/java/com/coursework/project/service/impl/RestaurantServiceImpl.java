package com.coursework.project.service.impl;

import com.coursework.project.dto.*;
import com.coursework.project.entity.*;
import com.coursework.project.logging.CustomLogger;
import com.coursework.project.repository.*;
import com.coursework.project.service.RestaurantService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.coursework.project.constant.Constant.PHOTO_DIRECTORY;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
@Transactional(rollbackOn = Exception.class)
public class RestaurantServiceImpl implements RestaurantService {

  private final RestaurantRepository restaurantRepository;
  private final ContactInfoRepository contactInfoRepository;
  private final WorkHoursRepository workHoursRepository;
  private final DiningTableRepository diningTableRepository;
  private final AddressRepository addressRepository;
  private final UserRepository userRepository;

  public RestaurantServiceImpl(RestaurantRepository restaurantRepository, ContactInfoRepository contactInfoRepository,
          WorkHoursRepository workHoursRepository, DiningTableRepository diningTableRepository, AddressRepository addressRepository,
          UserRepository userRepository) {
    this.restaurantRepository = restaurantRepository;
    this.contactInfoRepository = contactInfoRepository;
    this.workHoursRepository = workHoursRepository;
    this.diningTableRepository = diningTableRepository;
    this.addressRepository = addressRepository;
    this.userRepository = userRepository;
  }

  @Override
  public Restaurant createRestaurant(RestaurantDTO restaurantDTO, Long userId) {
    try {
      User creator = userRepository.findById(userId)
              .orElseThrow(() -> new RuntimeException("User not found"));

      Restaurant restaurant = new Restaurant();
      restaurant.setName(restaurantDTO.getName());
      restaurant.setDescription(restaurantDTO.getDescription());
      restaurant.setPhotos(restaurantDTO.getPhotos());
      restaurant.setRating(restaurantDTO.getRating());
      restaurant.setCuisineType(restaurantDTO.getCuisineType());
      restaurant.setWebsite(restaurantDTO.getWebsite());
      restaurant.setMenu(restaurantDTO.getMenu());
      restaurant.setPopularityCount(restaurantDTO.getPopularityCount());
      restaurant.setPriceCategory(restaurantDTO.getPriceCategory());
      restaurant.setCreator(creator);

      AddressDTO addressDTO = restaurantDTO.getAddress();
      if (addressDTO != null) {
        Address address = new Address();
        address.setFormattedAddress(addressDTO.getFormattedAddress());
        address.setLatitude(addressDTO.getLatitude());
        address.setLongitude(addressDTO.getLongitude());
        address.setCity(addressDTO.getCity());
        address.setCountry(addressDTO.getCountry());
        addressRepository.save(address);
        restaurant.setAddress(address);
      }

      ContactInfoDTO contactInfoDTO = restaurantDTO.getContactInfo();
      if (contactInfoDTO != null) {
        ContactInfo contactInfo = new ContactInfo();
        contactInfo.setPhoneNumber(contactInfoDTO.getPhoneNumber());
        contactInfo.setEmail(contactInfoDTO.getEmail());
        contactInfoRepository.save(contactInfo);
        restaurant.setContactInfo(contactInfo);
      }
      restaurantRepository.save(restaurant);

      List<WorkHoursDTO> workHoursDTOList = restaurantDTO.getWorkHours();
      if (workHoursDTOList != null) {
        for (WorkHoursDTO workHoursDTO : workHoursDTOList) {
          WorkHours workHours = new WorkHours();
          workHours.setDayOfWeek(workHoursDTO.getDayOfWeek());
          workHours.setStartTime(workHoursDTO.getStartTime());
          workHours.setEndTime(workHoursDTO.getEndTime());
          workHours.setDayOff(workHoursDTO.isDayOff() ? workHoursDTO.isDayOff() : false);
          workHours.setRestaurant(restaurant);
          workHoursRepository.save(workHours);
        }
      }

      List<DiningTableDTO> diningTableDTOList = restaurantDTO.getDiningTables();
      if (diningTableDTOList != null) {
        for(DiningTableDTO diningTableDTO : diningTableDTOList) {
          DiningTable diningTable = new DiningTable();
          diningTable.setCapacity(diningTableDTO.getCapacity());
          diningTable.setRestaurant(restaurant);
          diningTableRepository.save(diningTable);
        }
      }

      CustomLogger.logInfo("RestaurantServiceImpl Restaurant created successfully: " + restaurant.getName());
      return restaurant;
    } catch (Exception e) {
      CustomLogger.logError("RestaurantServiceImpl Error creating restaurant: " + e.getMessage());
      throw e;
    }
  }

  @Override
  public Page<Restaurant> getAllRestaurants(int page, int size) {
    try {
      CustomLogger.logInfo("RestaurantServiceImpl Retrieved all restaurants successfully");
      return restaurantRepository.findAll(PageRequest.of(page, size, Sort.by("name")));
    } catch (Exception e) {
      CustomLogger.logError("RestaurantServiceImpl Error getting all restaurants: " + e.getMessage());
      throw e;
    }
  }

  @Override
  public boolean deleteRestaurant(Long id) {
    try {
      restaurantRepository.deleteById(id);
      CustomLogger.logInfo("RestaurantServiceImpl Deleted restaurant with ID: " + id);
      return true;
    } catch (Exception e) {
      CustomLogger.logError("RestaurantServiceImpl Error deleting restaurant with ID " + id + ": " + e.getMessage());
      throw e;
    }
  }

  @Override
  public Restaurant getRestaurantById(Long id) {
    try {
      Optional<Restaurant> optionalRestaurant = restaurantRepository.findById(id);
      CustomLogger.logInfo("RestaurantServiceImpl Retrieved restaurant by ID: " + id);
      return optionalRestaurant.orElse(null);
    } catch (Exception e) {
      CustomLogger.logError("RestaurantServiceImpl Error getting restaurant with ID " + id + ": " + e.getMessage());
      throw e;
    }
  }

  public String uploadPhoto(Long id, MultipartFile file) {
    try {
      Restaurant restaurant = getRestaurantById(id);
      String photoUrl = photoFunction.apply(id, file);
      List<String> existingPhotos = restaurant.getPhotos();
      existingPhotos.add(photoUrl);
      restaurant.setPhotos(existingPhotos);
      restaurantRepository.save(restaurant);
      CustomLogger.logInfo("RestaurantServiceImpl Uploaded photo for restaurant with ID: " + id);
      return photoUrl;
    } catch (Exception e) {
      CustomLogger.logError("RestaurantServiceImpl Error uploading photo for restaurant with ID " + id + ": " + e.getMessage());
      throw e;
    }
  }

  private final Function<String, String> fileExtension = filename -> Optional.of(filename).filter(name -> name.contains("."))
          .map(name -> "." + name.substring(filename.lastIndexOf(".") + 1)).orElse(".png");

  private final BiFunction<Long, MultipartFile, String> photoFunction = (id, image) -> {
    String filename = UUID.randomUUID().toString() + fileExtension.apply(image.getOriginalFilename());
    try {
      Path fileStorageLocation = Paths.get(PHOTO_DIRECTORY).toAbsolutePath().normalize();
      if (!Files.exists(fileStorageLocation)) {
        Files.createDirectories(fileStorageLocation);
      }
      Files.copy(image.getInputStream(), fileStorageLocation.resolve(filename), REPLACE_EXISTING);
      return ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/restaurants/image/" + filename).toUriString();
    } catch (Exception exception) {
      throw new RuntimeException("Unable to save image");
    }
  };

  @Override
  public Page<Restaurant> searchByNameOrFirstLetter(String searchTerm, Pageable pageable) {
    try {
      CustomLogger.logInfo("RestaurantServiceImpl Inside searchByNameOrFirstLetter");
      return restaurantRepository.findByNameOrFirstLetter(searchTerm, pageable);
    } catch (Exception e) {
      CustomLogger.logError("RestaurantServiceImpl Error in searchByNameOrFirstLetter" + e.getMessage());
      throw e;
    }
  }

  @Override
  public Page<Restaurant> searchByMultipleFields(List<String> searchTerms, Pageable pageable) {
    try {
      SearchCriteria criteria = parseSearchTerms(searchTerms);

      CustomLogger.logInfo("RestaurantServiceImpl Performing search with parameters - " + criteria);

      return restaurantRepository.searchByMultipleFields(criteria.getCities(), criteria.getCuisines(), criteria.getRatings(),
              criteria.getPriceCategories(), pageable);
    } catch (Exception e) {
      CustomLogger.logError("RestaurantServiceImpl Error performing search: " + e.getMessage());
      throw e;
    }
  }

  private SearchCriteria parseSearchTerms(List<String> searchTerms) {
    return new SearchCriteria(searchTerms.stream().filter(this::isCity).collect(Collectors.toList()),
            searchTerms.stream().filter(this::isCuisine).collect(Collectors.toList()),
            searchTerms.stream().filter(this::isRating).collect(Collectors.toList()),
            searchTerms.stream().filter(this::isPriceCategory).map(String::toUpperCase).collect(Collectors.toList()));
  }

  private boolean isCity(String term) {
    return List.of("львів", "київ", "харків", "дніпро", "одеса").contains(term.toLowerCase());
  }

  private boolean isCuisine(String term) {
    return List.of("italian", "ukrainian", "indian", "chinese", "mexican", "international", "american").contains(term.toLowerCase());
  }

  private boolean isRating(String term) {
    try {
      double rating = Double.parseDouble(term);
      return rating >= 1.0 && rating <= 5.0;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  private boolean isPriceCategory(String term) {
    return List.of("low", "medium", "high").contains(term.toLowerCase());
  }

  @Override
  public void updatePopularityCount(Long restaurantId, int newPopularityCount) {
    try {
      CustomLogger.logInfo("RestaurantServiceImpl Inside updatePopularityCount");
      restaurantRepository.updatePopularityCountById(restaurantId, newPopularityCount);
    } catch (Exception e) {
      CustomLogger.logError("RestaurantServiceImpl Error in updatePopularityCount" + e.getMessage());
      throw e;
    }
  }

  @Override
  public Page<Restaurant> getAllRestaurantsSortedByRating(int page, int size) {
    try {
      Pageable pageable = PageRequest.of(page, size, Sort.by("rating").descending());

      CustomLogger.logInfo("RestaurantServiceImpl Retrieved restaurants sorted by rating successfully");
      return restaurantRepository.findAll(pageable);
    } catch (Exception e) {
      CustomLogger.logError("RestaurantServiceImpl Error getting restaurants sorted by rating: " + e.getMessage());
      throw e; // Rethrow the exception for the transaction to rollback
    }
  }

  @Override
  public Page<Restaurant> getAllRestaurantsSortedByPopularity(int page, int size) {

    try {
      Pageable pageable = PageRequest.of(page, size, Sort.by("popularityCount").descending());
      CustomLogger.logInfo("RestaurantServiceImpl Retrieved restaurants sorted by popularity successfully");
      return restaurantRepository.findAll(pageable);
    } catch (Exception e) {
      CustomLogger.logError("RestaurantServiceImpl Error getting restaurants sorted by popularity: " + e.getMessage());
      throw e;
    }
  }

  public List<Restaurant> getCreatedRestaurants(Long userId) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

    return user.getCreatedRestaurants();
  }

  @Override
  public Restaurant updateRestaurant(Long restaurantId, RestaurantDTO restaurantDTO) {
    try {
      Restaurant existingRestaurant = restaurantRepository.findById(restaurantId)
              .orElseThrow(() -> new RuntimeException("Restaurant not found"));

      existingRestaurant.setName(restaurantDTO.getName());
      existingRestaurant.setDescription(restaurantDTO.getDescription());
      existingRestaurant.setCuisineType(restaurantDTO.getCuisineType());
      existingRestaurant.setWebsite(restaurantDTO.getWebsite());
      existingRestaurant.setMenu(restaurantDTO.getMenu());
      existingRestaurant.setPriceCategory(restaurantDTO.getPriceCategory());

      AddressDTO addressDTO = restaurantDTO.getAddress();
      if (addressDTO != null) {
        Address address = existingRestaurant.getAddress();
        if (address == null) {
          address = new Address();
        }
        address.setFormattedAddress(addressDTO.getFormattedAddress());
        address.setLatitude(addressDTO.getLatitude());
        address.setLongitude(addressDTO.getLongitude());
        address.setCity(addressDTO.getCity());
        address.setCountry(addressDTO.getCountry());
        addressRepository.save(address);
        existingRestaurant.setAddress(address);
      }

      ContactInfoDTO contactInfoDTO = restaurantDTO.getContactInfo();
      if (contactInfoDTO != null) {
        ContactInfo contactInfo = existingRestaurant.getContactInfo();
        if (contactInfo == null) {
          contactInfo = new ContactInfo();
        }
        contactInfo.setPhoneNumber(contactInfoDTO.getPhoneNumber());
        contactInfo.setEmail(contactInfoDTO.getEmail());
        contactInfoRepository.save(contactInfo);
        existingRestaurant.setContactInfo(contactInfo);
      }

      if (restaurantDTO.getWorkHours() != null) {
        workHoursRepository.deleteByRestaurant(existingRestaurant);

        for (WorkHoursDTO workHoursDTO : restaurantDTO.getWorkHours()) {
          WorkHours workHours = new WorkHours();
          workHours.setDayOfWeek(workHoursDTO.getDayOfWeek());
          workHours.setStartTime(workHoursDTO.getStartTime());
          workHours.setEndTime(workHoursDTO.getEndTime());
          workHours.setDayOff(workHoursDTO.isDayOff());
          workHours.setRestaurant(existingRestaurant);
          workHoursRepository.save(workHours);
        }
      }

      if (restaurantDTO.getDiningTables() != null) {
        diningTableRepository.deleteByRestaurant(existingRestaurant);

        for (DiningTableDTO diningTableDTO : restaurantDTO.getDiningTables()) {
          DiningTable diningTable = new DiningTable();
          diningTable.setCapacity(diningTableDTO.getCapacity());
          diningTable.setRestaurant(existingRestaurant);
          diningTableRepository.save(diningTable);
        }
      }

      CustomLogger.logInfo("RestaurantServiceImpl Restaurant updated successfully: " + existingRestaurant.getName());
      return restaurantRepository.save(existingRestaurant);
    } catch (Exception e) {
      CustomLogger.logError("RestaurantServiceImpl Error updating restaurant: " + e.getMessage());
      throw e;
    }
  }
}

