// src/pages/MainPage.jsx
import React from "react";
import axios from "../lib/axios.js";
import CenterWrapper from "../styles/CenterWrapper.jsx";
import { useNavigate } from "react-router-dom";

const MainPage = () => {
  const navigate = useNavigate();

  const handleLogout = async () => {
    try {
      const socialType = localStorage.getItem("socialType");

      let logoutUrl = "/api/auth/logout"; // 기본: 일반 로그인
      if (socialType === "KAKAO") logoutUrl = "/oauth/kakao/logout";
      if (socialType === "GOOGLE") logoutUrl = "/oauth/google/logout";

      await axios.post(logoutUrl);
      localStorage.removeItem("socialType"); // 정리

      alert("로그아웃 되었습니다.");
      navigate("/"); // 로그인 페이지로 이동
    } catch (error) {
      console.error("로그아웃 실패:", error);
      alert("로그아웃 중 오류 발생");
    }
  };

  return (
    <CenterWrapper>
      <div className="p-8 text-center">
        <h1 className="text-3xl font-bold mb-4">메인 페이지</h1>
        <p className="text-lg mb-6">로그인 완료 메인 기능 페이지</p>
        <button
          onClick={handleLogout}
          className="bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded"
        >
          로그아웃
        </button>
      </div>
    </CenterWrapper>
  );
};

export default MainPage;
