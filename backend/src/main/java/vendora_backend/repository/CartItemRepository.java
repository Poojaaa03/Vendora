package vendora_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import vendora_backend.model.Cart;
import vendora_backend.model.CartItem;
import vendora_backend.model.Product;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByCart(Cart cart);

    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
}