import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";
import { useAuth } from "../context/AuthContext";

function AdminProducts() {

  const { token } = useAuth();
  const navigate = useNavigate();

  const emptyForm = {
    name: "",
    description: "",
    price: "",
    category: "",
    stock: "",
    imageUrl: "",
  };

  const [form, setForm] = useState(emptyForm);
  const [products, setProducts] = useState([]);
  const [editingId, setEditingId] = useState(null);

  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const getRole = () => {
    try {
      if (!token) {
        return null;
      }

      const payload = JSON.parse(
        atob(
          token.split(".")[1]
            .replace(/-/g, "+")
            .replace(/_/g, "/")
        )
      );

      return payload.role;

    } catch (error) {
      return null;
    }
  };

  const role = getRole();

  useEffect(() => {
    if (role === "ADMIN") {
      fetchProducts();
    }
  }, [role]);

  if (role !== "ADMIN") {
    return (
      <div className="page-container">
        <h2>Access Denied</h2>

        <p>
          You do not have permission to access this page.
        </p>

        <button
          className="checkout-button"
          onClick={() => navigate("/")}
        >
          Go Home
        </button>
      </div>
    );
  }

  const fetchProducts = async () => {
    try {

      const response = await api.get("/products");

      setProducts(response.data);

    } catch (error) {

      setError(
        error.response?.data?.message ||
        "Failed to load products"
      );
    }
  };

  const handleChange = (e) => {

    setForm({
      ...form,
      [e.target.name]: e.target.value,
    });
  };

  // ADD / UPDATE PRODUCT
  const handleSubmit = async (e) => {

    e.preventDefault();

    setMessage("");
    setError("");

    try {

      const productData = {
        name: form.name,
        description: form.description,
        price: Number(form.price),
        category: form.category,
        stock: Number(form.stock),
        imageUrl: form.imageUrl,
      };

      if (editingId) {

        await api.put(
          `/products/${editingId}`,
          productData
        );

        setMessage("Product updated successfully!");

      } else {

        await api.post(
          "/products",
          productData
        );

        setMessage("Product added successfully!");
      }

      setForm(emptyForm);
      setEditingId(null);

      fetchProducts();

    } catch (error) {

      setError(
        error.response?.data?.message ||
        "Failed to save product"
      );
    }
  };

  // EDIT PRODUCT
  const editProduct = (product) => {

    setEditingId(product.id);

    setForm({
      name: product.name || "",
      description: product.description || "",
      price: product.price || "",
      category: product.category || "",
      stock: product.stock || "",
      imageUrl: product.imageUrl || "",
    });

    setMessage("");
    setError("");

    window.scrollTo({
      top: 0,
      behavior: "smooth",
    });
  };

  // DELETE PRODUCT
  const deleteProduct = async (productId) => {

    const confirmed = window.confirm(
      "Are you sure you want to delete this product?"
    );

    if (!confirmed) {
      return;
    }

    try {

      await api.delete(
        `/products/${productId}`
      );

      setMessage("Product deleted successfully!");

      fetchProducts();

    } catch (error) {

      setError(
        error.response?.data?.message ||
        "Failed to delete product"
      );
    }
  };

  // CANCEL EDIT
  const cancelEdit = () => {

    setEditingId(null);
    setForm(emptyForm);

    setMessage("");
    setError("");
  };

  return (
    <div className="admin-container">

      <div className="admin-card">

        <h1>
          {editingId
            ? "Update Product"
            : "Add Product"}
        </h1>

        <p className="admin-subtitle">
          {editingId
            ? "Update product information"
            : "Add a new product to Vendora"}
        </p>

        {message && (
          <p className="success-message">
            {message}
          </p>
        )}

        {error && (
          <p className="error-message">
            {error}
          </p>
        )}

        <form onSubmit={handleSubmit}>

          <label>
            Product Name
          </label>

          <input
            type="text"
            name="name"
            placeholder="Enter product name"
            value={form.name}
            onChange={handleChange}
            required
          />

          <label>
            Description
          </label>

          <textarea
            name="description"
            placeholder="Enter product description"
            value={form.description}
            onChange={handleChange}
            rows="4"
          />

          <label>
            Price
          </label>

          <input
            type="number"
            name="price"
            placeholder="Enter price"
            value={form.price}
            onChange={handleChange}
            min="0.01"
            step="0.01"
            required
          />

          <label>
            Category
          </label>

          <input
            type="text"
            name="category"
            placeholder="e.g. Electronics"
            value={form.category}
            onChange={handleChange}
          />

          <label>
            Stock
          </label>

          <input
            type="number"
            name="stock"
            placeholder="Enter stock quantity"
            value={form.stock}
            onChange={handleChange}
            min="0"
            required
          />

          <label>
            Image URL
          </label>

          <input
            type="url"
            name="imageUrl"
            placeholder="https://example.com/image.jpg"
            value={form.imageUrl}
            onChange={handleChange}
          />

          {form.imageUrl && (
            <img
              className="admin-image-preview"
              src={form.imageUrl}
              alt="Product preview"
            />
          )}

          <button
            className="admin-submit-button"
            type="submit"
          >
            {editingId
              ? "Update Product"
              : "Add Product"}
          </button>

          {editingId && (
            <button
              type="button"
              className="admin-cancel-button"
              onClick={cancelEdit}
            >
              Cancel Edit
            </button>
          )}

        </form>

      </div>

      {/* PRODUCT LIST */}

      <div className="admin-products-section">

        <h2>Manage Products</h2>

        {products.length === 0 && (
          <p>No products found.</p>
        )}

        <div className="admin-product-list">

          {products.map((product) => (

            <div
              className="admin-product-card"
              key={product.id}
            >

              {product.imageUrl && (
                <img
                  src={product.imageUrl}
                  alt={product.name}
                  className="admin-product-image"
                />
              )}

              <div className="admin-product-info">

                <h3>
                  {product.name}
                </h3>

                <p>
                  {product.description}
                </p>

                <p>
                  ₹{product.price}
                </p>

                <p>
                  Category: {product.category}
                </p>

                <p>
                  Stock: {product.stock}
                </p>

              </div>

              <div className="admin-product-actions">

                <button
                  className="admin-edit-button"
                  onClick={() =>
                    editProduct(product)
                  }
                >
                  Edit
                </button>

                <button
                  className="admin-delete-button"
                  onClick={() =>
                    deleteProduct(product.id)
                  }
                >
                  Delete
                </button>

              </div>

            </div>

          ))}

        </div>

      </div>

    </div>
  );
}

export default AdminProducts;