package org.example.backend.stt.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class AudioUploadRequestDto {

  private Long roomId; // 어떤 멘토링 룸의 음성인지
  private MultipartFile audioFile; // blob 단위 오디오 파일
}
/*설명
MultipartFile은 Spring에서 파일 업로드 시 사용하는 표준 타입입니다.
@RequestPart를 통해 multipart/form-data 요청에서 파라미터와 파일을 함께 받을 수 있습니다.*/