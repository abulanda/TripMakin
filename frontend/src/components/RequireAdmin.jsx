import React from "react";
import { Navigate } from "react-router-dom";

const RequireAdmin = ({ children, payload }) => {
  if (!payload || !payload.roles || !payload.roles.includes("ROLE_ADMIN")) {
    return <Navigate to="/" replace />;
  }
  return children;
};

export default RequireAdmin;