import React, { useState } from "react";
import "./AddTripForm.css";

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

  function getEmailFromToken(token) {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.sub;
    } catch {
      return null;
    }
  }

  const handleSubmit = (e) => {
    e.preventDefault();
    const token = localStorage.getItem("jwtToken");
    if (!token) {
      alert("Brak tokena JWT. Zaloguj się ponownie.");
      return;
    }
    const email = getEmailFromToken(token);
    if (!email) {
      alert("Nie można pobrać emaila z tokena.");
      return;
    }
    const tripData = {
      ...formData,
      createdBy: { email }, 
    };

    fetch("http://localhost:8081/api/trips", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify(tripData),
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