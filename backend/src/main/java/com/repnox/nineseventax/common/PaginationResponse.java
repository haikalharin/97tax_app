package com.repnox.nineseventax.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaginationResponse<T> {

    private long numResults;

    private int resultsInPage;

    private int pageNumber;

    private int pageSize;

    private List<T> rows;

    public static <T> PaginationResponse<T> fromPage(Page<T> page) {
        PaginationResponse<T> response = new PaginationResponse<>();
        response.setNumResults(page.getTotalElements());
        response.setPageNumber(page.getNumber());
        response.setPageSize(page.getSize());
        response.setResultsInPage(page.getNumberOfElements());
        response.setRows(page.getContent());
        return response;
    }

}
