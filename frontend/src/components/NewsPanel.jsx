import React from "react";

const NewsPanel = ({ trips }) => {
  const today = new Date();
  const inProgress = trips.filter(
    (t) =>
      t.status === "IN_PROGRESS" &&
      new Date(t.startDate) <= today &&
      new Date(t.endDate) >= today
  );
  const future = trips
    .filter((t) => new Date(t.startDate) > today)
    .sort((a, b) => new Date(a.startDate) - new Date(b.startDate));
  const nextTrip = future[0];

  return (
    <div className="news-panel">
      <h3>Aktualności</h3>
      {inProgress.length > 0 && (
        <div>
          <b>Jesteś właśnie na wycieczce:</b>
          <ul>
            {inProgress.map((t) => (
              <li key={t.tripId}>
                {t.destination} - udanej podróży!
              </li>
            ))}
          </ul>
        </div>
      )}
      {nextTrip && (
        <div style={{ marginTop: "1em" }}>
          <b>Najbliższa wycieczka:</b>
          <div>
            {nextTrip.destination} (start za{" "}
            {Math.ceil(
              (new Date(nextTrip.startDate) - today) / (1000 * 60 * 60 * 24)
            )}{" "}
            dni)
          </div>
        </div>
      )}
      {inProgress.length === 0 && !nextTrip && (
        <div>Brak aktualnych lub nadchodzących wycieczek.</div>
      )}
    </div>
  );
};

export default NewsPanel;