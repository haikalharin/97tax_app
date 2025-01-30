package com.repnox.nineseventax.features.admin.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoidRequest {

    private boolean sendNotification;
    private String type;
    private String content;
    private String user;
    private long orderNum;
    private Integer cbtype;

}
