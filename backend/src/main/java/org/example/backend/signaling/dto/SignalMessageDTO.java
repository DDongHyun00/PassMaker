package org.example.backend.signaling.dto;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignalMessageDTO {
    private String type;      // 메시지 종류 (join, offer, answer, candidate, startMentoring)
    private String roomId;    // 방 ID
    private String sender;    // 보낸 사람
    private String receiver;  // 받을 사람 (1:1 연결 시)
    private String chat;      // 추후 텍스트 채팅을 위해 추가
    private Object data;      // 실제 내용 (SDP, ICE, 시작시간 등)


}
