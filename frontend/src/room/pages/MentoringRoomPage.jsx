import React, { useState, useEffect } from 'react';
import MentoringHeader from '../components/MentoringHeader.jsx';
import VideoArea from '../components/VideoArea.jsx';
import MentoringPreparation from '../components/MentoringPreparation.jsx';

const MentoringRoomPage = () => {
  const [timer, setTimer] = useState(4985); // 01:23:05 in seconds
  const [cameraEnabled, setCameraEnabled] = useState(true);
  const [micEnabled, setMicEnabled] = useState(true);
  const [internetConnected, setInternetConnected] = useState(true);

  // 타이머 업데이트
  useEffect(() => {
    const interval = setInterval(() => {
      setTimer(prev => prev + 1);
    }, 1000);

    return () => clearInterval(interval);
  }, []);

  // 시간 포맷팅 (초를 HH:MM:SS로 변환)
  const formatTime = (seconds) => {
    const hours = Math.floor(seconds / 3600);
    const minutes = Math.floor((seconds % 3600) / 60);
    const secs = seconds % 60;
    return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  };

  return (
    <div className="min-h-screen bg-gray-100 flex flex-col">
      <MentoringHeader />
      
      <VideoArea 
        timer={formatTime(timer)}
        cameraEnabled={cameraEnabled}
        micEnabled={micEnabled}
      />
      
      <MentoringPreparation 
        cameraEnabled={cameraEnabled}
        setCameraEnabled={setCameraEnabled}
        micEnabled={micEnabled}
        setMicEnabled={setMicEnabled}
        internetConnected={internetConnected}
      />
    </div>
  );
};

export default MentoringRoomPage;