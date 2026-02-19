package com.nanba.hussain;

import com.nanba.hussain.invoice.InvoicePdfService;
import com.nanba.hussain.order.FoodOrder;
import com.nanba.hussain.order.FoodOrderRepository;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Controller
public class FoodController {

    private final FoodOrderRepository foodOrderRepository;
    private final InvoicePdfService invoicePdfService;

    public FoodController(FoodOrderRepository foodOrderRepository, InvoicePdfService invoicePdfService) {
        this.foodOrderRepository = foodOrderRepository;
        this.invoicePdfService = invoicePdfService;
    }

    @GetMapping("/")
    public String home() {
        return "Home";
    }

    @GetMapping("/contact")
    public String contact() {
        return "Contact";
    }

    @GetMapping("/admin")
    public String admin() {
        return "Admin";
    }

    @PostMapping("/contact")
    public String contactSubmit() {
        return "Contact";
    }

    @GetMapping("/order")
    public String order() {
        return "Order";
    }

    @GetMapping("/orders")
    public String pastOrders(Model model) {
        model.addAttribute("orders", foodOrderRepository.findAllByOrderByOrderedAtDesc());
        return "Orders";
    }

    @PostMapping("/bill")
    public String showBill(
            @RequestParam String itemsSummary,
            @RequestParam int itemCount,
            @RequestParam int totalAmount,
            Model model) {

        if (itemsSummary == null || itemsSummary.isBlank() || totalAmount <= 0) {
            return "redirect:/order";
        }

        FoodOrder savedOrder = foodOrderRepository.save(
                new FoodOrder(itemsSummary.trim(), Math.max(itemCount, 1), totalAmount)
        );

        model.addAttribute("orderId", savedOrder.getId());
        model.addAttribute("itemsSummary", savedOrder.getItemsSummary());
        model.addAttribute("itemCount", savedOrder.getItemCount());
        model.addAttribute("totalAmount", savedOrder.getTotalAmount());
        model.addAttribute("orderedAt", savedOrder.getOrderedAt());

        return "Bill";
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
}
