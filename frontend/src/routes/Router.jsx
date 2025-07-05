import { BrowserRouter, Routes, Route } from "react-router-dom";
import TitlePage from "../pages/TitlePage";
import SignupPage from "../pages/SignupPage";
import LoginPage from "../pages/LoginPage";
import MainPage from "../pages/MainPage.jsx";
import React from "react";

const Router = () => {
  return (
    <Routes>
      <Route path="/" element={<TitlePage />} />
      <Route path="/main" element={<MainPage />} />
      <Route path="/signup" element={<SignupPage />} />
      <Route path="/login" element={<LoginPage />} />
    </Routes>
  );
};

export default Router;
