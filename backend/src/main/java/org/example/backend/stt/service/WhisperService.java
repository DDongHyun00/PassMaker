package org.example.backend.stt.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Service
@Slf4j
public class WhisperService {

  /**
   * Whisper를 호출하여 음성을 텍스트로 변환하는 메서드
   */
  public String transcribe(MultipartFile audioFile) throws IOException {
    // 1. 임시 파일로 저장 (원본 확장자 유지)
    String originalName = audioFile.getOriginalFilename();
    String suffix = (originalName != null && originalName.contains("."))
            ? originalName.substring(originalName.lastIndexOf("."))
            : ".webm";
    File tempInput = File.createTempFile("audio-input-", suffix);
    audioFile.transferTo(tempInput);
    log.info("[Whisper] 업로드된 파일명: {}", originalName);
    log.info("[Whisper] 임시 저장 경로: {}", tempInput.getAbsolutePath());
    log.info("[Whisper] 임시 파일 존재 여부: {}", tempInput.exists());

    // 2. ffmpeg 로 WAV(PCM 16kHz mono) 변환
    File tempWav = File.createTempFile("audio-wav-", ".wav");
    ProcessBuilder convBuilder = new ProcessBuilder(
            "ffmpeg",
            "-y",                          // 덮어쓰기 허용
            "-i", tempInput.getAbsolutePath(),
            "-ar", "16000",                // 샘플링 레이트 16k
            "-ac", "1",                    // mono
            tempWav.getAbsolutePath()
    );
    convBuilder.redirectErrorStream(true);
    Process conv = convBuilder.start();
    try (BufferedReader br = new BufferedReader(new InputStreamReader(conv.getInputStream()))) {
      String line;
      while ((line = br.readLine()) != null) {
        log.debug("[Whisper][ffmpeg] {}", line);
      }
    }
    int convExit = safeWait(conv);
    if (convExit != 0) {
      throw new IOException("ffmpeg 변환 실패 - 종료 코드: " + convExit);
    }
    log.info("[Whisper] WAV 변환 완료: {}", tempWav.getAbsolutePath());

    // 3. Whisper CLI 호출
    File tempDir = tempWav.getParentFile();
    ProcessBuilder pb = new ProcessBuilder(
            "whisper",
            tempWav.getAbsolutePath(),
            "--language", "Korean",
            "--model", "base",
            "--output_format", "txt",
            "--output_dir", tempDir.getAbsolutePath()
    );
    pb.redirectErrorStream(true);
    Process proc = pb.start();
    try (BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
      String line;
      while ((line = br.readLine()) != null) {
        log.info("[Whisper] {}", line);
      }
    }
    int whisperExit = safeWait(proc);
    if (whisperExit != 0) {
      throw new IOException("Whisper 실행 실패 - 종료 코드: " + whisperExit);
    }

    // 4. 결과 TXT 읽기 (생성될 때까지 최대 5초 대기)
    String base = getBaseName(tempWav);
    File txtFile = new File(tempDir, base + ".txt");
    log.info("[Whisper] 기대되는 TXT 경로: {}", txtFile.getAbsolutePath());

    long waited = 0;
    while (!txtFile.exists() && waited < 5_000) {
      try {
        Thread.sleep(200);
      } catch (InterruptedException ignored) {}
      waited += 200;
    }
    if (!txtFile.exists()) {
      throw new FileNotFoundException("Whisper 결과 TXT 파일을 찾을 수 없습니다: " + txtFile.getAbsolutePath());
    }
    String transcript = readFileToString(txtFile);
    log.info("[Whisper] 변환된 텍스트: {}", transcript.replaceAll("\n", "\\\\n"));

    // 5. 임시파일 정리
    tempInput.delete();
    tempWav.delete();
    txtFile.delete();

    return transcript;
  }

  /** 프로세스 종료 대기 + 예외 캐치 헬퍼 */
  private int safeWait(Process p) throws IOException {
    try {
      return p.waitFor();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IOException("프로세스 실행 중 중단됨", e);
    }
  }

  private String getBaseName(File file) {
    String name = file.getName();
    int idx = name.lastIndexOf('.');
    return (idx > 0) ? name.substring(0, idx) : name;
  }

  private String readFileToString(File file) throws IOException {
    StringBuilder sb = new StringBuilder();
    try (BufferedReader r = new BufferedReader(new FileReader(file))) {
      String line;
      while ((line = r.readLine()) != null) {
        sb.append(line).append("\n");
      }
    }
    return sb.toString().trim();
  }
}


// 이 코드는 로컬에서 Whisper CLI를 사용하는 방식입니다. 사전 준비 사항은 다음과 같습니다:
//    | 항목            | 설명                                                        |
//    | -------------  | --------------------------------------------------         |
//    | Python 설치     | Whisper는 Python 패키지                                      |
//    | whisper 설치    | `pip install openai-whisper`                               |
//    | ffmpeg 설치     | 오디오 변환 처리에 필수                                        |
//    | whisper 실행 확인 | `whisper somefile.mp3 --model base` 명령어가 정상 동작해야 함 |
