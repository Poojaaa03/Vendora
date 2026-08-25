package vendora_backend.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import vendora_backend.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByActiveTrue();

    List<Product> findByNameContainingIgnoreCaseAndActiveTrue(
            String name);

    List<Product> findByCategoryIgnoreCaseAndActiveTrue(
            String category);

    List<Product> findByPriceBetweenAndActiveTrue(
            BigDecimal minPrice,
            BigDecimal maxPrice);
}