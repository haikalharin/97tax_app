package com.repnox.nineseventax.features.ecommerce.util;

import java.util.Map;

public class JwtPayloadResponse {

    private String jwt;

    private Map<String,Object> payload;

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }
}
