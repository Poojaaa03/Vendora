package vendora_backend.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import vendora_backend.model.Product;
import vendora_backend.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // GET ALL ACTIVE PRODUCTS
    public List<Product> getAllProducts() {
        return productRepository.findByActiveTrue();
    }

    // GET PRODUCT BY ID
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Product not found"));
    }

    // CREATE PRODUCT
    public Product createProduct(Product product) {
        product.setActive(true);
        return productRepository.save(product);
    }

    // UPDATE PRODUCT
    public Product updateProduct(
            Long id,
            Product updatedProduct) {

        Product existingProduct = getProductById(id);

        existingProduct.setName(updatedProduct.getName());
        existingProduct.setDescription(
                updatedProduct.getDescription());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setCategory(updatedProduct.getCategory());
        existingProduct.setImageUrl(updatedProduct.getImageUrl());
        existingProduct.setStock(updatedProduct.getStock());

        return productRepository.save(existingProduct);
    }

    // SOFT DELETE PRODUCT
    public void deleteProduct(Long id) {

        Product product = getProductById(id);

        product.setActive(false);

        productRepository.save(product);
    }

    // SEARCH BY NAME - ACTIVE PRODUCTS ONLY
    public List<Product> searchByName(String name) {
        return productRepository
                .findByNameContainingIgnoreCaseAndActiveTrue(name);
    }

    // FILTER BY CATEGORY - ACTIVE PRODUCTS ONLY
    public List<Product> filterByCategory(String category) {
        return productRepository
                .findByCategoryIgnoreCaseAndActiveTrue(category);
    }

    // FILTER BY PRICE - ACTIVE PRODUCTS ONLY
    public List<Product> filterByPriceRange(
            BigDecimal minPrice,
            BigDecimal maxPrice) {

        return productRepository
                .findByPriceBetweenAndActiveTrue(
                        minPrice,
                        maxPrice);
    }

    // COMBINED FILTER - ACTIVE PRODUCTS ONLY
    public List<Product> filterProducts(
            String name,
            String category,
            BigDecimal minPrice,
            BigDecimal maxPrice) {

        return productRepository.findByActiveTrue()
                .stream()
                .filter(product ->
                        name == null
                                || name.isBlank()
                                || product.getName()
                                        .toLowerCase()
                                        .contains(name.toLowerCase()))
                .filter(product ->
                        category == null
                                || category.isBlank()
                                || (product.getCategory() != null
                                && product.getCategory()
                                        .equalsIgnoreCase(category)))
                .filter(product ->
                        minPrice == null
                                || product.getPrice()
                                        .compareTo(minPrice) >= 0)
                .filter(product ->
                        maxPrice == null
                                || product.getPrice()
                                        .compareTo(maxPrice) <= 0)
                .toList();
    }

    // PAGINATION - ACTIVE PRODUCTS ONLY
    public Page<Product> getProductsPaginated(
            Pageable pageable) {

        return productRepository
                .findAll(pageable)
                .map(product -> product);
    }
}