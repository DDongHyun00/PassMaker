package org.example.backend.admin.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.admin.dto.AdminReportReviewDto;
import org.example.backend.admin.repository.AdminReportReviewRepository;
import org.example.backend.review.domain.ReportCategory;
import org.example.backend.review.domain.ReportStatus;
import org.example.backend.review.domain.ReviewReport;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminReportReviewService {
    private final AdminReportReviewRepository adminReportReviewRepository;

    public List<AdminReportReviewDto> getReports(String keyword, String statusFilter, String categoryFilter) {
        List<ReviewReport> reports = adminReportReviewRepository.findAllWithReviewAndUser();

        return reports.stream()
                .filter(report -> {
                    boolean matchesKeyword = keyword == null || keyword.isBlank()
                            || report.getReview().getContent().toLowerCase().contains(keyword.toLowerCase())
                            || report.getReview().getId().toString().contains(keyword)
                            || report.getReporter().getName().toLowerCase().contains(keyword.toLowerCase());

                    boolean matchesStatus = statusFilter.equals("전체 상태") || report.getStatus().name().equalsIgnoreCase(convertStatus(statusFilter));
                    boolean matchesReason = categoryFilter.equals("전체 사유") || report.getCategory().name().equalsIgnoreCase(convertCategory(categoryFilter));

                    return matchesKeyword && matchesStatus && matchesReason;
                })
                .collect(Collectors.groupingBy(ReviewReport::getReview)) // 리뷰별로 그룹핑
                .entrySet()
                .stream()
                .map(entry -> {
                    var groupedReports = entry.getValue();
                    var first = groupedReports.get(0);
                    return AdminReportReviewDto.builder()
                            .id(first.getId())
                            .reviewId(first.getReview().getId().toString())
                            .author(first.getReporter().getName())
                            .content(first.getReview().getContent())
                            .category(convertCategoryToKor(first.getCategory()))
                            .status(convertStatusToKor(first.getStatus()))
                            .date(first.getCreatedAt().toLocalDate().toString())
                            .build();
                })
                .collect(Collectors.toList());
    }

    private String convertStatus(String statusKor) {
        return switch (statusKor) {
            case "대기" -> "PENDING";
            case "처리완료" -> "REVIEWED";
            case "반려" -> "REJECTED";
            default -> "PENDING";
        };
    }

    private String convertStatusToKor(ReportStatus status) {
        return switch (status) {
            case PENDING -> "대기";
            case REVIEWED -> "처리완료";
            case REJECTED -> "반려";
        };
    }
    private String convertCategory(String categoryKor) {
        return switch (categoryKor) {
            case "비방, 욕설" -> "Abusive";
            case "허위정보" -> "FalseInformation";
            case "광고" -> "Advertisement";
            case "기타" -> "Etc";
            case "전체 사유" -> ""; // 필터 전체일 때 빈값 처리 가능
            default -> "";
        };
    }
    private String convertCategoryToKor(ReportCategory category) {
        return switch (category) {
            case Abusive -> "비방, 욕설";
            case FalseInformation -> "허위정보";
            case Advertisement -> "광고";
            case Etc -> "기타";
        };
    }
}
