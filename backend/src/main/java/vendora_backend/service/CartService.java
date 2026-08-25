package vendora_backend.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import vendora_backend.dto.AddToCartRequest;
import vendora_backend.dto.CartItemResponse;
import vendora_backend.dto.CartResponse;
import vendora_backend.model.Cart;
import vendora_backend.model.CartItem;
import vendora_backend.model.Product;
import vendora_backend.model.User;
import vendora_backend.repository.CartItemRepository;
import vendora_backend.repository.CartRepository;
import vendora_backend.repository.ProductRepository;
import vendora_backend.repository.UserRepository;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            ProductRepository productRepository,
            UserRepository userRepository) {

        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public CartResponse addToCart(
            String email,
            AddToCartRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Product product = productRepository
                .findById(request.getProductId())
                .orElseThrow(() ->
                        new RuntimeException("Product not found"));

        if (product.getStock() < request.getQuantity()) {
            throw new RuntimeException(
                    "Insufficient stock for product: "
                            + product.getName());
        }

        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() ->
                        cartRepository.save(new Cart(user)));

        CartItem cartItem = cartItemRepository
                .findByCartAndProduct(cart, product)
                .orElse(null);

        if (cartItem != null) {

            int newQuantity =
                    cartItem.getQuantity()
                            + request.getQuantity();

            if (newQuantity > product.getStock()) {
                throw new RuntimeException(
                        "Insufficient stock for product: "
                                + product.getName());
            }

            cartItem.setQuantity(newQuantity);

        } else {

            cartItem = new CartItem(
                    cart,
                    product,
                    request.getQuantity(),
                    product.getPrice()
            );
        }

        cartItemRepository.save(cartItem);

        return buildCartResponse(cart);
    }

    public CartResponse getCart(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() ->
                        cartRepository.save(new Cart(user)));

        return buildCartResponse(cart);
    }

    private CartResponse buildCartResponse(Cart cart) {

        List<CartItemResponse> items =
                cartItemRepository.findByCart(cart)
                        .stream()
                        .map(item -> {

                            BigDecimal subtotal =
                                    item.getPrice()
                                            .multiply(
                                                    BigDecimal.valueOf(
                                                            item.getQuantity()));

                            return new CartItemResponse(
                                    item.getId(),
                                    item.getProduct().getId(),
                                    item.getProduct().getName(),
                                    item.getQuantity(),
                                    item.getPrice(),
                                    subtotal
                            );
                        })
                        .toList();

        BigDecimal totalAmount = items.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );

        return new CartResponse(
                cart.getId(),
                items,
                totalAmount
        );
    }

    public CartResponse removeFromCart(
        String email,
        Long cartItemId) {

    User user = userRepository.findByEmail(email)
            .orElseThrow(() ->
                    new RuntimeException("User not found"));

    Cart cart = cartRepository.findByUser(user)
            .orElseThrow(() ->
                    new RuntimeException("Cart not found"));

    CartItem cartItem = cartItemRepository.findById(cartItemId)
            .orElseThrow(() ->
                    new RuntimeException("Cart item not found"));

    if (!cartItem.getCart().getId().equals(cart.getId())) {
        throw new RuntimeException(
                "You are not authorized to remove this item");
    }

    cartItemRepository.delete(cartItem);

    return buildCartResponse(cart);
}

public CartResponse updateCartItem(
        String email,
        Long cartItemId,
        Integer quantity) {

    User user = userRepository.findByEmail(email)
            .orElseThrow(() ->
                    new RuntimeException("User not found"));

    Cart cart = cartRepository.findByUser(user)
            .orElseThrow(() ->
                    new RuntimeException("Cart not found"));

    CartItem cartItem = cartItemRepository.findById(cartItemId)
            .orElseThrow(() ->
                    new RuntimeException("Cart item not found"));

    if (!cartItem.getCart().getId().equals(cart.getId())) {
        throw new RuntimeException(
                "You are not authorized to update this item");
    }

    if (quantity == null || quantity <= 0) {
        throw new RuntimeException(
                "Quantity must be greater than zero");
    }

    Product product = cartItem.getProduct();

    if (quantity > product.getStock()) {
        throw new RuntimeException(
                "Insufficient stock for product: "
                        + product.getName());
    }

    cartItem.setQuantity(quantity);

    cartItemRepository.save(cartItem);

    return buildCartResponse(cart);
}

public CartResponse clearCart(String email) {

    User user = userRepository.findByEmail(email)
            .orElseThrow(() ->
                    new RuntimeException("User not found"));

    Cart cart = cartRepository.findByUser(user)
            .orElseThrow(() ->
                    new RuntimeException("Cart not found"));

    List<CartItem> items = cartItemRepository.findByCart(cart);

    cartItemRepository.deleteAll(items);

    return buildCartResponse(cart);
}
}