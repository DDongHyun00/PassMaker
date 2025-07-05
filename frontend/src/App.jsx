import React from "react";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Router from "./routes/Router.jsx";

function App() {
  console.log(import.meta.env.VITE_API_URL);

  return (
    <BrowserRouter>
      <Router />
    </BrowserRouter>
  );
}

export default App;
