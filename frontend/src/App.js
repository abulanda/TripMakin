import React, { useEffect, useState } from "react";
import "./App.css"; 
function App() {
  const [message, setMessage] = useState("");

  useEffect(() => {
    fetch("/api/hello")
      .then((response) => response.text())
      .then((data) => setMessage(data))
      .catch((error) => console.error("Error:", error));
  }, []);

  return (
    <div className="container">
      <p className="hello-text">{message}</p>
    </div>
  );
}

export default App;
