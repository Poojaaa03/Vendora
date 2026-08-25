import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";
import { useAuth } from "../context/AuthContext";

function Login() {

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {

    e.preventDefault();
    setError("");

    try {

      const response = await api.post("/auth/login", {
        email,
        password,
      });

      login(response.data.token);

      navigate("/");

    } catch (error) {

      setError(
        error.response?.data?.message ||
        "Invalid email or password"
      );
    }
  };

  return (
    <div className="login-container">

      <div className="login-card">

        <h1>Vendora</h1>

        <form onSubmit={handleSubmit}>

          <input
            type="email"
            placeholder="Email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />

          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />

          <button type="submit">
            Login
          </button>

        </form>

        {error && (
          <p className="error-message">
            {error}
          </p>
        )}

        <p className="register-link">
          Don't have an account?{" "}
          <span onClick={() => navigate("/register")}>
            Register
          </span>
        </p>

      </div>

    </div>
  );
}

export default Login;