import React, { useEffect, useRef, useState } from "react";
import { Rnd } from "react-rnd";

const VideoArea = ({ timer, cameraEnabled, micEnabled }) => {
    const localVideoRef = useRef(null);
    const [localStream, setLocalStream] = useState(null);

    useEffect(() => {
        const initMedia = async () => {
            try {
                const stream = await navigator.mediaDevices.getUserMedia({
                    video: true,
                    audio: true,
                });

                if (localVideoRef.current) {
                    localVideoRef.current.srcObject = stream;
                }

                setLocalStream(stream);
            } catch (err) {
                console.error("카메라/마이크 접근 실패:", err);
                alert("카메라/마이크 권한이 필요합니다.");
            }
        };

        if (cameraEnabled || micEnabled) {
            initMedia();
        }

        return () => {
            if (localStream) {
                localStream.getTracks().forEach((track) => track.stop());
            }
        };
    }, [cameraEnabled, micEnabled]);

    return (
        <div className="flex-1 bg-black relative overflow-hidden h-[calc(100vh-80px)]">
            {/* 타이머 표시 */}
            <div className="absolute top-4 right-4 text-white text-xl">{timer}</div>

            {/* 상대방 대기 UI */}
            <div className="w-full h-full flex items-start justify-center pt-40">
                {!cameraEnabled && (
                    <div className="text-gray-400 text-center">
                        <div className="w-32 h-32 bg-gray-700 rounded-full flex items-center justify-center mx-auto mb-4">
                            <svg className="w-16 h-16" fill="currentColor" viewBox="0 0 20 20">
                                <path
                                    fillRule="evenodd"
                                    d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-6-3a2 2 0 11-4 0 2 2 0 014 0zm-2 4a5 5 0 00-4.546 2.916A5.986 5.986 0 0010 16a5.986 5.986 0 004.546-2.084A5 5 0 0010 11z"
                                    clipRule="evenodd"
                                />
                            </svg>
                        </div>
                        <p className="text-lg">상대방을 기다리고 있습니다...</p>
                    </div>
                )}
            </div>

            {/* 내 영상: Rnd로 드래그+리사이즈+비율 고정 */}
            {cameraEnabled && (
                <Rnd
                    default={{
                        x: 40,
                        y: 40,
                        width: 200,
                        height: 150,
                    }}
                    bounds="parent"
                    lockAspectRatio
                    minWidth={120}
                    minHeight={90}
                    maxHeight={400}
                    style={{
                        border: "5px solid #a855f7",
                        borderRadius: "12px",
                        overflow: "hidden",
                        backgroundColor: "black",
                        position: "absolute",
                        zIndex: 10,
                    }}
                >
                    <video
                        ref={localVideoRef}
                        autoPlay
                        playsInline
                        muted
                        className="w-full h-full object-cover"
                    />
                </Rnd>
            )}
        </div>
    );
};

export default VideoArea;
