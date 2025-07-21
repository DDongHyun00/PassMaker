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
    // 1. 임시 파일로 저장 (Whisper CLI 또는 API가 파일 기반으로 동작하기 때문)
    File tempFile = File.createTempFile("audio-", ".mp3"); // mp3로 저장
    audioFile.transferTo(tempFile); // MultipartFile → 실제 파일로 변환

    // 🔧 추가: tempFile의 부모 디렉토리 가져오기 (Whisper 출력용)
    File tempDir = tempFile.getParentFile();

    // 📌 여기서 파일 존재 여부와 경로 확인 로그 추가
    log.info("[Whisper] 업로드된 파일명: {}", audioFile.getOriginalFilename());
    log.info("[Whisper] 임시 저장 경로: {}", tempFile.getAbsolutePath());
    log.info("[Whisper] 임시 파일 존재 여부: {}", tempFile.exists());

    // 2. Whisper CLI 호출 (예: whisper tempFile.getAbsolutePath() --language Korean --model base)
    ProcessBuilder processBuilder = new ProcessBuilder(
        "whisper",
        tempFile.getAbsolutePath(),
        "--language", "Korean",
        "--model", "base",
        "--output_format", "txt",
        "--output_dir", tempDir.getAbsolutePath()
    );

    processBuilder.redirectErrorStream(true);
    Process process = processBuilder.start();

    // 3. Whisper 로그 출력 확인 (디버깅용)
    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    String line;
    while ((line = reader.readLine()) != null) {
      log.info("[Whisper] {}", line);
    }

    int exitCode;
    try {
      exitCode = process.waitFor();
    } catch (InterruptedException e) {
      throw new IOException("Whisper 프로세스 실행 중 중단됨", e);
    }

    if (exitCode != 0) {
      throw new IOException("Whisper 실행 실패 - 종료 코드: " + exitCode);
    }

    // 4. 결과 파일 읽기 (whisper는 같은 디렉토리에 txt 파일 생성함)
    File txtFile = new File(tempFile.getParent(), getBaseName(tempFile) + ".txt");
    String transcript = readFileToString(txtFile);
    log.info("[Whisper] 생성된 TXT 경로: {}", txtFile.getAbsolutePath());
    log.info("[Whisper] TXT 존재 여부: {}", txtFile.exists());


    // 5. 임시파일 정리
    tempFile.delete();
    txtFile.delete();

    return transcript;
  }

  private String getBaseName(File file) {
    String name = file.getName();
    return name.substring(0, name.lastIndexOf('.')); // 확장자 제거
  }

  private String readFileToString(File file) throws IOException {
    StringBuilder sb = new StringBuilder();
    BufferedReader reader = new BufferedReader(new FileReader(file));
    String line;
    while ((line = reader.readLine()) != null) {
      sb.append(line).append("\n");
    }
    reader.close();
    return sb.toString().trim();
  }
}
//이 코드는 로컬에서 Whisper CLI를 사용하는 방식입니다. 사전 준비 사항은 다음과 같습니다:
//    | 항목            | 설명                                                        |
//    | -------------  | --------------------------------------------------         |
//    | Python 설치     | Whisper는 Python 패키지                                      |
//    | whisper 설치    | `pip install openai-whisper`                               |
//    | ffmpeg 설치     | 오디오 변환 처리에 필수                                        |
//    | whisper 실행 확인 | `whisper somefile.mp3 --model base` 명령어가 정상 동작해야 함 |
