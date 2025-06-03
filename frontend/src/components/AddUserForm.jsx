import React, { useState } from "react";
import { authFetch } from "../utils/authFetch";


const AddUserForm = ({ onUserAdded }) => {
  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    email: "",
    password: "",
    createdAt: new Date().toISOString(),
    isActive: true,
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    authFetch("/api/v1/users", {
      method: "POST",
      credentials: "include",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(formData),
    })
      .then((res) => {
        if (!res.ok) throw new Error("Błąd podczas dodawania użytkownika");
        return res.json();
      })
      .then((data) => {
        onUserAdded();
        setFormData({
          firstName: "",
          lastName: "",
          email: "",
          password: "",
          createdAt: new Date().toISOString(),
          isActive: true,
        });
      })
  };

  return (
    <form onSubmit={handleSubmit}>
      <h2>Dodaj nowego użytkownika</h2>
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
      <input
        type="password"
        name="password"
        value={formData.password}
        onChange={handleChange}
        placeholder="Password"
        required
      />
      <button type="submit">Dodaj</button>
    </form>
  );
};

export default AddUserForm;
