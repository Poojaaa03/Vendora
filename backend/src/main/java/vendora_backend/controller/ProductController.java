package vendora_backend.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vendora_backend.dto.ProductResponse;
import vendora_backend.model.Product;
import vendora_backend.service.ProductService;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // GET ALL PRODUCTS
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {

        List<ProductResponse> products =
                productService.getAllProducts()
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(products);
    }

    // SEARCH PRODUCTS
    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> searchProducts(
            @RequestParam String name) {

        List<ProductResponse> products =
                productService.searchByName(name)
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(products);
    }

    // FILTER BY CATEGORY
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductResponse>> filterByCategory(
            @PathVariable String category) {

        List<ProductResponse> products =
                productService.filterByCategory(category)
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(products);
    }

    // FILTER BY PRICE
    @GetMapping("/price")
    public ResponseEntity<List<ProductResponse>> filterByPrice(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {

        List<ProductResponse> products =
                productService
                        .filterByPriceRange(minPrice, maxPrice)
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(products);
    }

    // COMBINED FILTER
    @GetMapping("/filter")
    public ResponseEntity<List<ProductResponse>> filterProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {

        List<ProductResponse> products =
                productService.filterProducts(
                                name,
                                category,
                                minPrice,
                                maxPrice
                        )
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(products);
    }

    // PAGINATION
    @GetMapping("/page")
    public ResponseEntity<Page<ProductResponse>> getProductsPaginated(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<ProductResponse> products =
                productService
                        .getProductsPaginated(pageable)
                        .map(this::toResponse);

        return ResponseEntity.ok(products);
    }

    // GET PRODUCT BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(
            @PathVariable Long id) {

        Product product =
                productService.getProductById(id);

        return ResponseEntity.ok(
                toResponse(product)
        );
    }

    // CREATE PRODUCT
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody Product product) {

        Product savedProduct =
                productService.createProduct(product);

        return new ResponseEntity<>(
                toResponse(savedProduct),
                HttpStatus.CREATED
        );
    }

    // UPDATE PRODUCT
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody Product product) {

        Product updatedProduct =
                productService.updateProduct(
                        id,
                        product
                );

        return ResponseEntity.ok(
                toResponse(updatedProduct)
        );
    }

    // DELETE PRODUCT
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long id) {

        productService.deleteProduct(id);

        return ResponseEntity.noContent().build();
    }

    // CONVERT PRODUCT TO RESPONSE DTO
   private ProductResponse toResponse(Product product) {

    return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getStock(),
            product.getImageUrl()
    );
}
}