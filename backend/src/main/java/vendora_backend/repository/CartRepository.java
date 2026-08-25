package vendora_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import vendora_backend.model.Cart;
import vendora_backend.model.User;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUser(User user);
}