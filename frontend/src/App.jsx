import React, { useState, useEffect } from "react";
import LoginPage from "./components/LoginPage";
import RegisterPage from "./components/RegisterPage";
import Dashboard from "./components/Dashboard";
import "./App.css";

function App() {
  const [token, setToken] = useState(localStorage.getItem("jwtToken"));
  const [isRegistering, setIsRegistering] = useState(false);

  useEffect(() => {
    setToken(localStorage.getItem("jwtToken"));
  }, []);

  const handleLogin = (jwtToken) => {
    setToken(jwtToken);
  };

  const handleLogout = () => {
    localStorage.removeItem("jwtToken");
    localStorage.removeItem("userId");
    setToken(null);
  };

  const handleRegister = () => {
    setIsRegistering(false);
  };

  if (!token) {
    return isRegistering ? (
      <RegisterPage onRegister={handleRegister} />
    ) : (
      <LoginPage
        onLogin={handleLogin}
        onSwitchToRegister={() => setIsRegistering(true)}
      />
    );
  }

  return (
    <div className="App">
      <button
        onClick={handleLogout}
        style={{ float: "right", margin: "1rem" }}
      >
        Wyloguj
      </button>
      <Dashboard />
    </div>
  );
}

export default App;
