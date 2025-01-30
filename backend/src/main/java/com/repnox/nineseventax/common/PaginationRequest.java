package com.repnox.nineseventax.common;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PaginationRequest<T> {

    private int pageNumber;

    private int pageSize;

    private String orderBy;

    private Sort.Direction orderDirection;

    private T query;

    public Pageable toPageable() {
        if (getOrderBy() != null && orderDirection != null) {
            return PageRequest.of(getPageNumber(), getPageSize(), Sort.by(getOrderDirection(), getOrderBy()));
        } else {
            return PageRequest.of(getPageNumber(), getPageSize());
        }
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public Sort.Direction getOrderDirection() {
        return orderDirection;
    }

    public void setOrderDirection(Sort.Direction orderDirection) {
        this.orderDirection = orderDirection;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public T getQuery() {
        return query;
    }

    public void setQuery(T query) {
        this.query = query;
    }
}
