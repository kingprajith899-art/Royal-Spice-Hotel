package com.nanba.hussain.order;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "food_orders")
public class FoodOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2000)
    private String itemsSummary;

    @Column(nullable = false)
    private Integer itemCount;

    @Column(nullable = false)
    private Integer totalAmount;

    @Column(nullable = false)
    private LocalDateTime orderedAt;

    public FoodOrder() {
    }

    public FoodOrder(String itemsSummary, Integer itemCount, Integer totalAmount) {
        this.itemsSummary = itemsSummary;
        this.itemCount = itemCount;
        this.totalAmount = totalAmount;
        this.orderedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getItemsSummary() {
        return itemsSummary;
    }

    public void setItemsSummary(String itemsSummary) {
        this.itemsSummary = itemsSummary;
    }

    public Integer getItemCount() {
        return itemCount;
    }

    public void setItemCount(Integer itemCount) {
        this.itemCount = itemCount;
    }

    public Integer getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getOrderedAt() {
        return orderedAt;
    }

    public void setOrderedAt(LocalDateTime orderedAt) {
        this.orderedAt = orderedAt;
    }
}
