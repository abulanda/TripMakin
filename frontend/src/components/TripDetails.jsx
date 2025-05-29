import React, { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";

const TripDetails = () => {
  const { id } = useParams();
  const [trip, setTrip] = useState(null);
  const [participants, setParticipants] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem("jwtToken");
    fetch(`/api/trips/${id}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
      .then((res) => res.json())
      .then((data) => {
        setTrip(data);
        setLoading(false);
      });

    fetch(`/api/trips/${id}/participants`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
      .then((res) => res.ok ? res.json() : [])
      .then((data) => setParticipants(data));
  }, [id]);

  if (loading || !trip) return <p>Ładowanie...</p>;

  return (
    <div>
      <h2>{trip.destination}</h2>
      <p><strong>Data:</strong> {trip.startDate} - {trip.endDate}</p>
      <p><strong>Status:</strong> {trip.status}</p>
      <p><strong>Opis:</strong> {trip.description}</p>
      <p><strong>Stworzył:</strong> {trip.createdBy?.firstName} {trip.createdBy?.lastName} ({trip.createdBy?.email})</p>
      <h3>Uczestnicy:</h3>
      <ul>
        {participants.length === 0 && <li>Brak uczestników</li>}
        {participants.map((p) => (
          <li key={p.participantId}>
            {p.user?.firstName} {p.user?.lastName} ({p.user?.email}) - {p.role}
          </li>
        ))}
      </ul>
      <Link to="/">⬅ Powrót</Link>
    </div>
  );
};

export default TripDetails;