import { useEffect, useState } from "react";
import api from "../services/api";

function Home() {

  const [products, setProducts] = useState([]);
  const [error, setError] = useState("");
  const [search, setSearch] = useState("");
  const [category, setCategory] = useState("");

  useEffect(() => {
    fetchProducts();
  }, []);

  const fetchProducts = async () => {
    try {
      const response = await api.get("/products");
      setProducts(response.data);
      setError("");
    } catch (error) {
      setError(
        error.response?.data?.message ||
        "Failed to load products"
      );
    }
  };

  const addToCart = async (productId) => {
    try {

      await api.post("/cart", {
        productId: productId,
        quantity: 1,
      });

      alert("Product added to cart");

    } catch (error) {

      alert(
        error.response?.data?.message ||
        "Failed to add product to cart"
      );
    }
  };

  const searchProducts = async () => {
    try {

      if (!search.trim()) {
        fetchProducts();
        return;
      }

      const response = await api.get(
        `/products/search?name=${encodeURIComponent(search)}`
      );

      setProducts(response.data);
      setError("");

    } catch (error) {

      setError(
        error.response?.data?.message ||
        "Failed to search products"
      );
    }
  };

  const filterByCategory = async () => {
    try {

      if (!category.trim()) {
        fetchProducts();
        return;
      }

      const response = await api.get(
        `/products/category/${encodeURIComponent(category)}`
      );

      setProducts(response.data);
      setError("");

    } catch (error) {

      setError(
        error.response?.data?.message ||
        "Failed to filter products"
      );
    }
  };

  return (
    <div className="home-container">

      <div className="home-heading">
        <h1>Vendora</h1>
        <p>Discover products you'll love</p>
      </div>

      <div className="filters-container">

  <div className="search-container">

    <input
      className="search-input"
      type="text"
      placeholder="Search products..."
      value={search}
      onChange={(e) => setSearch(e.target.value)}
      onKeyDown={(e) => {
        if (e.key === "Enter") {
          searchProducts();
        }
      }}
    />

    <button
      className="search-button"
      onClick={searchProducts}
    >
      Search
    </button>

  </div>

  <div className="category-container">

    <input
      className="category-input"
      type="text"
      placeholder="Enter category..."
      value={category}
      onChange={(e) => setCategory(e.target.value)}
      onKeyDown={(e) => {
        if (e.key === "Enter") {
          filterByCategory();
        }
      }}
    />

    <button
      className="category-button"
      onClick={filterByCategory}
    >
      Filter
    </button>

  </div>

</div>

      <div className="products-header">
        <h2>Products</h2>
      </div>

      {error && (
        <p className="error-message">
          {error}
        </p>
      )}

      {products.length === 0 && !error && (
        <p className="empty-message">
          No products available.
        </p>
      )}

      <div className="product-grid">

        {products.map((product) => (

          <div
            className="product-card"
            key={product.id}
          >
            
                        {product.imageUrl && (
              <img
                className="product-image"
                src={product.imageUrl}
                alt={product.name}
              />
            )}

            <div className="product-info">

              <h3>{product.name}</h3>

              <p className="product-description">
                {product.description}
              </p>

              <p className="product-price">
                ₹{product.price}
              </p>

              <p className="product-stock">
                Stock: {product.stock}
              </p>

            </div>

            <button
              className="add-cart-button"
              onClick={() => addToCart(product.id)}
            >
              Add to Cart
            </button>

          </div>

        ))}

      </div>

    </div>
  );
}

export default Home;