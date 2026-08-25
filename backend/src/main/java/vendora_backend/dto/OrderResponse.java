package vendora_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {

    private Long id;
    private BigDecimal totalAmount;
    private String status;
    private String paymentStatus;
    private LocalDateTime createdAt;
    private List<OrderItemResponse> items;

    public OrderResponse(
            Long id,
            BigDecimal totalAmount,
            String status,
            String paymentStatus,
            LocalDateTime createdAt) {

        this.id = id;
        this.totalAmount = totalAmount;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.createdAt = createdAt;
    }

    public OrderResponse(
            Long id,
            BigDecimal totalAmount,
            String status,
            String paymentStatus,
            LocalDateTime createdAt,
            List<OrderItemResponse> items) {

        this.id = id;
        this.totalAmount = totalAmount;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.createdAt = createdAt;
        this.items = items;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<OrderItemResponse> getItems() {
        return items;
    }
}