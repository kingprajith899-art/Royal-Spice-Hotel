package com.nanba.hussain.menu;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "dishes")
public class Dish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String category;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private Boolean active;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Dish() {
    }

    public Dish(String category, String name, Integer price, Boolean active) {
        this.category = category;
        this.name = name;
        this.price = price;
        this.active = active;
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    @PreUpdate
    public void touch() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
