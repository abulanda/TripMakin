import React, { useState } from "react";
import "./App.css";
import UserList from "./components/UserList";
import AddUserForm from "./components/AddUserForm";

function App() {
  const [refresh, setRefresh] = useState(false);

  const handleUserAdded = () => {
    setRefresh((prev) => !prev);
  };

  return (
    <div className="App">
      <h1>TripMakin</h1>
      <AddUserForm onUserAdded={handleUserAdded} />
      <UserList refresh={refresh}/>
    </div>
  );
}

export default App;
