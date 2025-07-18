package org.example.backend.admin.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.example.backend.inquiry.domain.Inquiry;
import org.example.backend.inquiry.domain.InquiryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AdminInquiryRepositoryImpl implements AdminInquiryRepositoryCustom {
    private final EntityManager em;

    @Override
    public Page<Inquiry> findFiltered(String searchText, InquiryStatus status, String type, Pageable pageable) {
        String jpql = "SELECT i FROM Inquiry i WHERE 1=1";
        String countJpql = "SELECT COUNT(i) FROM Inquiry i WHERE 1=1";

        StringBuilder where = new StringBuilder();

        if (searchText != null && !searchText.isBlank()) {
            where.append(" AND (LOWER(i.inquiryTitle) LIKE LOWER(CONCAT('%', :searchText, '%')) ")
                    .append("OR LOWER(i.user.name) LIKE LOWER(CONCAT('%', :searchText, '%')))"); // inquirer가 user.name으로 변경됨
        }

        if (status != null) {
            where.append(" AND i.inquiryStatus = :status");
        }

        if (type != null && !type.equals("전체 구분")) {
            where.append(" AND i.inquiryType = :type");
        }

        // 정렬
        String orderBy = "";
        if (pageable.getSort().isSorted()) {
            Sort.Order order = pageable.getSort().iterator().next();
            String property = switch (order.getProperty()) {
                case "createdAt" -> "i.createdAt";
                case "inquirer" -> "i.user.name";  // user.name으로 맞춤
                case "status" -> "i.inquiryStatus";
                default -> "i.createdAt";
            };
            orderBy = " ORDER BY " + property + (order.getDirection().isAscending() ? " ASC" : " DESC");
        }

        TypedQuery<Inquiry> query = em.createQuery(jpql + where + orderBy, Inquiry.class);
        TypedQuery<Long> countQuery = em.createQuery(countJpql + where, Long.class);

        // 파라미터 바인딩
        if (searchText != null && !searchText.isBlank()) {
            query.setParameter("searchText", searchText);
            countQuery.setParameter("searchText", searchText);
        }

        if (status != null) {
            query.setParameter("status", status);
            countQuery.setParameter("status", status);
        }

        if (type != null && !type.equals("전체 구분")) {
            query.setParameter("type", type);
            countQuery.setParameter("type", type);
        }

        // 페이징
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<Inquiry> resultList = query.getResultList();
        Long total = countQuery.getSingleResult();

        return new PageImpl<>(resultList, pageable, total);
    }
}
