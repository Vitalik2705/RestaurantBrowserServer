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
  private final Faker faker = new Faker(new Locale("uk"));

  private static final int NUMBER_OF_RESTAURANTS = 50;
  private static final int NUMBER_OF_USERS = 30;
  private static final int MIN_TABLES_PER_RESTAURANT = 5;
  private static final int MAX_TABLES_PER_RESTAURANT = 20;
  private static final int MIN_FEEDBACK_PER_RESTAURANT = 3;
  private static final int MAX_FEEDBACK_PER_RESTAURANT = 15;

  @Override
  @Transactional
  public void run(String... args) {
//    if (roleRepository.count() == 0) {
//      initializeData();
//    }
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
            .password(passwordEncoder.encode("Admin123"))
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
              .password(passwordEncoder.encode("User1234"))
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
    restaurant.setDescription(generateDescription());
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
    String[] prefixes = {"", "Старий", "Новий", "Золотий", "Смачний", "Теплий", "Щирий", "Файний", "Добрий", "Рідний", "Веселий"};
    String[] nouns = {
            "Куток", "Затишок", "Борщ", "Вареник", "Смак", "Хата",
            "Пиріг", "Корчма", "Світлиця", "Келих", "Колиба", "Галушка",
            "Шинок", "Калина", "Барліг", "Вітряк", "Млин", "Пивниця",
            "Їдальня", "Садиба", "Трапезна", "Дворик", "Господа", "Вулик"
    };
    String[] suffixes = {"", "і Компанія", "та Друзі", "у Саду", "на Подолі", "на Площі"};

    String prefix = prefixes[faker.random().nextInt(prefixes.length)];
    String noun = nouns[faker.random().nextInt(nouns.length)];
    String suffix = suffixes[faker.random().nextInt(suffixes.length)];

    return (prefix.isEmpty() ? "" : prefix + " ") + noun + (suffix.isEmpty() ? "" : " " + suffix);
  }

  private String generateDescription() {
    String[] intro = {
            "Ласкаво просимо до ресторану", "Запрошуємо до затишного закладу",
            "Відкрийте для себе неповторну атмосферу", "У самому серці міста розташувався",
            "Завітайте до нашого ресторану", "Поринути в атмосферу справжньої гостинності"
    };

    String[] features = {
            "традиційні українські страви", "авторська кухня від шеф-кухаря",
            "страви за старовинними рецептами", "сезонне меню з локальних продуктів",
            "унікальні гастрономічні поєднання", "домашня випічка",
            "фірмові настоянки", "крафтове пиво власного виробництва",
            "страви на живому вогні", "авторські коктейлі",
            "винна карта від сомельє", "власна сироварня",
            "домашні соління та маринади", "фермерські продукти",
            "страви в печі на дровах"
    };

    String[] atmosphere = {
            "затишний інтер'єр у етнічному стилі", "панорамні вікна з видом на місто",
            "жива музика щовихідних", "літня тераса в саду",
            "камін для затишної атмосфери", "відкрита кухня",
            "дитяча кімната", "власна пекарня",
            "історична будівля XIX століття", "унікальні витвори народних майстрів",
            "колекція старовинних предметів побуту", "автентичні елементи декору"
    };

    String[] special = {
            "Регулярно проводимо майстер-класи з приготування", "Організовуємо тематичні вечори",
            "Пропонуємо дегустаційні сети", "Проводимо весілля та банкети",
            "Влаштовуємо гастрономічні фестивалі", "Запрошуємо на бранчі щовихідних",
            "Маємо окреме вегетаріанське меню", "Враховуємо індивідуальні побажання гостей"
    };

    return intro[faker.random().nextInt(intro.length)] + ". " +
            "Ми пропонуємо " + features[faker.random().nextInt(features.length)] + " та " +
            features[faker.random().nextInt(features.length)] + ". " +
            "Наш заклад вирізняє " + atmosphere[faker.random().nextInt(atmosphere.length)] + ". " +
            special[faker.random().nextInt(special.length)] + ". " +
            atmosphere[faker.random().nextInt(atmosphere.length)] + ".";
  }

  private List<String> generateRandomPhotoPaths() {
    List<String> predefinedPhotos = Arrays.asList(
            "https://images.pexels.com/photos/262978/pexels-photo-262978.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
            "https://images.pexels.com/photos/67468/pexels-photo-67468.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
            "https://images.pexels.com/photos/941861/pexels-photo-941861.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
            "https://images.pexels.com/photos/958545/pexels-photo-958545.jpeg?auto=compress&cs=tinysrgb&w=800",
            "https://images.pexels.com/photos/262047/pexels-photo-262047.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
            "https://images.pexels.com/photos/1267320/pexels-photo-1267320.jpeg?auto=compress&cs=tinysrgb&w=800",
            "https://images.pexels.com/photos/1581384/pexels-photo-1581384.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
            "https://images.pexels.com/photos/541216/pexels-photo-541216.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
            "https://images.pexels.com/photos/2104558/pexels-photo-2104558.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
            "https://images.pexels.com/photos/239975/pexels-photo-239975.jpeg?auto=compress&cs=tinysrgb&w=800",
            "https://images.pexels.com/photos/460537/pexels-photo-460537.jpeg?auto=compress&cs=tinysrgb&w=800",
            "https://images.pexels.com/photos/914388/pexels-photo-914388.jpeg?auto=compress&cs=tinysrgb&w=800"
    );
    List<String> photos = new ArrayList<>();
    int numPhotos = faker.random().nextInt(1, 4);
    for (int i = 0; i < numPhotos; i++) {
      photos.add(predefinedPhotos.get(faker.random().nextInt(predefinedPhotos.size())));
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
    String[] cities = {"Київ", "Львів", "Харків", "Одеса", "Дніпро", "Запоріжжя"};
    Address address = new Address();
    address.setFormattedAddress(faker.address().streetName() + ", " + faker.random().nextInt(1, 100));
    address.setLatitude(Double.parseDouble(faker.address().latitude()));
    address.setLongitude(Double.parseDouble(faker.address().longitude()));
    address.setCity(cities[faker.random().nextInt(cities.length)]);
    address.setCountry("Україна");
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