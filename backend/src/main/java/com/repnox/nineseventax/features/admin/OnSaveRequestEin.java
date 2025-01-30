package com.repnox.nineseventax.features.admin;

import com.repnox.nineseventax.features.ein.EinOrder;
import lombok.Data;

@Data
public class OnSaveRequestEin {

    private EinOrder orderRecord;
    private String content;
    private String emailChangeContent;
    private String user;

}
