package com.coursework.project.config;

import com.github.javafaker.Faker;
import com.coursework.project.entity.*;
import com.coursework.project.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RandomDataGenerator implements CommandLineRunner {

  private final RestaurantRepository restaurantRepository;
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;
  private final Faker faker = new Faker(new Locale("en"));

  private static final int NUMBER_OF_RESTAURANTS = 50;
  private static final int NUMBER_OF_USERS = 30;
  private static final int MIN_TABLES_PER_RESTAURANT = 5;
  private static final int MAX_TABLES_PER_RESTAURANT = 20;
  private static final int MIN_FEEDBACK_PER_RESTAURANT = 3;
  private static final int MAX_FEEDBACK_PER_RESTAURANT = 15;

  @Override
  @Transactional
  public void run(String... args) {
    if (roleRepository.count() == 0) {
      initializeData();
    }
  }

  private void initializeData() {
    // Create and save roles first
    Role userRole = roleRepository.save(new Role(null, "ROLE_USER"));
    Role adminRole = roleRepository.save(new Role(null, "ROLE_ADMIN"));

    // Create admin user with saved role
    User admin = User.builder()
            .name("Admin")
            .surname("Admin")
            .email("admin@example.com")
            .password(passwordEncoder.encode("admin123"))
            .roles(new HashSet<>(Arrays.asList(adminRole)))
            .build();
    admin = userRepository.save(admin);

    // Create regular users
    List<User> users = new ArrayList<>();
    for (int i = 0; i < NUMBER_OF_USERS; i++) {
      User user = User.builder()
              .name(faker.name().firstName())
              .surname(faker.name().lastName())
              .email(faker.internet().emailAddress())
              .password(passwordEncoder.encode("user123"))
              .roles(new HashSet<>(Arrays.asList(userRole)))
              .build();
      users.add(userRepository.save(user));
    }

    // Create restaurants (all created by admin)
    for (int i = 0; i < NUMBER_OF_RESTAURANTS; i++) {
      createRandomRestaurant(admin, users);
    }
  }

  private Restaurant createRandomRestaurant(User admin, List<User> users) {
    Restaurant restaurant = new Restaurant();

    // Basic info
    String restaurantName = generateRestaurantName();
    restaurant.setName(restaurantName);
    restaurant.setDescription(faker.lorem().paragraph());
    restaurant.setCuisineType(getRandomCuisineType());
    restaurant.setPriceCategory(getRandomPriceCategory());
    restaurant.setCreator(admin);
    restaurant.setRating(faker.number().randomDouble(1, 3, 5));
    restaurant.setPopularityCount(faker.number().numberBetween(0, 1000));
    restaurant.setPhotos(generateRandomPhotoPaths());
    String websiteName = restaurantName.toLowerCase().replace(" ", "").replace("'", "");
    if (websiteName.length() > 20) {
      websiteName = websiteName.substring(0, 20);
    }
    restaurant.setWebsite("http://www." + websiteName + ".com");
    restaurant.setMenu(generateMenuUrl(restaurantName));

    // Address
    Address address = createRandomAddress();
    restaurant.setAddress(address);

    // Contact info
    ContactInfo contactInfo = createRandomContactInfo(restaurant);
    restaurant.setContactInfo(contactInfo);

    // Save the restaurant first to get an ID
    restaurant = restaurantRepository.save(restaurant);

    // Work hours
    List<WorkHours> workHours = createRandomWorkHours(restaurant);
    restaurant.setWorkHours(workHours);

    // Dining tables
    List<DiningTable> tables = createRandomDiningTables(restaurant);
    restaurant.setDiningTables(tables);

    // Feedback
    List<Feedback> feedbackList = createRandomFeedback(restaurant, users);
    restaurant.setFeedbackList(feedbackList);

    return restaurantRepository.save(restaurant);
  }

  private String generateRestaurantName() {
    String[] prefixes = {"The", "La", "El", "Le", ""};
    String[] adjectives = {"Golden", "Royal", "Blue", "Green", "Silver", "Red", "Peaceful"};
    String[] nouns = {"Fork", "Spoon", "Table", "Kitchen", "Garden", "House", "Room", "Place"};

    String prefix = prefixes[faker.random().nextInt(prefixes.length)];
    String adj = adjectives[faker.random().nextInt(adjectives.length)];
    String noun = nouns[faker.random().nextInt(nouns.length)];

    return (prefix.isEmpty() ? "" : prefix + " ") + adj + " " + noun;
  }

  private List<String> generateRandomPhotoPaths() {
    List<String> photos = new ArrayList<>();
    int numPhotos = faker.random().nextInt(3, 8);
    for (int i = 0; i < numPhotos; i++) {
      photos.add("/images/restaurants/restaurant" + faker.random().nextInt(1, 100) + ".jpg");
    }
    return photos;
  }

  private String generateMenuUrl(String restaurantName) {
    String baseRestaurantName = restaurantName.toLowerCase()
            .replace(" ", "")
            .replace("'", "")
            .replaceAll("[^a-zA-Z0-9]", "");
    if (baseRestaurantName.length() > 20) {
      baseRestaurantName = baseRestaurantName.substring(0, 20);
    }
    return "https://menu." + baseRestaurantName + ".com/menu.pdf";
  }

  private Address createRandomAddress() {
    Address address = new Address();
    address.setFormattedAddress(faker.address().streetAddress());
    address.setLatitude(Double.parseDouble(faker.address().latitude()));
    address.setLongitude(Double.parseDouble(faker.address().longitude()));
    address.setCity(faker.address().city());
    address.setCountry(faker.address().country());
    return address;
  }

  private ContactInfo createRandomContactInfo(Restaurant restaurant) {
    ContactInfo contactInfo = new ContactInfo();
    contactInfo.setEmail("contact@" + restaurant.getName().toLowerCase().replace(" ", "") + ".com");
    contactInfo.setPhoneNumber(faker.phoneNumber().phoneNumber());
    contactInfo.setRestaurant(restaurant);
    return contactInfo;
  }

  private List<WorkHours> createRandomWorkHours(Restaurant restaurant) {
    List<WorkHours> workHours = new ArrayList<>();
    for (DayOfWeek day : DayOfWeek.values()) {
      WorkHours hours = new WorkHours();
      hours.setDayOfWeek(day);
      hours.setDayOff(day == DayOfWeek.SUNDAY || faker.random().nextInt(100) < 10);

      if (!hours.isDayOff()) {
        int startHour = faker.random().nextInt(7, 11);
        int endHour = faker.random().nextInt(20, 24);
        hours.setStartTime(String.format("%02d:00", startHour));
        hours.setEndTime(String.format("%02d:00", endHour));
      }

      hours.setRestaurant(restaurant);
      workHours.add(hours);
    }
    return workHours;
  }

  private List<DiningTable> createRandomDiningTables(Restaurant restaurant) {
    List<DiningTable> tables = new ArrayList<>();
    int numTables = faker.random().nextInt(MIN_TABLES_PER_RESTAURANT, MAX_TABLES_PER_RESTAURANT + 1);

    for (int i = 0; i < numTables; i++) {
      DiningTable table = new DiningTable();
      table.setCapacity(faker.random().nextInt(2, 9));
      table.setRestaurant(restaurant);
      tables.add(table);
    }
    return tables;
  }

  private List<Feedback> createRandomFeedback(Restaurant restaurant, List<User> users) {
    List<Feedback> feedbackList = new ArrayList<>();
    int numFeedback = faker.random().nextInt(MIN_FEEDBACK_PER_RESTAURANT, MAX_FEEDBACK_PER_RESTAURANT + 1);

    for (int i = 0; i < numFeedback; i++) {
      Feedback feedback = new Feedback();
      feedback.setRating(faker.number().randomDouble(1, 1, 5));
      feedback.setDescription(faker.lorem().paragraph());
      feedback.setAdvantages(faker.lorem().sentence());
      feedback.setDisadvantages(faker.lorem().sentence());
      feedback.setDate(faker.date().past(365, TimeUnit.DAYS));
      feedback.setRestaurant(restaurant);
      feedback.setUser(users.get(faker.random().nextInt(users.size())));
      feedbackList.add(feedback);
    }
    return feedbackList;
  }

  private CuisineType getRandomCuisineType() {
    return CuisineType.values()[faker.random().nextInt(CuisineType.values().length)];
  }

  private PriceCategory getRandomPriceCategory() {
    return PriceCategory.values()[faker.random().nextInt(PriceCategory.values().length)];
  }
}