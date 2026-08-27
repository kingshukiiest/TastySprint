package com.foodordering.config;

import com.foodordering.category.entity.Category;
import com.foodordering.category.repository.CategoryRepository;
import com.foodordering.food.entity.Food;
import com.foodordering.food.repository.FoodRepository;
import com.foodordering.restaurant.entity.Restaurant;
import com.foodordering.restaurant.repository.RestaurantRepository;
import com.foodordering.user.entity.Role;
import com.foodordering.user.entity.User;
import com.foodordering.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Automatically seeds demo Users, Categories, Restaurants, and custom Indian menu Food items.
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RestaurantRepository restaurantRepository;
    private final FoodRepository foodRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Refresh catalog with custom items
        foodRepository.deleteAll();
        restaurantRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();

        // 1. Seed Users
        User customer = User.builder()
                .name("John Customer")
                .email("customer@tastysprint.com")
                .password(passwordEncoder.encode("password123"))
                .phone("+919876543210")
                .role(Role.CUSTOMER)
                .build();

        User owner = User.builder()
                .name("Sujoy Owner")
                .email("owner@tastysprint.com")
                .password(passwordEncoder.encode("password123"))
                .phone("+919876543211")
                .role(Role.RESTAURANT_OWNER)
                .build();

        User admin = User.builder()
                .name("Admin User")
                .email("admin@tastysprint.com")
                .password(passwordEncoder.encode("password123"))
                .phone("+919876543212")
                .role(Role.ADMIN)
                .build();

        userRepository.saveAll(Arrays.asList(customer, owner, admin));

        // 2. Seed Categories
        Category biryaniCat = Category.builder().name("Biryani Special").description("Aromatic dum biryani specials").build();
        Category mainCourseCat = Category.builder().name("Main Course & Rice").description("Fried rice, chaap & gravies").build();
        Category thaliCat = Category.builder().name("Thali & Meals").description("Complete thali combos").build();
        Category beverageCat = Category.builder().name("Beverages & Shakes").description("Refreshing drinks & milkshakes").build();

        categoryRepository.saveAll(Arrays.asList(biryaniCat, mainCourseCat, thaliCat, beverageCat));

        // 3. Seed Restaurants
        Restaurant rest1 = Restaurant.builder()
                .name("Royal Biryani & Mughlai Station")
                .description("Authentic Mughlai Biryani & Chaap Delicacies")
                .address("12 Park Street, Kolkata")
                .phone("+919876543211")
                .rating(4.9)
                .owner(owner)
                .build();

        restaurantRepository.save(rest1);

        // 4. Seed Custom Specified Food Items
        List<Food> foods = Arrays.asList(
                Food.builder()
                        .name("Chicken Biryani")
                        .description("Aromatic basmati rice cooked with succulent chicken piece, potato & boiled egg in dum style.")
                        .price(299.0)
                        .imageUrl("https://images.unsplash.com/photo-1633945274405-b6c8069047b0?w=500")
                        .available(true)
                        .restaurant(rest1)
                        .category(biryaniCat)
                        .build(),
                Food.builder()
                        .name("Mutton Biryani")
                        .description("Slow-cooked tender mutton pieces with fragrant ghee basmati rice & secret Mughlai spices.")
                        .price(399.0)
                        .imageUrl("https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=500")
                        .available(true)
                        .restaurant(rest1)
                        .category(biryaniCat)
                        .build(),
                Food.builder()
                        .name("Fried Rice")
                        .description("Wok-tossed Indo-Chinese fried rice with fresh chopped vegetables & aromatic seasonings.")
                        .price(249.0)
                        .imageUrl("https://images.unsplash.com/photo-1603133872878-684f208fb84b?w=500")
                        .available(true)
                        .restaurant(rest1)
                        .category(mainCourseCat)
                        .build(),
                Food.builder()
                        .name("Chicken Chaap (8 Pieces)")
                        .description("Classic Mughlai slow-cooked marinated chicken leg & thigh pieces in rich cashew gravy.")
                        .price(279.0)
                        .imageUrl("https://images.unsplash.com/photo-1603894584373-5ac82b2ae398?w=500")
                        .available(true)
                        .restaurant(rest1)
                        .category(mainCourseCat)
                        .build(),
                Food.builder()
                        .name("Veg Thali")
                        .description("Complete wholesome platter with paneer butter masala, dal makhani, naan, rice & salad.")
                        .price(299.0)
                        .imageUrl("https://images.unsplash.com/photo-1610192244261-3f33de3f55e4?w=500")
                        .available(true)
                        .restaurant(rest1)
                        .category(thaliCat)
                        .build(),
                Food.builder()
                        .name("Mineral Water (1L)")
                        .description("Packaged chilled purified mineral drinking water bottle.")
                        .price(20.0)
                        .imageUrl("https://images.unsplash.com/photo-1548839140-29a749e1bc4e?w=500")
                        .available(true)
                        .restaurant(rest1)
                        .category(beverageCat)
                        .build(),
                Food.builder()
                        .name("Chocolate Milkshake")
                        .description("Creamy chilled chocolate milkshake topped with cocoa powder & chocolate drizzle.")
                        .price(100.0)
                        .imageUrl("https://images.unsplash.com/photo-1572490122747-3968b75cc699?w=500")
                        .available(true)
                        .restaurant(rest1)
                        .category(beverageCat)
                        .build(),
                Food.builder()
                        .name("Paneer Butter Masala")
                        .description("Soft cottage cheese cubes simmered in a creamy spiced tomato-butter gravy.")
                        .price(249.0)
                        .imageUrl("https://images.unsplash.com/photo-1631452180519-c014fe946bc7?w=500")
                        .available(true)
                        .restaurant(rest1)
                        .category(mainCourseCat)
                        .build()
        );

        foodRepository.saveAll(foods);
    }
}
