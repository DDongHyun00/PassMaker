package org.example.backend.stt.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.stt.dto.MentoringRecordDto;
import org.example.backend.room.domain.MentoringRoom;
import org.example.backend.room.repository.MentoringRoomRepository;
import org.example.backend.stt.entity.MentoringRecord;
import org.example.backend.stt.repository.MentoringRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SttService {

  private final MentoringRoomRepository roomRepository;
  private final MentoringRecordRepository recordRepository;
  private final WhisperService whisperService;
  private final GptSummaryService gptSummaryService;

  /**
   * Whisper STT 처리 및 DB 저장 (파트별)
   */
  public MentoringRecordDto transcribeAudio(Long roomId, int partIndex, MultipartFile audioFile) throws IOException {
    // 1) room 존재 확인
    MentoringRoom room = roomRepository.findById(roomId)
            .orElseThrow(() -> new IllegalArgumentException("해당 roomId가 존재하지 않습니다: " + roomId));

    // 2) Whisper로 STT 변환
    String sttResult = whisperService.transcribe(audioFile);

    // 3) 파트별로 새 레코드 저장
    MentoringRecord record = MentoringRecord.builder()
            .room(room)
            .partIndex(partIndex)
            .sttText(sttResult)
            .summaryText(null)
            .build();
    recordRepository.save(record);

    // 4) 클라이언트에 해당 파트의 STT 반환
    return MentoringRecordDto.builder()
            .roomId(roomId)
            .sttText(sttResult)
            .build();
  }

  /**
   * 전체 STT를 이어붙여 요약 생성 (방 종료 시 사용)
   */
  public void summarize(Long roomId) {
    // 1) 파트별 순서(partIndex)로 정렬된 STT 레코드 조회
    List<MentoringRecord> records = recordRepository
            .findAllByRoom_RoomIdOrderByPartIndex(roomId);

    if (records.isEmpty()) {
      throw new IllegalArgumentException("해당 roomId에 대한 STT 기록이 없습니다.");
    }

    // 2) sttText 이어붙이기 (null 제외)
    String mergedStt = records.stream()
            .map(MentoringRecord::getSttText)
            .filter(Objects::nonNull)
            .collect(Collectors.joining("\n"));

    // 3) GPT 요약 실행
    String summary = gptSummaryService.summarize(mergedStt);

    // 4) 요약용 MentoringRecord 생성 (partIndex=null)
    MentoringRecord summaryRecord = MentoringRecord.builder()
            .room(records.get(0).getRoom())
            .sttText(null)
            .summaryText(summary)
            .partIndex(null)
            .build();

    recordRepository.save(summaryRecord);
  }
}