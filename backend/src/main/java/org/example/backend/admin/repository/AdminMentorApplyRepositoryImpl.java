package org.example.backend.admin.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.example.backend.mentor.domain.ApplyStatus;
import org.example.backend.mentor.domain.MentorApply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AdminMentorApplyRepositoryImpl implements AdminMentorApplyRepositoryCustom {
    private final EntityManager em;

    @Override
    public Page<MentorApply> findFiltered(String searchText, ApplyStatus status, String type, Pageable pageable) {
        String jpql = "SELECT m FROM MentorApply m JOIN m.user u WHERE 1=1";
        String countJpql = "SELECT COUNT(m) FROM MentorApply m JOIN m.user u WHERE 1=1";

        String orderBy = "";
        if (pageable.getSort().isSorted()) {
            Sort.Order order = pageable.getSort().iterator().next(); // 첫 번째 정렬 기준만 적용
            String property = switch (order.getProperty()) {
                case "user.name" -> "u.name";
                case "updatedAt" -> "m.updatedAt";
                case "createdAt" -> "m.createdAt";
                default -> "m.createdAt";
            };
            orderBy = " ORDER BY " + property + (order.getDirection().isAscending() ? " ASC" : " DESC");
        }

        StringBuilder where = new StringBuilder();
        Long id = null;
        if (!searchText.isBlank()) {
            where.append(" AND (LOWER(u.name) LIKE LOWER(CONCAT('%', :searchText, '%')) ")
                    .append("OR LOWER(u.email) LIKE LOWER(CONCAT('%', :searchText, '%')) ");

            try {
                id = Long.parseLong(searchText);
                where.append("OR m.applyId = :id)"); // 숫자 비교
            } catch (NumberFormatException e) {
                where.append(")"); // 괄호만 닫기
            }
        }

        if (status != null) {
            where.append(" AND m.status = :status");
        }

        if (!type.equals("전체 분야")) {
            where.append(" AND EXISTS (SELECT f2 FROM m.applyFields f2 WHERE f2.fieldName LIKE CONCAT('%', :type, '%'))");
        }

        TypedQuery<MentorApply> query = em.createQuery(jpql + where + orderBy, MentorApply.class);
        TypedQuery<Long> countQuery = em.createQuery(countJpql + where, Long.class);

        if (!searchText.isBlank()) {
            query.setParameter("searchText", searchText);
            countQuery.setParameter("searchText", searchText);
            if (id != null) {
                query.setParameter("id", id);
                countQuery.setParameter("id", id);
            }
        }
        if (status != null) {
            query.setParameter("status", status);
            countQuery.setParameter("status", status);
        }

        if (!type.equals("전체 분야")) {
            query.setParameter("type", type);
            countQuery.setParameter("type", type);
        }

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<MentorApply> resultList = query.getResultList();
        Long total = countQuery.getSingleResult();

        return new PageImpl<>(resultList, pageable, total);
    }
}
