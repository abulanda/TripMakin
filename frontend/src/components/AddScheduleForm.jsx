import React, { useState } from "react";

const AddScheduleForm = ({ tripId, onScheduleAdded }) => {
  const [form, setForm] = useState({
    date: "",
    startTime: "",
    endTime: "",
    title: "",
    description: "",
    location: "",
    type: "",
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const token = localStorage.getItem("jwtToken");
    const userId = localStorage.getItem("userId");
    if (!userId || isNaN(Number(userId))) {
      alert("Brak informacji o użytkowniku. Zaloguj się ponownie.");
      return;
    }

    fetch("/api/schedules", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({
        ...form,
        trip: { tripId: parseInt(tripId) },
        createdBy: { userId: Number(userId) },
      }),
    })
      .then((res) => {
        if (!res.ok) throw new Error("Błąd podczas dodawania punktu harmonogramu");
        return res.json();
      })
      .then(() => {
        onScheduleAdded();
        setForm({
          date: "",
          startTime: "",
          endTime: "",
          title: "",
          description: "",
          location: "",
          type: "",
        });
      })
      .catch((err) => alert(err.message));
  };

  return (
    <form onSubmit={handleSubmit}>
      <input type="date" name="date" value={form.date} onChange={handleChange} required />
      <input type="time" name="startTime" value={form.startTime} onChange={handleChange} required />
      <input type="time" name="endTime" value={form.endTime} onChange={handleChange} required />
      <input type="text" name="title" value={form.title} onChange={handleChange} placeholder="Tytuł" required />
      <input type="text" name="description" value={form.description} onChange={handleChange} placeholder="Opis" />
      <input type="text" name="location" value={form.location} onChange={handleChange} placeholder="Lokalizacja" />
      <input type="text" name="type" value={form.type} onChange={handleChange} placeholder="Typ" />
      <button type="submit">Dodaj punkt</button>
    </form>
  );
};

export default AddScheduleForm;