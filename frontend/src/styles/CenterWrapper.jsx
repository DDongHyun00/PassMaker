// src/components/CenterWrapper.jsx
import React from "react";

const CenterWrapper = ({ children }) => {
  return (
    <div className="flex justify-center items-center min-h-screen w-screen bg-[#242424]">
      <div className="w-full max-w-md px-4">{children}</div>
    </div>
  );
};

export default CenterWrapper;
