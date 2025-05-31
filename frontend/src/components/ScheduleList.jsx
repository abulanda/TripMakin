import React, { useEffect, useState } from "react";
import AddScheduleForm from "./AddScheduleForm";

const ScheduleList = ({ tripId }) => {
  const [schedules, setSchedules] = useState([]);
  const [showAdd, setShowAdd] = useState(false);

  const fetchSchedules = () => {
    fetch(`/api/schedules/trip/${tripId}`, {
      credentials: "include",
    })
      .then((res) => (res.ok ? res.json() : []))
      .then((data) => setSchedules(data));
  };

  useEffect(() => {
    fetchSchedules();
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
            fetchSchedules();
          }}
        />
      )}
    </div>
  );
};

export default ScheduleList;