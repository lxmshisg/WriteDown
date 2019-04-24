package com.writedown.writedown.BaiduTranslate;
import java.util.HashMap;
import java.util.Map;

public class TranslationAPI {
    private static final String TRANS_API_HOST = "http://api.fanyi.baidu.com/api/trans/vip/translate";

    private static final String APP_ID = "20190409000285899";
    private static final String SECURITY_KEY = "1ebrj6EofozmTCCYMmo8";

    private String appid;
    private String securityKey;

    public TranslationAPI() {
        this.appid = APP_ID;
        this.securityKey = SECURITY_KEY;
    }

    public TranslationAPI(String appid, String securityKey) {
        this.appid = appid;
        this.securityKey = securityKey;
    }

    public String getTransResult(String query, String from, String to) {
        Map<String, String> params = buildParams(query, from, to);
        return Http_Get.get(TRANS_API_HOST, params);
    }

    private Map<String, String> buildParams(String query, String from, String to) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("q", query);
        params.put("from", from);
        params.put("to", to);

        params.put("appid", appid);

        // random number
        String salt = String.valueOf(System.currentTimeMillis());
        params.put("salt", salt);

        // sign
        String src = appid + query + salt + securityKey; // original notes before the security
        params.put("sign", MD5.md5(src));

        return params;
    }

}