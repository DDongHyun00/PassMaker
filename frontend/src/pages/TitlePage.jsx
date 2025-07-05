// TitlePage.jsx
import React from "react";
import { useNavigate } from "react-router-dom";
import CenterWrapper from "../styles/CenterWrapper.jsx";
import logo from '../assets/PassMakerLogo.webp';



const TitlePage = () => {
  const navigate = useNavigate();

  const handleKakaoLogin = () => {
    // 백엔드 카카오 OAuth 시작 URL로 리디렉트
    window.location.href = "http://localhost:8080/oauth/kakao";
  };

  const handleGoogleLogin = () => {
    // 백엔드 카카오 OAuth 시작 URL로 리디렉트
    window.location.href = "http://localhost:8080/oauth/google";
  };

  return (
    <CenterWrapper>
      {/* 콘텐츠 박스 */}
      <div className="text-center">
        <img src={logo} alt="PassMakerLogo" className="mx-auto w-40 mb-4" />
        <h1 className="text-3xl font-bold mb-2 text-white">PassMaker</h1>
        <p className="mb-6 text-white">환영합니다!</p>
        {/* 기본 로그인/회원가입 버튼 */}
        <div>
          <button
            onClick={() => navigate("/signup")}
            className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
          >
            회원가입
          </button>
          <button
            onClick={() => navigate("/login")}
            className="ml-4 bg-gray-600 text-white px-4 py-2 rounded hover:bg-gray-700"
          >
            로그인
          </button>
        </div>
        {/* 구분선 */}
        <div className="text-white text-sm mt-2 mb-2">또는</div>
        {/* 카카오 소셜 로그인 버튼 */}
        <div className="w-[300px] mx-auto">
          <button onClick={handleKakaoLogin} className="w-full">
            <img
              src="src/assets/kakao_Login__Button_large.png" // 경로 확인 필수!
              alt="카카오 로그인"
              className="w-full h-10 object-cover"
            />
          </button>
        </div>
        {/* 구분선 */}
        <div className="text-white text-sm mt-2 mb-2"></div>
        {/* 구글 소셜 로그인 버튼 */}
        <div className="w-[300px] mx-auto">
          <button
            onClick={handleGoogleLogin}
            className="relative w-full h-10 rounded font-bold text-[14px] bg-white text-black hover:bg-gray-100"
          >
            {/* 로고: 절대 위치로 왼쪽에 고정 */}
            <img
              src="https://developers.google.com/identity/images/g-logo.png"
              alt="Google"
              className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5"
            />

            {/* 텍스트: flex 안 쓰고 그냥 가운데 */}
            <span className="block text-center w-full leading-none ml-2 text-sm font-medium tracking-tight">
              구글 로그인
            </span>
          </button>
        </div>
      </div>
    </CenterWrapper>
  );
};

export default TitlePage;
