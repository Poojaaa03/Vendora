package vendora_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import vendora_backend.model.Order;
import vendora_backend.model.User;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUser(User user);
}