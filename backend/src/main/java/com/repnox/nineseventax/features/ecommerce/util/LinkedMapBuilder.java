package com.repnox.nineseventax.features.ecommerce.util;

import java.util.LinkedHashMap;

public class LinkedMapBuilder {

    private LinkedHashMap<String,Object> map = new LinkedHashMap<>();

    public LinkedMapBuilder put(String key, String value) {
        map.put(key, value);
        return this;
    }

    public LinkedMapBuilder put(String key, LinkedMapBuilder builder) {
        map.put(key, builder.getMap());
        return this;
    }

    public LinkedHashMap<String,Object> getMap() {
        return map;
    }

}
