package com.repnox.nineseventax.features.ecommerce.chaseorbital;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import java.util.Map;
import java.util.List;


public final class Utils {
    public static final Type TYPE_MAP_OF_STRING_TO_STRING = new TypeToken<Map<String, String>>(){}.getType();
    public static final Type TYPE_LIST_OF_STRINGS = new TypeToken<List<String>>(){}.getType();
}
