import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import App from "./App";
import UserDetails from "./components/UserDetails";
import TripDetails from "./components/TripDetails";
import "./index.css";

ReactDOM.createRoot(document.getElementById("root")).render(
  <React.StrictMode>
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<App />} />
        <Route path="/users/:id" element={<UserDetails />} />
        <Route path="/trips/:id" element={<TripDetails />} />
      </Routes>
    </BrowserRouter>
  </React.StrictMode>
);