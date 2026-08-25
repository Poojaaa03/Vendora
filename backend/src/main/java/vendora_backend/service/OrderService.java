package vendora_backend.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import vendora_backend.dto.OrderItemRequest;
import vendora_backend.dto.OrderRequest;
import vendora_backend.model.Cart;
import vendora_backend.model.CartItem;
import vendora_backend.model.Order;
import vendora_backend.model.OrderItem;
import vendora_backend.model.Product;
import vendora_backend.model.User;
import vendora_backend.repository.CartItemRepository;
import vendora_backend.repository.CartRepository;
import vendora_backend.repository.OrderItemRepository;
import vendora_backend.repository.OrderRepository;
import vendora_backend.repository.ProductRepository;
import vendora_backend.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            ProductRepository productRepository,
            UserRepository userRepository,
            CartRepository cartRepository,
            CartItemRepository cartItemRepository) {

        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
    }

    @Transactional
public Order createOrder(String email, OrderRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        if (request.getItems() == null ||
                request.getItems().isEmpty()) {

            throw new RuntimeException(
                    "Order must contain at least one item");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;

        Order order = new Order();
        order.setUser(user);
        order.setStatus("PENDING");
        order.setTotalAmount(BigDecimal.ZERO);

        order = orderRepository.save(order);

        for (OrderItemRequest itemRequest : request.getItems()) {

            Product product = productRepository
                    .findById(itemRequest.getProductId())
                    .orElseThrow(() ->
                            new RuntimeException("Product not found"));

            if (itemRequest.getQuantity() == null ||
                    itemRequest.getQuantity() <= 0) {

                throw new RuntimeException(
                        "Quantity must be greater than zero");
            }

            if (product.getStock() < itemRequest.getQuantity()) {

                throw new RuntimeException(
                        "Insufficient stock for product: "
                                + product.getName());
            }

            BigDecimal itemTotal = product.getPrice()
                    .multiply(
                            BigDecimal.valueOf(
                                    itemRequest.getQuantity()));

            totalAmount = totalAmount.add(itemTotal);

            OrderItem orderItem = new OrderItem(
                    product,
                    itemRequest.getQuantity(),
                    product.getPrice()
            );

            orderItem.setOrder(order);

            orderItemRepository.save(orderItem);

            product.setStock(
                    product.getStock()
                            - itemRequest.getQuantity()
            );

            productRepository.save(product);
        }

        order.setTotalAmount(totalAmount);

        return orderRepository.save(order);
    }

    public List<Order> getUserOrders(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return orderRepository.findByUser(user);
    }

    public Order getOrderById(String email, Long orderId) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException(
                    "You are not authorized to view this order");
        }

        return order;
    }

    @Transactional
public Order cancelOrder(String email, Long orderId) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException(
                    "You are not authorized to cancel this order");
        }

        if ("CANCELLED".equals(order.getStatus())) {
            throw new RuntimeException(
                    "Order is already cancelled");
        }

        List<OrderItem> items =
                orderItemRepository.findByOrder(order);

        for (OrderItem item : items) {

            Product product = item.getProduct();

            product.setStock(
                    product.getStock() + item.getQuantity()
            );

            productRepository.save(product);
        }

        order.setStatus("CANCELLED");

        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order updateOrderStatus(
            Long orderId,
            String status) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Order not found"));

        String currentStatus = order.getStatus();
        String newStatus = status.toUpperCase();

        if (!newStatus.equals("PENDING")
                && !newStatus.equals("CONFIRMED")
                && !newStatus.equals("SHIPPED")
                && !newStatus.equals("DELIVERED")
                && !newStatus.equals("CANCELLED")) {

            throw new RuntimeException(
                    "Invalid order status");
        }

        if ("DELIVERED".equals(currentStatus)) {
            throw new RuntimeException(
                    "Delivered order cannot be updated");
        }

        if ("CANCELLED".equals(currentStatus)) {
            throw new RuntimeException(
                    "Cancelled order cannot be updated");
        }

        if ("CONFIRMED".equals(currentStatus)
                && "PENDING".equals(newStatus)) {

            throw new RuntimeException(
                    "Order cannot move back to PENDING");
        }

        if ("SHIPPED".equals(currentStatus)
                && !newStatus.equals("DELIVERED")
                && !newStatus.equals("CANCELLED")) {

            throw new RuntimeException(
                    "Shipped order can only be delivered or cancelled");
        }

        order.setStatus(newStatus);

        return orderRepository.save(order);
    }

    // CHECKOUT

    @Transactional
public Order checkout(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() ->
                        new RuntimeException("Cart not found"));

        List<CartItem> cartItems =
                cartItemRepository.findByCart(cart);

        if (cartItems.isEmpty()) {
            throw new RuntimeException(
                    "Cannot checkout an empty cart");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;

        Order order = new Order();
        order.setUser(user);
        order.setStatus("PENDING");
        order.setTotalAmount(BigDecimal.ZERO);

        order = orderRepository.save(order);

        for (CartItem cartItem : cartItems) {

            Product product = cartItem.getProduct();

            if (product.getStock() < cartItem.getQuantity()) {

                throw new RuntimeException(
                        "Insufficient stock for product: "
                                + product.getName());
            }

            BigDecimal itemTotal =
                    product.getPrice()
                            .multiply(
                                    BigDecimal.valueOf(
                                            cartItem.getQuantity()));

            totalAmount = totalAmount.add(itemTotal);

            OrderItem orderItem = new OrderItem(
                    product,
                    cartItem.getQuantity(),
                    product.getPrice()
            );

            orderItem.setOrder(order);

            orderItemRepository.save(orderItem);

            product.setStock(
                    product.getStock()
                            - cartItem.getQuantity()
            );

            productRepository.save(product);
        }

        order.setTotalAmount(totalAmount);

        order = orderRepository.save(order);

        cartItemRepository.deleteAll(cartItems);

        return order;
    }

    public Order processPayment(String email, Long orderId) {

    User user = userRepository.findByEmail(email)
            .orElseThrow(() ->
                    new RuntimeException("User not found"));

    Order order = orderRepository.findById(orderId)
            .orElseThrow(() ->
                    new RuntimeException("Order not found"));

    if (!order.getUser().getId().equals(user.getId())) {
        throw new RuntimeException(
                "You are not authorized to pay for this order");
    }

    if ("CANCELLED".equals(order.getStatus())) {
        throw new RuntimeException(
                "Cannot make payment for a cancelled order");
    }

    if ("PAID".equals(order.getPaymentStatus())) {
        throw new RuntimeException(
                "Order is already paid");
    }

    order.setPaymentStatus("PAID");
    order.setStatus("CONFIRMED");

    return orderRepository.save(order);
}
}