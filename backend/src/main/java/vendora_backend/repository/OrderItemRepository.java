package vendora_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import vendora_backend.model.Order;
import vendora_backend.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrder(Order order);
}