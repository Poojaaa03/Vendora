import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

function Navbar() {

  const { token, isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();

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

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <nav>

      <Link to="/">
        <strong>Vendora</strong>
      </Link>

      {isAuthenticated && (
        <>
          <Link to="/">
            Home
          </Link>

          <Link to="/cart">
            Cart
          </Link>

          <Link to="/orders">
            My Orders
          </Link>

          {role === "ADMIN" && (
            <Link to="/admin/products">
              Admin
            </Link>
          )}

          <button onClick={handleLogout}>
            Logout
          </button>
        </>
      )}

    </nav>
  );
}

export default Navbar;