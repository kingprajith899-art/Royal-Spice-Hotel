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
        if (dishRepository.count() > 0) {
            return;
        }

        dishRepository.save(new Dish("🥬 Veg", "🥞 Dosa", 30, true));
        dishRepository.save(new Dish("🥬 Veg", "🍚 Idly", 25, true));
        dishRepository.save(new Dish("🥬 Veg", "🍛 Meals", 80, true));
        dishRepository.save(new Dish("🥬 Veg", "🥣 Rasam", 35, true));

        dishRepository.save(new Dish("🍗 Non-Veg", "🍗 Chicken Biryani", 150, true));
        dishRepository.save(new Dish("🍗 Non-Veg", "🥩 Mutton Biryani", 220, true));
        dishRepository.save(new Dish("🍗 Non-Veg", "🥚 Egg Parotta", 90, true));

        dishRepository.save(new Dish("🍹 Juices and Drinks", "🍊 Orange Juice", 50, true));
        dishRepository.save(new Dish("🍹 Juices and Drinks", "🍋 Lemon Juice", 40, true));
        dishRepository.save(new Dish("🍹 Juices and Drinks", "🍍 Pineapple Juice", 70, true));
    }
}
