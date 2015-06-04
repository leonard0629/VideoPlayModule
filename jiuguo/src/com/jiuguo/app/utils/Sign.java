package com.jiuguo.app.utils;

import android.annotation.SuppressLint;

import java.io.UnsupportedEncodingException;
import java.util.*;

public class Sign {

    private final static String TAG = "Sign";

    private final static String DOT = "@_@jiuguo2009.cn@_@";
    private final static String SIGNKEY = "@_@.jiuguo2009.cn.@_@###***^^^@@@_@.jiuguo2009.cn.@_@&&@_@.jiuguo2009.cn.@_@";
    private final static String ENCODE = "utf-8";

    /**
     * 改密码加密
     *
     * @param params
     */
    @SuppressLint("DefaultLocale")
    public static String sign(Map<String, Object> params) {
        String signResult = "";
        if(params != null && params.size() > 0) {
            List<String> keys = new ArrayList<String>(params.keySet());
            List<String> values = new ArrayList<String>();
            Collections.sort(keys);
            for(String key : keys) {
                String value = String.valueOf(params.get(key));
                values.add(value);
            }
            signResult = sign(values);
        }

        return signResult;
    }

    /**
     * 改密码加密
     *
     * @param values
     */
    @SuppressLint("DefaultLocale")
    private static String sign(List<String> values) {
        String signResult = "";
        if(values != null && values.size() > 0) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < values.size(); i ++){
                sb  = sb.append(values.get(i));
                if(i < values.size() - 1) {
                    sb = sb.append(DOT);
                }
            }
            String baseStr = SIGNKEY + sb.toString();
            Calendar calendar = Calendar.getInstance();
            int month = calendar.get(Calendar.MONTH) + 1;
            int singleLength = baseStr.length() / month + 1;
            String[] temp = new String[month];
            String result = "";
            for (int i = 0; i < month; i++) {
                if ((i + 1) * singleLength <= baseStr.length()) {
                    temp[i] = baseStr.substring((i * singleLength), (i + 1) * singleLength);
                } else {
                    temp[i] = baseStr.substring((i * singleLength));
                }
            }

            try {
                for (int i = 0; i < month; i++) {
                    result += MD5.getMessageDigest(temp[i].getBytes(ENCODE));
                }
                signResult = MD5.getMessageDigest(result.toUpperCase().getBytes(ENCODE));

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return signResult;
    }
}
