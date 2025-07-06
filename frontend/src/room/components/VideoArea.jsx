import React from 'react';

const VideoArea = ({ timer, cameraEnabled, micEnabled }) => {
    return (
        <div className="flex-1 bg-black relative overflow-hidden h-[calc(100vh-80px)]">
            {/* 메인 비디오 영역 */}
            <div className="w-full h-full flex items-start justify-center pt-40">
                {/* 비디오가 없을 때 표시할 placeholder */}
                <div className="text-gray-400 text-center">
                    <div className="w-32 h-32 bg-gray-700 rounded-full flex items-center justify-center mx-auto mb-4">
                        <svg className="w-16 h-16" fill="currentColor" viewBox="0 0 20 20">
                            <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-6-3a2 2 0 11-4 0 2 2 0 014 0zm-2 4a5 5 0 00-4.546 2.916A5.986 5.986 0 0010 16a5.986 5.986 0 004.546-2.084A5 5 0 0010 11z" clipRule="evenodd" />
                        </svg>
                    </div>
                    <p className="text-lg">상대방을 기다리고 있습니다...</p>
                </div>
            </div>

            {/* 왼쪽 상단 LIVE 표시 */}
            <div className="absolute top-4 left-4">
                <div className="flex items-center space-x-2 bg-red-500 text-white px-3 py-1 rounded-full">
                    <div className="w-2 h-2 bg-white rounded-full animate-pulse"></div>
                    <span className="font-semibold text-sm">LIVE</span>
                </div>
            </div>

            {/* 오른쪽 상단 타이머 */}
            <div className="absolute top-4 right-4">
                <div className="bg-black bg-opacity-50 text-white px-4 py-2 rounded-lg">
                    <span className="font-mono text-lg font-bold">{timer}</span>
                </div>
            </div>

            {/* 오른쪽 하단 본인 화면 */}
            <div className="absolute bottom-6 right-6">
                <div className="w-64 h-48 bg-gray-800 rounded-lg border-2 border-purple-500 overflow-hidden relative">
                    {cameraEnabled ? (
                        <div className="w-full h-full bg-gray-700 flex items-center justify-center">
                            <div className="text-center text-gray-300">
                                <div className="w-16 h-16 bg-gray-600 rounded-full flex items-center justify-center mx-auto mb-2">
                                    <span className="text-gray-300 text-lg font-medium">나</span>
                                </div>
                                <p className="text-sm">내 화면</p>
                            </div>
                        </div>
                    ) : (
                        <div className="w-full h-full bg-gray-900 flex items-center justify-center">
                            <div className="text-center text-gray-500">
                                <svg className="w-12 h-12 mx-auto mb-2" fill="currentColor" viewBox="0 0 20 20">
                                    <path fillRule="evenodd" d="M3.707 2.293a1 1 0 00-1.414 1.414l14 14a1 1 0 001.414-1.414l-1.473-1.473A10.014 10.014 0 0019.542 10C18.268 5.943 14.478 3 10 3a9.958 9.958 0 00-4.512 1.074l-1.78-1.781zm4.261 4.26l1.514 1.515a2.003 2.003 0 012.45 2.45l1.514 1.514a4 4 0 00-5.478-5.478z" clipRule="evenodd" />
                                    <path d="M12.454 16.697L9.75 13.992a4 4 0 01-3.742-3.741L2.335 6.578A9.98 9.98 0 00.458 10c1.274 4.057 5.065 7 9.542 7 .847 0 1.669-.105 2.454-.303z" />
                                </svg>
                                <p className="text-xs">카메라 끔</p>
                            </div>
                        </div>
                    )}

                    {/* 마이크 상태 표시 */}
                    <div className="absolute bottom-2 left-2">
                        <div className={`p-1 rounded-full ${micEnabled ? 'bg-green-500' : 'bg-red-500'}`}>
                            {micEnabled ? (
                                <svg className="w-4 h-4 text-white" fill="currentColor" viewBox="0 0 20 20">
                                    <path fillRule="evenodd" d="M7 4a3 3 0 016 0v4a3 3 0 11-6 0V4zm4 10.93A7.001 7.001 0 0017 8a1 1 0 10-2 0A5 5 0 015 8a1 1 0 00-2 0 7.001 7.001 0 006 6.93V17H6a1 1 0 100 2h8a1 1 0 100-2h-3v-2.07z" clipRule="evenodd" />
                                </svg>
                            ) : (
                                <svg className="w-4 h-4 text-white" fill="currentColor" viewBox="0 0 20 20">
                                    <path fillRule="evenodd" d="M9.383 3.076A1 1 0 0110 4v12a1 1 0 01-1.707.707L4.586 13H2a1 1 0 01-1-1V8a1 1 0 011-1h2.586l3.707-3.707a1 1 0 011.09-.217zM12.293 7.293a1 1 0 011.414 0L15 8.586l1.293-1.293a1 1 0 111.414 1.414L16.414 10l1.293 1.293a1 1 0 01-1.414 1.414L15 11.414l-1.293 1.293a1 1 0 01-1.414-1.414L13.586 10l-1.293-1.293a1 1 0 010-1.414z" clipRule="evenodd" />
                                </svg>
                            )}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default VideoArea;
