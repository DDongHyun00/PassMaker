package org.example.backend.admin.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.admin.dto.DashWeeklySaleDto;
import org.example.backend.admin.repository.DashWeeklySaleRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashWeeklySaleService {
    private final DashWeeklySaleRepository dashWeeklySaleRepository;

    public List<DashWeeklySaleDto> getWeeklySales() {
        // 이번 주 월요일 00:00:00 ~ 일요일 23:59:59 날짜 계산
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfWeek = now.with(DayOfWeek.MONDAY).toLocalDate().atStartOfDay();
        LocalDateTime endOfWeek = now.with(DayOfWeek.SUNDAY).toLocalDate().atTime(23, 59, 59);

        // 범위에 맞는 매출 데이터 가져오기
        List<Object[]> results = dashWeeklySaleRepository.findThisWeekSales(startOfWeek, endOfWeek);

        return results.stream()
                .map(row -> {
                    LocalDate date = convertToLocalDate(row[0]); // ✅ 안전하게 LocalDate로 변환
                    String dayName = getKoreanDayName(date.getDayOfWeek());
                    double sales = ((Number) row[1]).doubleValue();
                    return new DashWeeklySaleDto(dayName, sales);
                })
                .toList();
    }

    private LocalDate convertToLocalDate(Object dateObj) {
        if (dateObj instanceof java.sql.Date sqlDate) {
            return sqlDate.toLocalDate();
        } else if (dateObj instanceof java.time.LocalDate localDate) {
            return localDate;
        } else if (dateObj instanceof java.util.Date utilDate) {
            return utilDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        } else {
            throw new IllegalArgumentException("Unsupported date type: " + dateObj.getClass());
        }
    }

    private String getKoreanDayName(DayOfWeek dayOfWeek) {
        // 한국어 요일 반환
        return switch (dayOfWeek) {
            case MONDAY -> "월";
            case TUESDAY -> "화";
            case WEDNESDAY -> "수";
            case THURSDAY -> "목";
            case FRIDAY -> "금";
            case SATURDAY -> "토";
            case SUNDAY -> "일";
        };
    }
}
