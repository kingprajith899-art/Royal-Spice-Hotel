package com.nanba.hussain.api;

import com.nanba.hussain.invoice.InvoicePdfService;
import com.nanba.hussain.menu.Dish;
import com.nanba.hussain.menu.DishRepository;
import com.nanba.hussain.order.FoodOrder;
import com.nanba.hussain.order.FoodOrderRepository;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api")
public class OrderApiController {

    private final FoodOrderRepository foodOrderRepository;
    private final DishRepository dishRepository;
    private final InvoicePdfService invoicePdfService;

    public OrderApiController(
            FoodOrderRepository foodOrderRepository,
            DishRepository dishRepository,
            InvoicePdfService invoicePdfService) {
        this.foodOrderRepository = foodOrderRepository;
        this.dishRepository = dishRepository;
        this.invoicePdfService = invoicePdfService;
    }

    @GetMapping("/health")
    public ApiMessage health() {
        return new ApiMessage("ok");
    }

    @GetMapping("/orders")
    public List<OrderResponse> listOrders() {
        return foodOrderRepository.findAllByOrderByOrderedAtDesc()
                .stream()
                .map(OrderResponse::from)
                .toList();
    }

    @GetMapping("/dishes")
    public List<DishResponse> listActiveDishes() {
        return dishRepository.findAllByActiveTrueOrderByCategoryAscNameAsc()
                .stream()
                .map(DishResponse::from)
                .toList();
    }

    @PostMapping("/orders")
    public OrderResponse createOrder(@RequestBody CreateOrderRequest request) {
        if (request == null
                || request.itemsSummary() == null
                || request.itemsSummary().isBlank()
                || request.itemCount() <= 0
                || request.totalAmount() <= 0) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid order payload");
        }

        FoodOrder saved = foodOrderRepository.save(
                new FoodOrder(request.itemsSummary().trim(), request.itemCount(), request.totalAmount())
        );

        return OrderResponse.from(saved);
    }

    @GetMapping("/orders/{id}/invoice.pdf")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Long id) {
        FoodOrder order = foodOrderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Order not found"));

        byte[] pdfBytes = invoicePdfService.generateInvoice(order);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("invoice-order-" + order.getId() + ".pdf")
                .build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    public record CreateOrderRequest(String itemsSummary, int itemCount, int totalAmount) {
    }

    public record OrderResponse(
            Long id,
            String itemsSummary,
            Integer itemCount,
            Integer totalAmount,
            LocalDateTime orderedAt) {
        static OrderResponse from(FoodOrder order) {
            return new OrderResponse(
                    order.getId(),
                    order.getItemsSummary(),
                    order.getItemCount(),
                    order.getTotalAmount(),
                    order.getOrderedAt()
            );
        }
    }

    public record ApiMessage(String message) {
    }

    public record DishResponse(Long id, String category, String name, Integer price) {
        static DishResponse from(Dish dish) {
            return new DishResponse(dish.getId(), dish.getCategory(), dish.getName(), dish.getPrice());
        }
    }
}
