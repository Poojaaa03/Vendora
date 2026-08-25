package vendora_backend.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import vendora_backend.dto.AddToCartRequest;
import vendora_backend.dto.CartResponse;
import vendora_backend.service.CartService;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping
    public ResponseEntity<CartResponse> addToCart(
            @Valid @RequestBody AddToCartRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        CartResponse response =
                cartService.addToCart(email, request);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart(
            Authentication authentication) {

        String email = authentication.getName();

        CartResponse response =
                cartService.getCart(email);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{itemId}")
public ResponseEntity<CartResponse> removeFromCart(
        @PathVariable Long itemId,
        Authentication authentication) {

    String email = authentication.getName();

    CartResponse response =
            cartService.removeFromCart(email, itemId);

    return ResponseEntity.ok(response);
}

@PutMapping("/{itemId}")
public ResponseEntity<CartResponse> updateCartItem(
        @PathVariable Long itemId,
        @RequestParam Integer quantity,
        Authentication authentication) {

    String email = authentication.getName();

    CartResponse response =
            cartService.updateCartItem(
                    email,
                    itemId,
                    quantity
            );

    return ResponseEntity.ok(response);
}

@DeleteMapping
public ResponseEntity<CartResponse> clearCart(
        Authentication authentication) {

    String email = authentication.getName();

    CartResponse response =
            cartService.clearCart(email);

    return ResponseEntity.ok(response);
}
}