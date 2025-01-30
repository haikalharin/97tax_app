package com.repnox.nineseventax.features.admin;

import java.util.List;

public class DeleteListRequest {

    private List<Long> ids;

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }
}
