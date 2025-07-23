package org.example.backend.stt.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.room.domain.MentoringRoom;
import org.example.backend.stt.dto.MentoringRecordDto;
import org.example.backend.stt.dto.SummaryListDto;
import org.example.backend.stt.entity.MentoringRecord;
import org.example.backend.stt.repository.MentoringRecordRepository;
import org.example.backend.stt.service.GptSummaryService;
import org.example.backend.stt.service.SttService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stt")
@RequiredArgsConstructor
@Slf4j
public class SttController {

  private final SttService sttService;
  private final GptSummaryService gptSummaryService;
  private final MentoringRecordRepository mentoringRecordRepository;

  /**
   * 1) 오디오 업로드 → Whisper로 STT 변환 → DB에 저장
   */
  @PostMapping("/upload-audio")
  public ResponseEntity<MentoringRecordDto> uploadAudio(
          @RequestParam("roomId") Long roomId,
          @RequestParam("partIndex") int partIndex,
          @RequestParam("audioFile") MultipartFile audioFile
  ) {
    // ① 들어오는 파일 정보 로그로 확인
    log.info("▶️ uploadAudio called: roomId={}, partIndex={}", roomId, partIndex);
    log.info("▶️ audioFile Received → name='{}', size={} bytes, contentType='{}'",
            audioFile.getOriginalFilename(),
            audioFile.getSize(),
            audioFile.getContentType() );
    try {
      MentoringRecordDto dto = sttService.transcribeAudio(roomId, partIndex, audioFile);
      return ResponseEntity.ok(dto);
    } catch (IOException e) {
      log.error("[STT 변환 실패]", e);
      log.error("[STT 변환 실패] roomId={}, partIndex={}", roomId, partIndex, e);
      // 오류 시에는 빈 body로 500 반환
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  /**
   * 2) 전체 STT 조각 합치고 GPT로 요약 → DB에 요약 레코드 저장
   */
  @PostMapping("/summarize")
  public ResponseEntity<String> summarizeText(@RequestParam("roomId") Long roomId) {
    // 2-1) 해당 방의 모든 STT 레코드 조회
    List<MentoringRecord> records = mentoringRecordRepository
            .findAllByRoom_RoomIdOrderByRecordId(roomId);

    if (records.isEmpty()) {
      return ResponseEntity.badRequest().body("해당 roomId에 STT 기록이 없습니다.");
    }

    // 2-2) 텍스트 합치기
    String merged = records.stream()
            .map(MentoringRecord::getSttText)
            .filter(Objects::nonNull)
            .collect(Collectors.joining("\n"));

    // 2-3) GPT 요약
    String summary = gptSummaryService.summarize(merged);

    // 2-4) 요약 전용 레코드 생성 & 저장
    MentoringRoom room = records.get(0).getRoom();
    MentoringRecord summaryRecord = MentoringRecord.builder()
            .room(room)
            .sttText(null)
            .summaryText(summary)
            .partIndex(null)
            .build();
    mentoringRecordRepository.save(summaryRecord);

    return ResponseEntity.ok("요약 생성 완료");
  }

  /**
   * 3) 특정 방의 요약 단일 조회
   */
  @GetMapping("/summary")
  public ResponseEntity<String> getSummary(@RequestParam("roomId") Long roomId) {
    MentoringRecord summary = mentoringRecordRepository
            .findByRoom_RoomIdAndPartIndexIsNull(roomId)
            .orElseThrow(() -> new IllegalArgumentException("요약 결과가 없습니다."));
    return ResponseEntity.ok(summary.getSummaryText());
  }

  /**
   * 4) 특정 방의 요약 텍스트 파일 다운로드
   */
  @GetMapping("/summary/download")
  public ResponseEntity<byte[]> downloadSummaryAsTxt(@RequestParam("roomId") Long roomId) {
    MentoringRecord summary = mentoringRecordRepository
            .findByRoom_RoomIdAndPartIndexIsNull(roomId)
            .orElseThrow(() -> new IllegalArgumentException("요약 결과가 없습니다."));

    String text = summary.getSummaryText();
    String filename = "summary_room_" + roomId + ".txt";
    byte[] content = text.getBytes(StandardCharsets.UTF_8);

    return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=" + filename)
            .header("Content-Type", "text/plain; charset=UTF-8")
            .body(content);
  }

  /**
   * 5) 모든 방의 요약 목록 조회
   */
  @GetMapping("/summaries")
  public ResponseEntity<List<SummaryListDto>> listSummaries() {
    List<SummaryListDto> list = mentoringRecordRepository
            .findAllByPartIndexIsNull()
            .stream()
            .map(r -> {
              var room = r.getRoom();
              var mentor = room.getMentor();
              var mentee = room.getUser();

              String preview = r.getSummaryText() != null
                      ? r.getSummaryText().substring(0, Math.min(100, r.getSummaryText().length()))
                      : "";

              String status = r.getSummaryText() != null ? "completed" : "pending";

              return new SummaryListDto(
                      room.getRoomId(),
                      mentor.getMentoringTitle(),
                      mentor.getUser().getNickname(),
                      mentee.getNickname(),
                      r.getCreatedAt(),
                      status,
                      preview
              );
            })
            .collect(Collectors.toList());

    return ResponseEntity.ok(list);
  }

}