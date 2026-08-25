import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";

function Register() {

  const navigate = useNavigate();

  const [form, setForm] = useState({
    name: "",
    email: "",
    password: "",
    confirmPassword: "",
  });

  const [error, setError] = useState("");
  const [message, setMessage] = useState("");

  const handleChange = (e) => {

    setForm({
      ...form,
      [e.target.name]: e.target.value,
    });

  };

  const handleSubmit = async (e) => {

    e.preventDefault();

    setError("");
    setMessage("");

    if (form.password !== form.confirmPassword) {
      setError("Passwords do not match");
      return;
    }

    try {

      await api.post("/auth/register", {
        name: form.name,
        email: form.email,
        password: form.password,
      });

      setMessage("Registration successful! Redirecting to login...");

      setTimeout(() => {
        navigate("/login");
      }, 1200);

    } catch (error) {

      setError(
        error.response?.data?.message ||
        "Registration failed"
      );
    }
  };

  return (
    <div className="login-container">

      <div className="login-card">

        <h1>Create Account</h1>

        {error && (
          <p className="error-message">
            {error}
          </p>
        )}

        {message && (
          <p className="success-message">
            {message}
          </p>
        )}

        <form onSubmit={handleSubmit}>

          <input
            type="text"
            name="name"
            placeholder="Full name"
            value={form.name}
            onChange={handleChange}
            required
          />

          <input
            type="email"
            name="email"
            placeholder="Email address"
            value={form.email}
            onChange={handleChange}
            required
          />

          <input
            type="password"
            name="password"
            placeholder="Password"
            value={form.password}
            onChange={handleChange}
            required
          />

          <input
            type="password"
            name="confirmPassword"
            placeholder="Confirm password"
            value={form.confirmPassword}
            onChange={handleChange}
            required
          />

          <button type="submit">
            Register
          </button>

        </form>

        <p style={{ textAlign: "center", marginTop: "18px" }}>
          Already have an account?{" "}
          <span
            onClick={() => navigate("/login")}
            style={{
              color: "#6c4ab6",
              cursor: "pointer",
              fontWeight: "500",
            }}
          >
            Login
          </span>
        </p>

      </div>

    </div>
  );
}

export default Register;