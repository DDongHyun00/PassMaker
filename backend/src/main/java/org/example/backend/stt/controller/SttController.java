package org.example.backend.stt.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.room.domain.MentoringRoom;
import org.example.backend.stt.dto.MentoringRecordDto;
import org.example.backend.stt.entity.MentoringRecord;
import org.example.backend.stt.repository.MentoringRecordRepository;
import org.example.backend.stt.service.GptSummaryService;
import org.example.backend.stt.service.SttService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stt")
@RequiredArgsConstructor
public class SttController {

  private final SttService sttService;
  private final MentoringRecordRepository mentoringRecordRepository;
  private final GptSummaryService gptSummaryService;

  /**
   * 음성 파일 업로드 + Whisper STT 처리
   */
  @PostMapping("/upload-audio")
  public ResponseEntity<MentoringRecordDto> uploadAudio(
          @RequestParam("roomId") Long roomId,
          @RequestParam("partIndex") int partIndex,
          @RequestPart("audioFile") MultipartFile audioFile

  ) {
    try {
      System.out.println("== 업로드 요청 도달 ==");
      System.out.println("roomId: " + roomId);
      System.out.println("partIndex: " + partIndex);
      System.out.println("파일명: " + audioFile.getOriginalFilename());

      // partIndex를 함께 전달하도록 서비스 호출 시그니처 변경
      MentoringRecordDto response = sttService.transcribeAudio(roomId, partIndex, audioFile);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.badRequest().build();
    }
  }

  @PostMapping("/summarize")
  public ResponseEntity<?> summarizeText(@RequestParam("roomId") Long roomId) {
    try {
      // 1. roomId 기준으로 모든 STT 레코드 가져오기 (정렬 포함)
      List<MentoringRecord> records = mentoringRecordRepository
              .findAllByRoom_RoomIdOrderByRecordId(roomId);

      if (records.isEmpty()) {
        return ResponseEntity.badRequest().body("해당 roomId에 대한 STT 기록이 없습니다");
      }

      // 2. sttText 이어붙이기 (null 제외)
      String mergedStt = records.stream()
              .map(MentoringRecord::getSttText)
              .filter(Objects::nonNull)
              .collect(Collectors.joining("\n"));

      // 3. GPT 요약 실행
      String summary = gptSummaryService.summarize(mergedStt);

      // 4. 요약용 MentoringRecord 생성 (summary만 있음, partIndex=null)
      MentoringRoom room = records.get(0).getRoom();  // 기존 STT에서 room 꺼냄

      MentoringRecord summaryRecord = MentoringRecord.builder()
              .room(room)
              .sttText(null)
              .summaryText(summary)
              .partIndex(null)
              .build();

      mentoringRecordRepository.save(summaryRecord);

      return ResponseEntity.ok("요약 완료");
    } catch (Exception e) {
      return ResponseEntity.status(500).body("요약 실패: " + e.getMessage());
    }
  }

  /**
   * ✅ 요약 결과 조회 API
   */
  @GetMapping("/summary")
  public ResponseEntity<?> getSummary(@RequestParam("roomId") Long roomId) {
    try {
      MentoringRecord summary = mentoringRecordRepository
              .findByRoom_RoomIdAndPartIndexIsNull(roomId)
              .orElseThrow(() -> new IllegalArgumentException("요약 결과가 존재하지 않습니다."));

      return ResponseEntity.ok(summary.getSummaryText());
    } catch (Exception e) {
      return ResponseEntity.status(500).body("요약 조회 실패: " + e.getMessage());
    }
  }

  @GetMapping("/summary/download")
  public ResponseEntity<?> downloadSummaryAsTxt(@RequestParam("roomId") Long roomId) {
    try {
      MentoringRecord summary = mentoringRecordRepository
              .findByRoom_RoomIdAndPartIndexIsNull(roomId)
              .orElseThrow(() -> new IllegalArgumentException("요약 결과가 존재하지 않습니다."));

      String summaryText = summary.getSummaryText();
      String fileName = "summary_room_" + roomId + ".txt";

      byte[] content = summaryText.getBytes(StandardCharsets.UTF_8);

      return ResponseEntity.ok()
              .header("Content-Disposition", "attachment; filename=" + fileName)
              .header("Content-Type", "text/plain; charset=UTF-8")
              .body(content);
    } catch (Exception e) {
      return ResponseEntity.status(500).body("요약 다운로드 실패: " + e.getMessage());
    }
  }

}
/* 주의: @RequestParam("partIndex") 를 추가했으니, 프론트에서 FormData에 partIndex 필드를 반드시 담아야 합니다. */
