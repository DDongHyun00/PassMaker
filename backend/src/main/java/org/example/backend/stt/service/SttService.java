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
   * Whisper STT 처리 및 DB 저장
   */
  public MentoringRecordDto transcribeAudio(Long roomId, MultipartFile audioFile) throws IOException {
    MentoringRoom room = roomRepository.findById(roomId)
        .orElseThrow(() -> new IllegalArgumentException("해당 roomId가 존재하지 않습니다: " + roomId));

    String sttResult = whisperService.transcribe(audioFile);

    MentoringRecord record = recordRepository.findByRoom_RoomId(roomId).orElse(null);

    if (record == null) {
      record = MentoringRecord.builder()
          .room(room)
          .sttText(sttResult)
          .build();
    } else {
      String updated = record.getSttText() + "\n" + sttResult;
      record.setSttText(updated);
    }

    recordRepository.save(record);

    return MentoringRecordDto.builder()
        .roomId(roomId)
        .sttText(record.getSttText())
        .build();
  }

  /**
   * 전체 STT를 이어붙여 요약 생성 (방 종료 시 사용)
   */
  public void summarize(Long roomId) {
    List<MentoringRecord> records = recordRepository.findAllByRoom_RoomIdOrderByRecordId(roomId);

    if (records.isEmpty()) {
      throw new IllegalArgumentException("해당 roomId에 대한 STT 기록이 없습니다.");
    }

    String mergedStt = records.stream()
        .map(MentoringRecord::getSttText)
        .filter(Objects::nonNull)
        .collect(Collectors.joining("\n"));

    String summary = gptSummaryService.summarize(mergedStt);
    MentoringRoom room = records.get(0).getRoom();

    MentoringRecord summaryRecord = MentoringRecord.builder()
        .room(room)
        .sttText(null)
        .summaryText(summary)
        .partIndex(null)
        .build();

    recordRepository.save(summaryRecord);
  }
}
