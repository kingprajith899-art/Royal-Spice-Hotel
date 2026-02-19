package com.nanba.hussain.menu;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DishRepository extends JpaRepository<Dish, Long> {
    List<Dish> findAllByActiveTrueOrderByCategoryAscNameAsc();
    List<Dish> findAllByOrderByCategoryAscNameAsc();
}
