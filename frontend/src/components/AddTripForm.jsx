import React, { useState } from "react";
import "./AddTripForm.css";
import { authFetch } from "../utils/authFetch";


const AddTripForm = ({ onTripAdded }) => {
  const [formData, setFormData] = useState({
    destination: "",
    startDate: "",
    endDate: "",
    description: "",
    coverPhoto: "",
    status: "PLANNED",
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const payload = JSON.parse(localStorage.getItem("payload"));
    const email = payload?.username;

    authFetch("/api/v1/trips", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      credentials: "include",
      body: JSON.stringify({
        ...formData,
        createdBy: { email },
      }),
    })
      .then((res) => {
        if (!res.ok) throw new Error("Błąd podczas dodawania wycieczki");
        return res.json();
      })
      .then((data) => {
        alert("Wycieczka została dodana!");
        onTripAdded();
        setFormData({
          destination: "",
          startDate: "",
          endDate: "",
          description: "",
          coverPhoto: "",
          status: "PLANNED",
        });
      })
      .catch((err) => alert(err.message));
  };

  return (
    <form onSubmit={handleSubmit}>
      <h2>Dodaj nową wycieczkę</h2>
      <input
        type="text"
        name="destination"
        value={formData.destination}
        onChange={handleChange}
        placeholder="Cel podróży"
        required
      />
      <input
        type="date"
        name="startDate"
        value={formData.startDate}
        onChange={handleChange}
        placeholder="Data rozpoczęcia"
        required
      />
      <input
        type="date"
        name="endDate"
        value={formData.endDate}
        onChange={handleChange}
        placeholder="Data zakończenia"
        required
      />
      <textarea
        name="description"
        value={formData.description}
        onChange={handleChange}
        placeholder="Opis wycieczki"
        rows="4"
      />
      <button type="submit">Dodaj wycieczkę</button>
    </form>
  );
};

export default AddTripForm;