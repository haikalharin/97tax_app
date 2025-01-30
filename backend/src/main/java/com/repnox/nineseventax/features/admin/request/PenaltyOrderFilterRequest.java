package com.repnox.nineseventax.features.admin.request;

import com.repnox.nineseventax.features.admin.OrderSearchStatuses;

public class PenaltyOrderFilterRequest {
    String orderBy;
    String orderDirection;
    int pageNumber;
    int pageSize;

    PenaltyOrderFilterQuery query;

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getOrderDirection() {
        return orderDirection;
    }

    public void setOrderDirection(String orderDirection) {
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

    public PenaltyOrderFilterQuery getQuery() {
        return query;
    }

    public void setQuery(PenaltyOrderFilterQuery query) {
        this.query = query;
    }
}
