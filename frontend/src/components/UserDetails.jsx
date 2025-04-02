import React, { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";

const UserDetails = () => {
  const { id } = useParams();
  const [user, setUser] = useState(null);

  useEffect(() => {
    fetch(`/api/users/${id}`)
      .then(response => response.json())
      .then(data => {
        console.log("Szczegóły użytkownika:", data); // 🔍
        setUser(data);
      })
      .catch(error => console.error("Błąd pobierania szczegółów:", error));
    }, [id]);

  if (!user) {
    return <p>Ładowanie...</p>;
  }

  return (
    <div>
      <h2>{user.firstName} {user.lastName}</h2>
      <p><strong>Email:</strong> {user.email}</p>
      <p><strong>Nickname:</strong> {user.nickname}</p>
      <p><strong>Telefon:</strong> {user.phoneNumber}</p>
      <Link to="/">⬅ Powrót do listy</Link>
    </div>
  );
};

export default UserDetails;
