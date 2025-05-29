import React, { useEffect, useState } from "react";

const ScheduleList = ({ tripId }) => {
  const [schedules, setSchedules] = useState([]);
  const [showAdd, setShowAdd] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem("jwtToken");
    fetch(`/api/schedules/trip/${tripId}`, {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then((res) => res.ok ? res.json() : [])
      .then((data) => setSchedules(data));
  }, [tripId]);

  return (
    <div>
      <h3>Plan zwiedzania</h3>
      {schedules.length === 0 ? (
        <p>Brak punktów harmonogramu</p>
      ) : (
        <table>
          <thead>
            <tr>
              <th>Data</th>
              <th>Godziny</th>
              <th>Tytuł</th>
              <th>Opis</th>
              <th>Lokalizacja</th>
              <th>Typ</th>
            </tr>
          </thead>
          <tbody>
            {schedules.map((s) => (
              <tr key={s.scheduleId}>
                <td>{s.date}</td>
                <td>
                  {s.startTime} - {s.endTime}
                </td>
                <td>{s.title}</td>
                <td>{s.description}</td>
                <td>{s.location}</td>
                <td>{s.type}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
      <button onClick={() => setShowAdd(!showAdd)}>
        {showAdd ? "Anuluj" : "Dodaj punkt"}
      </button>
      {showAdd && (
        <AddScheduleForm
          tripId={tripId}
          onScheduleAdded={() => {
            setShowAdd(false);
            const token = localStorage.getItem("jwtToken");
            fetch(`/api/schedules/trip/${tripId}`, {
              headers: { Authorization: `Bearer ${token}` },
            })
              .then((res) => res.ok ? res.json() : [])
              .then((data) => setSchedules(data));
          }}
        />
      )}
    </div>
  );
};

export default ScheduleList;

import AddScheduleForm from "./AddScheduleForm";