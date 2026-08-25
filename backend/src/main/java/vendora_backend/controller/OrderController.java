package vendora_backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import vendora_backend.dto.OrderItemResponse;
import vendora_backend.dto.OrderRequest;
import vendora_backend.dto.OrderResponse;
import vendora_backend.model.Order;
import vendora_backend.repository.OrderItemRepository;
import vendora_backend.service.OrderService;
import vendora_backend.dto.OrderStatusRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderItemRepository orderItemRepository;

    public OrderController(
            OrderService orderService,
            OrderItemRepository orderItemRepository) {

        this.orderService = orderService;
        this.orderItemRepository = orderItemRepository;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody OrderRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        Order order = orderService.createOrder(email, request);

        OrderResponse response = new OrderResponse(
                order.getId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getPaymentStatus(),
                order.getCreatedAt()
        );

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getUserOrders(
            Authentication authentication) {

        String email = authentication.getName();

        List<Order> orders = orderService.getUserOrders(email);

        List<OrderResponse> responses = orders.stream()
                .map(order -> new OrderResponse(
                        order.getId(),
                        order.getTotalAmount(),
                        order.getStatus(),
                        order.getPaymentStatus(),
                        order.getCreatedAt()
                ))
                .toList();

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();

        Order order = orderService.getOrderById(email, id);

        List<OrderItemResponse> items = orderItemRepository
                .findByOrder(order)
                .stream()
                .map(item -> new OrderItemResponse(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getPrice()
                ))
                .toList();

        OrderResponse response = new OrderResponse(
                order.getId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getPaymentStatus(),
                order.getCreatedAt(),
                items
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();

        Order order = orderService.cancelOrder(email, id);

        OrderResponse response = new OrderResponse(
                order.getId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getPaymentStatus(),
                order.getCreatedAt()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {

        List<Order> orders = orderService.getAllOrders();

        List<OrderResponse> responses = orders.stream()
                .map(order -> new OrderResponse(
                        order.getId(),
                        order.getTotalAmount(),
                        order.getStatus(),
                        order.getPaymentStatus(),
                        order.getCreatedAt()
                ))
                .toList();

        return ResponseEntity.ok(responses);
    }

    @PutMapping("/admin/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody OrderStatusRequest request) {

        Order order = orderService.updateOrderStatus(
                id,
                request.getStatus()
        );

        OrderResponse response = new OrderResponse(
                order.getId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getPaymentStatus(),
                order.getCreatedAt()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout(
            Authentication authentication) {

        String email = authentication.getName();

        Order order = orderService.checkout(email);

        OrderResponse response = new OrderResponse(
                order.getId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getPaymentStatus(),
                order.getCreatedAt()
        );

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

    @PostMapping("/{id}/pay")
public ResponseEntity<OrderResponse> processPayment(
        @PathVariable Long id,
        Authentication authentication) {

    String email = authentication.getName();

    Order order = orderService.processPayment(email, id);

    OrderResponse response = new OrderResponse(
            order.getId(),
            order.getTotalAmount(),
            order.getStatus(),
            order.getPaymentStatus(),
            order.getCreatedAt()
    );

    return ResponseEntity.ok(response);
}
}