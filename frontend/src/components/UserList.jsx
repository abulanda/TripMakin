import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { authFetch } from "../utils/authFetch";

const UserList = ({ refresh }) => {
  const [users, setUsers] = useState([]);

  const fetchUsers = () => {
    authFetch("/api/v1/users", { credentials: "include" })
      .then((res) => (res.ok ? res.json() : []))
      .then((data) => setUsers(data))
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
