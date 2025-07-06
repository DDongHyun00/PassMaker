import React from 'react';

const MentoringPreparation = ({ 
  cameraEnabled, 
  setCameraEnabled, 
  micEnabled, 
  setMicEnabled, 
  internetConnected 
}) => {
  const handleCameraToggle = () => {
    setCameraEnabled(!cameraEnabled);
  };

  const handleMicToggle = () => {
    setMicEnabled(!micEnabled);
  };

  return (
    <div className="bg-white border-t border-gray-200">
      <div className="px-4 py-4">
        <h2 className="text-lg font-bold text-gray-900 mb-4">멘토링 준비 사항</h2>
        
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          {/* 카메라 상태 */}
          <div className="flex items-start space-x-3">
            <div className="flex-shrink-0">
              <div className={`w-10 h-10 rounded-full flex items-center justify-center ${
                cameraEnabled ? 'bg-blue-100' : 'bg-gray-100'
              }`}>
                {cameraEnabled ? (
                  <svg className="w-5 h-5 text-blue-600" fill="currentColor" viewBox="0 0 20 20">
                    <path d="M2 6a2 2 0 012-2h6a2 2 0 012 2v8a2 2 0 01-2 2H4a2 2 0 01-2-2V6zM14.553 7.106A1 1 0 0014 8v4a1 1 0 00.553.894l2 1A1 1 0 0018 13V7a1 1 0 00-1.447-.894l-2 1z" />
                  </svg>
                ) : (
                  <svg className="w-5 h-5 text-gray-400" fill="currentColor" viewBox="0 0 20 20">
                    <path fillRule="evenodd" d="M3.707 2.293a1 1 0 00-1.414 1.414l14 14a1 1 0 001.414-1.414l-1.473-1.473A10.014 10.014 0 0019.542 10C18.268 5.943 14.478 3 10 3a9.958 9.958 0 00-4.512 1.074l-1.78-1.781zm4.261 4.26l1.514 1.515a2.003 2.003 0 012.45 2.45l1.514 1.514a4 4 0 00-5.478-5.478z" clipRule="evenodd" />
                  </svg>
                )}
              </div>
            </div>
            
            <div className="flex-1">
              <h3 className="text-base font-semibold text-gray-900 mb-1">카메라 상태</h3>
              <p className="text-gray-600 text-xs mb-2">
                {cameraEnabled 
                  ? '카메라가 정상적으로 작동 중입니다. 아래 버튼으로 켜고 끌 수 있습니다.'
                  : '카메라가 꺼져 있습니다. 아래 버튼으로 켜고 끌 수 있습니다.'
                }
              </p>
              <div className="flex items-center space-x-2">
                <div className={`w-2 h-2 rounded-full ${cameraEnabled ? 'bg-green-500' : 'bg-red-500'}`}></div>
                <span className={`text-xs font-medium ${cameraEnabled ? 'text-green-600' : 'text-red-600'}`}>
                  {cameraEnabled ? '카메라 켜짐' : '카메라 꺼짐'}
                </span>
              </div>
              <button 
                onClick={handleCameraToggle}
                className={`mt-2 px-3 py-1.5 rounded-lg text-xs font-medium transition-colors ${
                  cameraEnabled 
                    ? 'bg-red-100 text-red-700 hover:bg-red-200' 
                    : 'bg-blue-100 text-blue-700 hover:bg-blue-200'
                }`}
              >
                {cameraEnabled ? '카메라 끄기' : '카메라 켜기'}
              </button>
            </div>
          </div>

          {/* 마이크 상태 */}
          <div className="flex items-start space-x-3">
            <div className="flex-shrink-0">
              <div className={`w-10 h-10 rounded-full flex items-center justify-center ${
                micEnabled ? 'bg-green-100' : 'bg-gray-100'
              }`}>
                {micEnabled ? (
                  <svg className="w-5 h-5 text-green-600" fill="currentColor" viewBox="0 0 20 20">
                    <path fillRule="evenodd" d="M7 4a3 3 0 016 0v4a3 3 0 11-6 0V4zm4 10.93A7.001 7.001 0 0017 8a1 1 0 10-2 0A5 5 0 015 8a1 1 0 00-2 0 7.001 7.001 0 006 6.93V17H6a1 1 0 100 2h8a1 1 0 100-2h-3v-2.07z" clipRule="evenodd" />
                  </svg>
                ) : (
                  <svg className="w-5 h-5 text-gray-400" fill="currentColor" viewBox="0 0 20 20">
                    <path fillRule="evenodd" d="M9.383 3.076A1 1 0 0110 4v12a1 1 0 01-1.707.707L4.586 13H2a1 1 0 01-1-1V8a1 1 0 011-1h2.586l3.707-3.707a1 1 0 011.09-.217zM12.293 7.293a1 1 0 011.414 0L15 8.586l1.293-1.293a1 1 0 111.414 1.414L16.414 10l1.293 1.293a1 1 0 01-1.414 1.414L15 11.414l-1.293 1.293a1 1 0 01-1.414-1.414L13.586 10l-1.293-1.293a1 1 0 010-1.414z" clipRule="evenodd" />
                  </svg>
                )}
              </div>
            </div>
            
            <div className="flex-1">
              <h3 className="text-base font-semibold text-gray-900 mb-1">마이크 상태</h3>
              <p className="text-gray-600 text-xs mb-2">
                {micEnabled 
                  ? '마이크가 정상적으로 작동 중입니다. 아래 버튼으로 켜고 끌 수 있습니다.'
                  : '마이크가 꺼져 있습니다. 아래 버튼으로 켜고 끌 수 있습니다.'
                }
              </p>
              <div className="flex items-center space-x-2">
                <div className={`w-2 h-2 rounded-full ${micEnabled ? 'bg-green-500' : 'bg-red-500'}`}></div>
                <span className={`text-xs font-medium ${micEnabled ? 'text-green-600' : 'text-red-600'}`}>
                  {micEnabled ? '마이크 켜짐' : '마이크 꺼짐'}
                </span>
              </div>
              <button 
                onClick={handleMicToggle}
                className={`mt-2 px-3 py-1.5 rounded-lg text-xs font-medium transition-colors ${
                  micEnabled 
                    ? 'bg-red-100 text-red-700 hover:bg-red-200' 
                    : 'bg-green-100 text-green-700 hover:bg-green-200'
                }`}
              >
                {micEnabled ? '마이크 끄기' : '마이크 켜기'}
              </button>
            </div>
          </div>

          {/* 인터넷 연결 상태 */}
          <div className="flex items-start space-x-3">
            <div className="flex-shrink-0">
              <div className={`w-10 h-10 rounded-full flex items-center justify-center ${
                internetConnected ? 'bg-purple-100' : 'bg-gray-100'
              }`}>
                <svg className={`w-5 h-5 ${internetConnected ? 'text-purple-600' : 'text-gray-400'}`} fill="currentColor" viewBox="0 0 20 20">
                  <path fillRule="evenodd" d="M17.778 8.222c-4.296-4.296-11.26-4.296-15.556 0A1 1 0 01.808 6.808c5.076-5.077 13.308-5.077 18.384 0a1 1 0 01-1.414 1.414zM14.95 11.05a7 7 0 00-9.9 0 1 1 0 01-1.414-1.414 9 9 0 0112.728 0 1 1 0 01-1.414 1.414zM12.12 13.88a3 3 0 00-4.242 0 1 1 0 01-1.415-1.415 5 5 0 017.072 0 1 1 0 01-1.415 1.415zM9 16a1 1 0 011-1h.01a1 1 0 110 2H10a1 1 0 01-1-1z" clipRule="evenodd" />
                </svg>
              </div>
            </div>
            
            <div className="flex-1">
              <h3 className="text-base font-semibold text-gray-900 mb-1">인터넷 연결 상태</h3>
              <p className="text-gray-600 text-xs mb-2">
                {internetConnected 
                  ? '현재 인터넷 연결 상태를 확인하세요. 연결이 끊기면 자동으로 재시도됩니다.'
                  : '인터넷 연결이 불안정합니다. 연결 상태를 확인해주세요.'
                }
              </p>
              <div className="flex items-center space-x-2">
                <div className={`w-2 h-2 rounded-full ${internetConnected ? 'bg-green-500' : 'bg-red-500'}`}></div>
                <span className={`text-xs font-medium ${internetConnected ? 'text-green-600' : 'text-red-600'}`}>
                  {internetConnected ? '인터넷 연결됨' : '연결 끊김'}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default MentoringPreparation;