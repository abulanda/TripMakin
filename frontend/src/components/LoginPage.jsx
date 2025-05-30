import React, { useState } from "react";
import "./LoginPage.css";

const LoginPage = ({ onLogin, onSwitchToRegister }) => {
  const [formData, setFormData] = useState({ username: "", password: "" });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    fetch("/api/auth/login", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(formData),
    })
      .then((res) => {
        if (!res.ok) throw new Error("Błąd logowania");
        return res.json();
      })
      .then((data) => {
        localStorage.setItem("jwtToken", data.token); 
        localStorage.setItem("userId", data.userId);

        const base64Url = data.token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const payload = JSON.parse(atob(base64));
        localStorage.setItem("payload", JSON.stringify(payload));

        onLogin(data.token);
      })
      .catch((err) => alert(err.message));
  };

  return (
    <div className="login-page">
            <div className="center">
                <img src="/logo.svg" alt="Logo" className="logo" />
                <div className="login-container">
                    <h1>Let's begin your journey!</h1>
                    <form onSubmit={handleSubmit}>
                    <input
                        type="text"
                        name="username"
                        value={formData.username}
                        onChange={handleChange}
                        placeholder="Email"
                        required
                    />
                    <input
                        type="password"
                        name="password"
                        value={formData.password}
                        onChange={handleChange}
                        placeholder="Password"
                        required
                    />
                    <div className="button-group">
                        <button type="button" className="secondary-button" onClick={onSwitchToRegister}>
                        Sign Up
                        </button>
                        <button type="submit" className="primary-button">
                        Log In
                        </button>
                    </div>
                    </form>
                </div>
            </div>
    </div>
  );
};

export default LoginPage;