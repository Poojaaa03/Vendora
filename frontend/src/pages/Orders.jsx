import { useEffect, useState } from "react";
import api from "../services/api";

function Orders() {

  const [orders, setOrders] = useState([]);
  const [error, setError] = useState("");

  useEffect(() => {
    fetchOrders();
  }, []);

  const fetchOrders = async () => {
    try {

      const response = await api.get("/orders");

      setOrders(response.data);

    } catch (error) {

      setError(
        error.response?.data?.message ||
        "Failed to load orders"
      );
    }
  };

  // Process payment
  const processPayment = async (orderId) => {

    try {

      await api.post(`/orders/${orderId}/pay`);

      alert("Payment successful!");

      fetchOrders();

    } catch (error) {

      alert(
        error.response?.data?.message ||
        "Payment failed"
      );
    }
  };

  return (
    <div className="page-container">

      <h1>My Orders</h1>

      {error && <p>{error}</p>}

      {orders.length === 0 && !error && (
        <p>No orders found.</p>
      )}

      {orders.map((order) => (

        <div
          className="order-card"
          key={order.id}
        >

          <h3>
            Order #{order.id}
          </h3>

          <p>
            Total: ₹{order.totalAmount}
          </p>

          <p>
            Order Status: {order.status}
          </p>

          <p>
            Payment Status: {order.paymentStatus}
          </p>

          <p>
            Date: {order.createdAt}
          </p>

          {order.paymentStatus === "PENDING" && (

            <button
              className="pay-button"
              onClick={() => processPayment(order.id)}
            >
              Pay Now
            </button>

          )}

        </div>

      ))}

    </div>
  );
}

export default Orders;