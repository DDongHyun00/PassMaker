package org.example.backend.admin.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class AdminMentorApplyDto {
    private Long applyId;
    private LocalDateTime applicationDate;
    private String name;
    private String email;
    private List<String> fields;
    private List<String> experiences;
    private String status;
    private LocalDateTime processedDate;
}

