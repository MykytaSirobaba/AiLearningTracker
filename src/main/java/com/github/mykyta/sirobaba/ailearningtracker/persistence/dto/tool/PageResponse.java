package com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.tool;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Created by Mykyta Sirobaba on 27.10.2025.
 * email mykyta.sirobaba@gmail.com
 */
public record PageResponse<T>(
        List<T> content,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean last
) {
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}
