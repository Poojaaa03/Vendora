import { useEffect, useState } from "react";
import api from "../services/api";

function Cart() {

  const [cart, setCart] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    fetchCart();
  }, []);

  const fetchCart = async () => {
    try {
      const response = await api.get("/cart");
      setCart(response.data);
    } catch (error) {
      setError(
        error.response?.data?.message ||
        "Failed to load cart"
      );
    }
  };

  // Update cart item quantity
  const updateQuantity = async (cartItemId, quantity) => {

    try {

      await api.put(`/cart/${cartItemId}?quantity=${quantity}`);

      fetchCart();

    } catch (error) {

      alert(
        error.response?.data?.message ||
        "Failed to update quantity"
      );
    }
  };

  // Remove item from cart
  const removeItem = async (cartItemId) => {

    try {

      await api.delete(`/cart/${cartItemId}`);

      fetchCart();

    } catch (error) {

      alert(
        error.response?.data?.message ||
        "Failed to remove item"
      );
    }
  };

  // Checkout
  const checkout = async () => {

    try {

      const response = await api.post("/orders/checkout");

      alert(
        `Order placed successfully! Order ID: ${response.data.id}`
      );

      fetchCart();

    } catch (error) {

      alert(
        error.response?.data?.message ||
        "Checkout failed"
      );
    }
  };

  return (
    <div className="page-container">

      <h1>Your Cart</h1>

      {error && <p>{error}</p>}

      {!cart && !error && (
        <p>Loading cart...</p>
      )}

      {cart && cart.items.length === 0 && (
        <p>Your cart is empty.</p>
      )}

      {cart && cart.items.length > 0 && (

        <div>

          {cart.items.map((item) => (

            <div
              className="cart-item"
              key={item.id}
            >

              <h3>{item.productName}</h3>

              <p>
                Price: ₹{item.price}
              </p>

              <div className="quantity-controls">

                <button
                  onClick={() =>
                    updateQuantity(
                      item.id,
                      item.quantity - 1
                    )
                  }
                  disabled={item.quantity <= 1}
                >
                  -
                </button>

                <span>
                  {item.quantity}
                </span>

                <button
                  onClick={() =>
                    updateQuantity(
                      item.id,
                      item.quantity + 1
                    )
                  }
                >
                  +
                </button>

              </div>

              <p>
                Subtotal: ₹{item.subtotal}
              </p>

              <button
                className="remove-button"
                onClick={() => removeItem(item.id)}
              >
                Remove
              </button>

            </div>

          ))}

          <div className="total">

            <h2>
              Total: ₹{cart.totalAmount}
            </h2>

            <button
              className="checkout-button"
              onClick={checkout}
            >
              Checkout
            </button>

          </div>

        </div>

      )}

    </div>
  );
}

export default Cart;