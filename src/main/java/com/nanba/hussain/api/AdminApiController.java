package com.nanba.hussain.api;

import com.nanba.hussain.admin.AdminAuthService;
import com.nanba.hussain.menu.Dish;
import com.nanba.hussain.menu.DishRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@RequestMapping("/api/admin")
public class AdminApiController {

    private final AdminAuthService adminAuthService;
    private final DishRepository dishRepository;

    public AdminApiController(AdminAuthService adminAuthService, DishRepository dishRepository) {
        this.adminAuthService = adminAuthService;
        this.dishRepository = dishRepository;
    }

    @PostMapping("/login")
    public AdminLoginResponse login(@RequestBody AdminLoginRequest request) {
        if (request == null || request.username() == null || request.password() == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid payload");
        }

        String token = adminAuthService.login(request.username(), request.password());
        if (token == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "Invalid admin credentials");
        }
        return new AdminLoginResponse(token, "Admin");
    }

    @PostMapping("/logout")
    public ApiMessage logout(@RequestHeader(value = "X-Admin-Token", required = false) String adminToken) {
        if (adminToken != null) {
            adminAuthService.logout(adminToken);
        }
        return new ApiMessage("logged_out");
    }

    @GetMapping("/me")
    public ApiMessage me(@RequestHeader(value = "X-Admin-Token", required = false) String adminToken) {
        requireAdmin(adminToken);
        return new ApiMessage("authenticated");
    }

    @GetMapping("/dishes")
    public List<DishResponse> allDishes(@RequestHeader(value = "X-Admin-Token", required = false) String adminToken) {
        requireAdmin(adminToken);
        return dishRepository.findAllByOrderByCategoryAscNameAsc()
                .stream()
                .map(DishResponse::from)
                .toList();
    }

    @PostMapping("/dishes")
    public DishResponse createDish(
            @RequestHeader(value = "X-Admin-Token", required = false) String adminToken,
            @RequestBody DishRequest request) {
        requireAdmin(adminToken);
        validateDishRequest(request);

        Dish saved = dishRepository.save(new Dish(
                request.category().trim(),
                request.name().trim(),
                request.price(),
                request.active() == null || request.active()
        ));

        return DishResponse.from(saved);
    }

    @PutMapping("/dishes/{id}")
    public DishResponse updateDish(
            @RequestHeader(value = "X-Admin-Token", required = false) String adminToken,
            @PathVariable Long id,
            @RequestBody DishRequest request) {
        requireAdmin(adminToken);
        validateDishRequest(request);

        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Dish not found"));

        dish.setCategory(request.category().trim());
        dish.setName(request.name().trim());
        dish.setPrice(request.price());
        dish.setActive(request.active() == null || request.active());
        dish.setUpdatedAt(LocalDateTime.now());

        return DishResponse.from(dishRepository.save(dish));
    }

    @DeleteMapping("/dishes/{id}")
    public ApiMessage disableDish(
            @RequestHeader(value = "X-Admin-Token", required = false) String adminToken,
            @PathVariable Long id) {
        requireAdmin(adminToken);
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Dish not found"));
        dish.setActive(false);
        dishRepository.save(dish);
        return new ApiMessage("dish_disabled");
    }

    private void validateDishRequest(DishRequest request) {
        if (request == null
                || request.category() == null
                || request.category().isBlank()
                || request.name() == null
                || request.name().isBlank()
                || request.price() <= 0) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid dish data");
        }
    }

    private void requireAdmin(String token) {
        if (!adminAuthService.isValid(token)) {
            throw new ResponseStatusException(UNAUTHORIZED, "Admin login required");
        }
    }

    public record AdminLoginRequest(String username, String password) {
    }

    public record AdminLoginResponse(String token, String role) {
    }

    public record DishRequest(String category, String name, int price, Boolean active) {
    }

    public record DishResponse(Long id, String category, String name, Integer price, Boolean active) {
        static DishResponse from(Dish dish) {
            return new DishResponse(dish.getId(), dish.getCategory(), dish.getName(), dish.getPrice(), dish.getActive());
        }
    }

    public record ApiMessage(String message) {
    }
}
