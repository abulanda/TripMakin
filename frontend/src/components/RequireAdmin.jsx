import React from "react";
import { Navigate } from "react-router-dom";

function parseJwt(token) {
  if (!token) return null;
  try {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    return JSON.parse(atob(base64));
  } catch {
    return null;
  }
}

const RequireAdmin = ({ children }) => {
  const token = localStorage.getItem("jwtToken");
  const payload = parseJwt(token);
  if (!payload || !payload.roles || !payload.roles.includes("ROLE_ADMIN")) {
    return <Navigate to="/" replace />;
  }
  return children;
};

export default RequireAdmin;