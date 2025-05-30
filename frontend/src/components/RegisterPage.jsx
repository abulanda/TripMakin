import React, { useState } from "react";
import "./RegisterPage.css";

const RegisterPage = ({ onRegister }) => {
  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    email: "",
    password: "",
    confirmPassword: "",
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    if (formData.password !== formData.confirmPassword) {
      alert("Hasła muszą być takie same!");
      return;
    }

    fetch("/api/users", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        firstName: formData.firstName,
        lastName: formData.lastName,
        email: formData.email,
        password: formData.password,
      }),
    })
      .then((res) => {
        if (!res.ok) throw new Error("Błąd podczas rejestracji");
        return res.json();
      })
      .then((data) => {
        alert("Rejestracja zakończona sukcesem!");
        onRegister();
      })
      .catch((err) => alert(err.message));
  };

  return (
    <div className="register-page">
      <img src="/logo.svg" alt="Logo" className="register-logo" />
      <div className="register-container">
        <h1>Stwórz swoje konto!</h1>
        <form onSubmit={handleSubmit}>
          <div className="register-columns">
            <div className="register-left">
              <input
                type="text"
                name="firstName"
                value={formData.firstName}
                onChange={handleChange}
                placeholder="Imię"
                required
              />
              <input
                type="text"
                name="lastName"
                value={formData.lastName}
                onChange={handleChange}
                placeholder="Nazwisko"
                required
              />
              <input
                type="email"
                name="email"
                value={formData.email}
                onChange={handleChange}
                placeholder="Email"
                required
              />
            </div>
            <div className="register-right">
              <input
                type="password"
                name="password"
                value={formData.password}
                onChange={handleChange}
                placeholder="Hasło"
                required
              />
              <input
                type="password"
                name="confirmPassword"
                value={formData.confirmPassword}
                onChange={handleChange}
                placeholder="Potwierdź hasło"
                required
              />
              <div className="button-group">
                <button type="submit" className="primary-button">
                  Zarejestruj się
                </button>
              </div>
            </div>
          </div>
        </form>
      </div>
    </div>
  );
};

export default RegisterPage;