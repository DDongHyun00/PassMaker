import { BrowserRouter, Routes, Route } from "react-router-dom";
import TitlePage from "../common/pages/TitlePage.jsx";
import SignupPage from "../auth/pages/SignupPage.jsx";
import LoginPage from "../auth/pages/LoginPage.jsx";
import MainPage from "../common/pages/MainPage.jsx";
import InterviewRoom from "../room/pages/MentoringRoomPage.jsx";
import React from "react";
import MentoringRoomPage from "../room/pages/MentoringRoomPage.jsx";

const Router = () => {
    return (
        <Routes>
            <Route path="/" element={<TitlePage />} />
            <Route path="/main" element={<MainPage />} />
            <Route path="/signup" element={<SignupPage />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/mentoringroom" element={<MentoringRoomPage />} />
        </Routes>
    );
};

export default Router;