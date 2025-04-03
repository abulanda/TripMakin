import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";

const UserList = ({ refresh }) => {
  const [users, setUsers] = useState([]);

  const fetchUsers = () => {
    fetch("/api/users")
      .then((res) => res.json())
      .then((data) => setUsers(data))
      .catch((err) => console.error("Błąd:", err));
  };

  useEffect(() => {
    fetchUsers();
  }, [refresh]);

  return (
    <div>
      <h2>Lista użytkowników</h2>
      <button onClick={fetchUsers}>Odśwież</button>
      <ul>
        {users.map((user) => (
          <li key={user.userId}>
            <Link to={`/users/${user.userId}`}>
              {user.firstName} {user.lastName} ({user.email})
            </Link>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default UserList;
