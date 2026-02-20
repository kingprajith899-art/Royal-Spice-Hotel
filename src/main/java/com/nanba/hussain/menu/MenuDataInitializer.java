package com.nanba.hussain.menu;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class MenuDataInitializer implements CommandLineRunner {

    private final DishRepository dishRepository;

    public MenuDataInitializer(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }

    @Override
    public void run(String... args) {
        saveIfMissing("🥬 Veg", "🥞 Dosa", 30);
        saveIfMissing("🥬 Veg", "🍚 Idly", 25);
        saveIfMissing("🥬 Veg", "🍛 Meals", 80);
        saveIfMissing("🥬 Veg", "🥣 Rasam", 35);

        saveIfMissing("🍗 Non-Veg", "🍗 Chicken Biryani", 150);
        saveIfMissing("🍗 Non-Veg", "🥩 Mutton Biryani", 220);
        saveIfMissing("🍗 Non-Veg", "🥚 Egg Parotta", 90);
        saveIfMissing("🍗 Non-Veg", "🐟 Fish Gravy", 160);
        saveIfMissing("🍗 Non-Veg", "🐟 Fish Fry", 180);

        saveIfMissing("🍹 Juices and Drinks", "🍊 Orange Juice", 50);
        saveIfMissing("🍹 Juices and Drinks", "🍋 Lemon Juice", 40);
        saveIfMissing("🍹 Juices and Drinks", "🍍 Pineapple Juice", 70);
    }

    private void saveIfMissing(String category, String name, int price) {
        if (!dishRepository.existsByCategoryAndName(category, name)) {
            dishRepository.save(new Dish(category, name, price, true));
        }
    }
}
