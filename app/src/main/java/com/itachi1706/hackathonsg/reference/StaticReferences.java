package com.itachi1706.hackathonsg.reference;

import com.getpebble.android.kit.util.PebbleDictionary;
import com.itachi1706.hackathonsg.Objects.JSONStoredProducts;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Kenneth on 25/7/2015
 * for Hackathon@SG in package com.itachi1706.hackathonsg.reference
 */
public class StaticReferences {

    /**
     * Base URL for URLy stuff
     */
    public static final String BASE_URL = "http://gottagofast.cloudapp.net/ayyy/";
    public static final int HTTP_QUERY_TIMEOUT = 30000;

    public static PebbleDictionary dict1, dict2;
    public static int extraSend;

    public static final UUID PEBBLE_APP_UUID = UUID.fromString("4093e50e-5ae4-46c5-b4da-3d41f6613f2a");

    public static ArrayList<JSONStoredProducts> products = new ArrayList<>();
}
