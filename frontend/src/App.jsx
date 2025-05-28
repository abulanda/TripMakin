import React, { useState } from "react";
import LoginPage from "./components/LoginPage";
import RegisterPage from "./components/RegisterPage";
import Dashboard from "./components/Dashboard";
import "./App.css";

function App() {
  const [token, setToken] = useState(null);
  const [isRegistering, setIsRegistering] = useState(false);

  const handleLogin = (jwtToken) => {
    setToken(jwtToken);
    console.log("Zalogowano z tokenem:", jwtToken);
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
      <Dashboard />
    </div>
  );
}

export default App;
