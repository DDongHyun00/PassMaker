package org.example.backend.stt.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.room.domain.MentoringRoom;
import org.example.backend.stt.dto.MentoringRecordDto;
import org.example.backend.stt.entity.MentoringRecord;
import org.example.backend.stt.repository.MentoringRecordRepository;
//import org.example.backend.stt.service.BedrockSummaryService;
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
//  private final BedrockSummaryService bedrockSummaryService;
private final GptSummaryService gptSummaryService;


  /**
   * 음성 파일 업로드 + Whisper STT 처리
   */
  @PostMapping("/upload-audio")
  public ResponseEntity<MentoringRecordDto> uploadAudio(@RequestParam("roomId") Long roomId,
                                                        @RequestParam("audioFile") MultipartFile audioFile) {
    try {
      System.out.println("== 업로드 요청 도달 ==");
      System.out.println("roomId: " + roomId);
      System.out.println("파일명: " + audioFile.getOriginalFilename());

      MentoringRecordDto response = sttService.transcribeAudio(roomId, audioFile);
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
      // 요약된 레코드 찾기 (partIndex == null)
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

      // 파일 내용을 byte[]로 변환
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
/*주의: @RequestPart는 multipart/form-data 요청에서 파일을 받을 때 사용됩니다. 프론트에서는 FormData를 써야 합니다.*/