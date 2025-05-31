import React, { useState } from "react";

const AddExpenseForm = ({ tripId, onExpenseAdded, participants }) => {
  const [form, setForm] = useState({
    description: "",
    amount: "",
    category: "",
    currency: "PLN",
    date: "",
    userId: participants[0]?.user?.userId || "",
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    fetch("/api/expenses", {
      method: "POST",
      credentials: "include",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        ...form,
        amount: parseFloat(form.amount),
        trip: { tripId: parseInt(tripId) },
        user: { userId: parseInt(form.userId) },
      }),
    })
      .then((res) => {
        if (!res.ok) throw new Error("Błąd podczas dodawania wydatku");
        return res.json();
      })
      .then(() => {
        onExpenseAdded();
        setForm({
          description: "",
          amount: "",
          category: "",
          currency: "PLN",
          date: "",
          userId: participants[0]?.user?.userId || "",
        });
      })
      .catch((err) => alert(err.message));
  };

  return (
    <form onSubmit={handleSubmit}>
      <input
        type="text"
        name="description"
        value={form.description}
        onChange={handleChange}
        placeholder="Opis"
        required
      />
      <input
        type="number"
        name="amount"
        value={form.amount}
        onChange={handleChange}
        placeholder="Kwota"
        required
      />
      <input
        type="text"
        name="category"
        value={form.category}
        onChange={handleChange}
        placeholder="Kategoria"
        required
      />
      <input
        type="text"
        name="currency"
        value={form.currency}
        onChange={handleChange}
        placeholder="Waluta"
        required
      />
      <input
        type="date"
        name="date"
        value={form.date}
        onChange={handleChange}
        required
      />
      <select name="userId" value={form.userId} onChange={handleChange} required>
        {participants.map((p) => (
          <option key={p.user?.userId} value={p.user?.userId}>
            {p.user?.firstName} {p.user?.lastName}
          </option>
        ))}
      </select>
      <button type="submit">Dodaj wydatek</button>
    </form>
  );
};

export default AddExpenseForm;