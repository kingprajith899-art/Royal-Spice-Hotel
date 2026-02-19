package com.nanba.hussain.order;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FoodOrderRepository extends JpaRepository<FoodOrder, Long> {
    List<FoodOrder> findAllByOrderByOrderedAtDesc();
}
