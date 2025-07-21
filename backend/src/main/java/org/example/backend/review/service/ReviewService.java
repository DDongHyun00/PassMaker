package org.example.backend.review.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.mentor.domain.MentorUser;
import org.example.backend.review.domain.Review;
import org.example.backend.review.dto.ReviewDto;
import org.example.backend.review.repository.ReviewRepository;
import org.example.backend.user.domain.User;
import org.example.backend.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.backend.review.dto.ReviewReportDto;
import org.example.backend.review.domain.ReviewReport;
import org.example.backend.review.dto.ReportStatus;
import org.example.backend.review.repository.ReviewReportRepository;
import org.example.backend.reservation.domain.MentoringReservation;
import org.example.backend.reservation.repository.MentoringReservationRepository;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ReviewReportRepository reviewReportRepository; // MPR-007: 리뷰 신고 Repository 추가
    private final MentoringReservationRepository mentoringReservationRepository; // MentoringReservationRepository 추가

    @Transactional
    public ReviewDto.CreateResponse createReview(ReviewDto.CreateRequest request, Long userId) {
        // 1. 예약 정보 확인 및 리뷰 작성 자격 검증
        MentoringReservation reservation = mentoringReservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다."));

        // 예약의 멘토와 멘티 정보를 가져옴
        MentorUser mentor = reservation.getMentor();
        User mentee = reservation.getUser();

        // 현재 로그인한 사용자가 해당 예약의 멘티인지 확인
        if (!mentee.getId().equals(userId)) {
            throw new IllegalArgumentException("리뷰를 작성할 권한이 없습니다. 해당 예약의 멘티만 리뷰를 작성할 수 있습니다.");
        }

        // TODO: 예약 상태 확인 (예: 완료된 예약에 대해서만 리뷰 작성 가능)
        // if (!reservation.getStatus().equals(ReservationStatus.COMPLETED)) {
        //     throw new IllegalStateException("완료된 예약에 대해서만 리뷰를 작성할 수 있습니다.");
        // }

        // TODO: 이미 리뷰가 작성되었는지 확인하는 로직 추가 필요

        Review review = Review.builder()
                .reservation(reservation) // 예약 엔티티 연결
                .rating(request.getRating())
                .content(request.getContent())
                .build();

        Review savedReview = reviewRepository.save(review);

        return ReviewDto.CreateResponse.builder()
                .reviewId(savedReview.getId())
                .userId(mentee.getId())
                .userNickname(mentee.getNickname())
                .mentorId(mentor.getId())
                .mentorNickname(mentor.getUser().getNickname())
                .rating(savedReview.getRating())
                .content(savedReview.getContent())
                .createdAt(savedReview.getCreatedAt())
                .build();
    }

    /**
     * MPR-007: 리뷰 신고
     * 부적절한 리뷰에 대한 신고를 처리합니다.
     * @param reportDto 신고할 리뷰의 ID와 신고 사유를 포함하는 DTO
     * @param reporterUserId 신고자의 고유 ID
     */
    @Transactional
    public void reportReview(ReviewReportDto reportDto, Long reporterUserId) {
        // 1. 신고할 리뷰 엔티티 조회
        Review review = reviewRepository.findById(reportDto.getReviewId())
                .orElseThrow(() -> new IllegalArgumentException("신고할 리뷰를 찾을 수 없습니다."));

        // 2. 신고자(reporter) 엔티티 조회
        User reporter = userRepository.findById(reporterUserId)
                .orElseThrow(() -> new IllegalArgumentException("신고자를 찾을 수 없습니다."));

        // 3. ReviewReport 엔티티 생성 및 값 설정
        ReviewReport reviewReport = ReviewReport.builder()
                .review(review) // 신고할 리뷰 엔티티 연결
                .reporter(reporter) // 신고자 엔티티 연결
                .reason(reportDto.getReason()) // DTO의 reason 값을 엔티티의 reason 필드에 설정
                .status(ReportStatus.PENDING) // 초기 상태 설정 (PENDING: 대기 중)
                .build();

        // 4. 엔티티 저장
        reviewReportRepository.save(reviewReport);

        // ✅ 5. Review 엔티티의 IsReported 필드 업데이트
        review.setIsReported(true); // IsReported 필드를 true로 설정
        reviewRepository.save(review); // 변경된 Review 엔티티 저장
    }
}