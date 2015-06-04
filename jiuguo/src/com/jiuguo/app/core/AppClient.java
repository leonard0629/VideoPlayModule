package com.jiuguo.app.core;

import android.util.Log;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jiuguo.app.bean.Barrage;
import com.jiuguo.app.bean.NewVideoUrl;
import com.jiuguo.app.bean.UrlBean;
import com.jiuguo.app.network.HttpHelper;
import com.jiuguo.app.network.URLs;
import com.jiuguo.app.utils.Sign;
import com.umeng.analytics.MobclickAgent;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppClient {
    public static final String TAG = "AppClient";

    /**
     * 获取视频地址新接口
     * 视频在一天之内更新的会用新接口，否则用旧接口
     *
     * @param appContext
     * @param checkedId
     * @param format
     * @return
     */
    public static NewVideoUrl getNewYoukuUrl(AppContext appContext, int userId, int checkedId, String format) {
        NewVideoUrl youkuUrl = null;
        String url = URLs.GetNewYouKu + "video_checkedid=" + checkedId + "&format=" + formatInput(format) + "&user_id=" + userId;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("video_checkedid", checkedId);
        params.put("format", format);
        params.put("user_id", userId);
        url += "&sign=" + Sign.sign(params);
        Log.i(TAG, "url:" + url);
        try {
            String response = HttpHelper.get(url, URLs.ENCODE);
            youkuUrl = NewVideoUrl.parse(response);
            youkuUrl.setType(NewVideoUrl.TypeYouku);

            MobclickAgent.onEvent(appContext, "NetWorkRequest_Action:newvideourl");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "get exception when getNewYoukuUrl, cause: " + e.getMessage());
        }
        return youkuUrl;
    }

    /**
     * 获取视频地址
     *
     * @param appContext
     * @param appContext
     * @param videoId
     * @param checkId
     * @param userId     if -1,cannot finish mission
     * @param userToken  if null,cannot finish mission
     * @return
     */
    public static NewVideoUrl getYoukuUrl(AppContext appContext, Long videoId, int checkId, int userId, String userToken) {
        String videoUrl = null;
        String youku_ip = null;
        String youku_ep = null;
        String url = null;
        String response = null;
        JSONObject object = null;
        NewVideoUrl youkuUrl = new NewVideoUrl();
        youkuUrl.setType(NewVideoUrl.TypeYouku);
        try {
            url = "http://v.youku.com/player/getPlayList/VideoIDS/" + videoId
                    + "/Pf/4/ctype/12/ev/1";
            Log.i(TAG, "url =" + url);
            response = HttpHelper.get(url, URLs.ENCODE);
            object = JSON.parseObject(response);
            if (object.containsKey("data")) {
                JSONArray dataJsonArray = object.getJSONArray("data");
                JSONObject dataObject = dataJsonArray.getJSONObject(0);
                youku_ip = dataObject.getString("ip");
                youku_ep = dataObject.getString("ep");
                if (dataObject.containsKey("streamsizes")) {
                    JSONObject segsJsonObject = dataObject
                            .getJSONObject("streamsizes");
                    List<UrlBean> urlBeans = new ArrayList<UrlBean>();
                    UrlBean urlBean = null;
                    String size = null;
                    if (segsJsonObject.containsKey("flv")) {
                        urlBean = new UrlBean();
                        size = segsJsonObject.getString("flv");
                        urlBean.setSize((int) (Long.parseLong(size) / 1024 / 1024));
                        urlBean.setType("flv");
                        urlBean.setShowName("标清");
                        urlBeans.add(urlBean);
                    }
                    if (segsJsonObject.containsKey("mp4")) {
                        urlBean = new UrlBean();
                        size = segsJsonObject.getString("mp4");
                        urlBean.setSize((int) (Long.parseLong(size) / 1024 / 1024));
                        urlBean.setType("mp4");
                        urlBean.setShowName("高清");
                        urlBeans.add(urlBean);
                    }
                    if (segsJsonObject.containsKey("hd2")) {
                        urlBean = new UrlBean();
                        size = segsJsonObject.getString("hd2");
                        urlBean.setSize((int) (Long.parseLong(size) / 1024 / 1024));
                        urlBean.setType("hd2");
                        urlBean.setShowName("超清");
                        urlBeans.add(urlBean);
                    }
                    if (segsJsonObject.containsKey("hd3")) {
                        urlBean = new UrlBean();
                        size = segsJsonObject.getString("hd3");
                        urlBean.setSize((int) (Long.parseLong(size) / 1024 / 1024));
                        urlBean.setType("hd3");
                        urlBean.setShowName("1080P");
                        urlBeans.add(urlBean);
                    }
                    youkuUrl.setListUrl(urlBeans);
                }
            }

            url = URLs.VIDEOURL_V2 + "user_id=" + userId + "&user_token="
                    + formatInput(userToken) + "&video_id=" + videoId
                    + "&video_checkedid=" + checkId
                    + "&iphone=false&youku_ip=" + formatInput(youku_ip) + "&youku_ep="
                    + formatInput(youku_ep);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("user_id", userId);
            params.put("user_token", userToken);
            params.put("video_id", videoId);
            params.put("video_checkedid", checkId);
            params.put("iphone", "false");
            params.put("youku_ip", youku_ip);
            params.put("youku_ep", youku_ep);
            url += "&sign=" + Sign.sign(params);

            response = HttpHelper.get(url, URLs.ENCODE);
            object = JSON.parseObject(response);
            if (object.containsKey("valid")) {
                if (object.getIntValue("valid") == 0) {
                    videoUrl = object.getString("url");
                    youkuUrl.setBaseURL(videoUrl);
                    List<UrlBean> urlBeans = youkuUrl.getListUrl();
                    for (UrlBean tmpUrl : urlBeans) {
                        tmpUrl.setUrl(youkuUrl.getBaseURL().replace("type=mp4",
                                "type=" + tmpUrl.getType()));
                    }
                    MobclickAgent.onEvent(appContext, "NetWorkRequest_Action:videourl");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "get exception when getYoukuUrl, cause: " + e.getMessage());
        }
        return youkuUrl;
    }

    /**
     * 发送弹幕
     * @param appContext
     * @param appContext
     * @param userId
     * @param userToken
     * @param checkId
     * @param videoTime
     * @param color
     * @param barrage
     * @return
     */
    public static boolean postBarrage(AppContext appContext, int userId, String userToken,
                                      int checkId, int videoTime, String color, String barrage) {
        boolean isSuccess = false;

        try {
            String url = URLs.BARRAGE_POST_V2 + "user_id=" + userId
                    + "&user_token=" + formatInput(userToken) + "&video_checkedid="
                    + checkId + "&barrage_videotime=" + videoTime + "&color="
                    + formatInput(color) + "&barrage=" + formatInput(barrage);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("user_id", userId);
            params.put("user_token", userToken);
            params.put("video_checkedid", checkId);
            params.put("barrage_videotime", videoTime);
            params.put("color", color);
            params.put("barrage", barrage);
            url += "&sign=" + Sign.sign(params);

            String response = HttpHelper.get(url, URLs.ENCODE);
            JSONObject object = JSON.parseObject(response);
            if (object.containsKey("success")) {
                int code = object.getIntValue("success");
                if (code == 1) {
                    isSuccess = true;
                    MobclickAgent.onEvent(appContext, "NetWorkRequest_Action:postbarrage");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "get exception when postBarrage, cause: " + e.getMessage());
        }

        return isSuccess;
    }

    /**
     * 获取弹幕
     * @param checkId
     * @return
     */
    public static List<Barrage> getBarrages(AppContext appContext, int checkId) {
        List<Barrage> barrages = null;

        try {
            String url = URLs.BARRAGE_GET_V2 + "video_checkedid=" + checkId;
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("video_checkedid", checkId);
            url += "&sign=" + Sign.sign(params);
            String response = HttpHelper.get(url, URLs.ENCODE);
            JSONObject object = JSON.parseObject(response);
            int count = object.getIntValue("count");
            JSONArray array = object.getJSONArray("contents");
            barrages = new ArrayList<Barrage>();
            for (int i = 0; i < count; i++) {
                barrages.add(Barrage.parse(array.getString(i)));
            }
            MobclickAgent.onEvent(appContext, "NetWorkRequest_Action:getbarrage");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "get exception when getBarrages, cause: " + e.getMessage());
        }

        return barrages;
    }


//    public static WeXinTrade getWeXinTrade(AppContext appContext, int userId,
//                                           String userToken, int productId) throws AppException {
//        WeXinTrade trade = null;
//
//        try {
//            String url = URLs.WEIXINTRADE_GET_V2 + "&user_id=" + userId
//                    + "&user_token=" + formatInput(userToken) + "&fromapp=" + PLATFORM + "&fee="
//                    + productId;
//
//            String response = HttpHelper.get(url, URLs.ENCODE);
//            JSONObject object = JSON.parseObject(response);
//            trade = WeXinTrade.parse(object);
//            MobclickAgent.onEvent(appContext, "NetWorkRequest_Action:weixinpay");
//
//            return trade;
//        } catch (Exception e) {
//            // TODO: handle exception
//            throw AppException.run(e);
//        }
//    }

//    public static boolean getWeixinPayState(AppContext appContext, int userId, String userToken, String tradeId) throws AppException {
//        boolean isSuccess = false;
//        try {
//            boolean isRetry = true;
//            while (isRetry) {
//                String url = URLs.WEIXINPAYSTATE_GET_V2 + "&user_id=" + userId
//                        + "&user_token=" + formatInput(userToken) + "&tradeId=" + tradeId;
//
//                String response = HttpHelper.get(url, URLs.ENCODE);
//                JSONObject object = JSON.parseObject(response);
//                if (object.containsKey("trade_state")) {
//                    int type = object.getIntValue("trade_state");
//                    if (type == 1) {
//                        isSuccess = true;
//                        MobclickAgent.onEvent(appContext, "NetWorkRequest_Action:weixinpaystate");
//                    }
//                    isRetry = false;
//                }
//            }
//            return isSuccess;
//        } catch (Exception e) {
//            // TODO: handle exception
//            e.printStackTrace();
//            throw AppException.run(e);
//        }
//    }

//    public static AlipayTrade getAlipayTrade(AppContext appContext, int userId,
//                                             String userToken, int productId) throws AppException {
//        AlipayTrade trade = null;
//
//        try {
//            String url = URLs.ALIPAYTRADE_GET_V2 + "&user_id=" + userId
//                    + "&user_token=" + formatInput(userToken) + "&fromapp=" + PLATFORM + "&fee="
//                    + productId;
//
//            String response = HttpHelper.get(url, URLs.ENCODE);
//            JSONObject object = JSON.parseObject(response);
//            trade = AlipayTrade.parse(object);
//
//            MobclickAgent.onEvent(appContext, "NetWorkRequest_Action:alipay");
//            return trade;
//        } catch (Exception e) {
//            // TODO: handle exception
//            throw AppException.run(e);
//        }
//    }


    /**
     * 送苹果
     * @param appContext
     * @param videoId
     * @return
     */
    public static boolean postApple(AppContext appContext, int userId, String userToken, int videoId) {
        boolean result = false;
        try {
            String url = URLs.POSTAPPLE + "video_checkedid=" + videoId + "&user_id=" + userId + "&user_token=" + userToken;
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("video_checkedid", videoId);
            params.put("user_id", userId);
            params.put("user_token", userToken);
            url += "&sign=" + Sign.sign(params);

            String response = HttpHelper.get(url, URLs.ENCODE);
            JSONObject object = JSON.parseObject(response);
            int type = object.getIntValue("valid");
            if (type == 0) {
                result = true;
                MobclickAgent.onEvent(appContext, "NetWorkRequest_Action:postapple");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "get exception when postApple, cause: " + e.getMessage());
        }
        return result;
    }

    /**
     * 获取苹果
     * @param appContext
     * @param videoId
     * @return
     */
    public static int getApple(AppContext appContext, int videoId) {
        int count = 0;

        try {
            String url = URLs.GETVIDEOAPPLE + "video_checkedid=" + videoId;
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("video_checkedid", videoId);
            url += "&sign=" + Sign.sign(params);
            // Log.v(TAG, url);

            String response = HttpHelper.get(url, URLs.ENCODE);
            JSONObject object = JSON.parseObject(response);
            count = object.getIntValue("count");
            MobclickAgent.onEvent(appContext, "NetWorkRequest_Action:getvideoapple");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "get exception when getApple, cause: " + e.getMessage());
        }
        return count;
    }

    /**
     * 获取直播URL
     * @param appContext
     * @param id
     * @return
     */
    public static NewVideoUrl getLiveUrl(AppContext appContext, int id) {
        NewVideoUrl result = null;
        try {
            String url = URLs.GETLIVEURL + "id=" + id;
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("id", id);
            url += "&sign=" + Sign.sign(params);

            String response = HttpHelper.get(url, URLs.ENCODE);
            JSONObject object = JSON.parseObject(response);
            String baseURL = object.getString("url");
            if (baseURL == null || baseURL.equals("")) {
                return null;
            }
            result = new NewVideoUrl();
            result.setType(NewVideoUrl.TypeLive);
            result.setBaseURL(baseURL);
            String urls = object.getString("allurl");
            if (urls != null) {
                String[] allurl = urls.split("\\|");
                if (allurl != null && allurl.length == 3) {
                    List<UrlBean> urlBeans = new ArrayList<UrlBean>();

                    UrlBean urlBean = new UrlBean();
                    urlBean.setShowName("标清");
                    urlBean.setUrl(allurl[2]);
                    urlBeans.add(urlBean);

                    urlBean = new UrlBean();
                    urlBean.setShowName("高清");
                    urlBean.setUrl(allurl[1]);
                    urlBeans.add(urlBean);

                    urlBean = new UrlBean();
                    urlBean.setShowName("超清");
                    urlBean.setUrl(allurl[0]);
                    urlBeans.add(urlBean);
                    result.setListUrl(urlBeans);
                    MobclickAgent.onEvent(appContext, "NetWorkRequest_Action:getliveurl");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "get exception when getLiveUrl, cause: " + e.getMessage());
        }

        return result;
    }

    /**
     * 获取直播订阅
     * @param appContext
     * @param userId
     * @param userToken
     * @param lid
     * @return
     */
    public static boolean getLiveSub(AppContext appContext, int userId, String userToken, int lid) {
        boolean isSub = false;

        try {
            String url = URLs.GETLIVESUB + "user_id=" + userId + "&user_token="
                    + formatInput(userToken) + "&lid=" + lid;
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("user_id", userId);
            params.put("user_token", userToken);
            params.put("lid", lid);
            url += "&sign=" + Sign.sign(params);

            String response = HttpHelper.get(url, URLs.ENCODE);
            JSONObject object = JSON.parseObject(response);
            if (object.containsKey("issub")) {
                int code = object.getIntValue("issub");
                if (code == 1) {
                    isSub = true;
                    MobclickAgent.onEvent(appContext, "NetWorkRequest_Action:getlivesub");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "get exception when getLiveSub, cause: " + e.getMessage());
        }

        return isSub;
    }

    /**
     * 订阅直播
     * @param appContext
     * @param userId
     * @param userToken
     * @param lid
     * @return
     */
    public static boolean postLiveSub(AppContext appContext, int userId, String userToken, int lid) {
        boolean isSuccess = false;

        try {
            String url = URLs.POSTLIVESUB + "user_id=" + userId
                    + "&user_token=" + formatInput(userToken) + "&lid=" + lid;
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("user_id", userId);
            params.put("user_token", userToken);
            params.put("lid", lid);
            url += "&sign=" + Sign.sign(params);

            String response = HttpHelper.get(url, URLs.ENCODE);
            JSONObject object = JSON.parseObject(response);
            if (object.containsKey("success")) {
                int code = object.getIntValue("success");
                if (code == 1) {
                    isSuccess = true;
                    MobclickAgent.onEvent(appContext, "NetWorkRequest_Action:postlivesub");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "get exception when postLiveSub, cause: " + e.getMessage());
        }

        return isSuccess;
    }

    /**
     * 取消订阅
     * @param appContext
     * @param userId
     * @param userToken
     * @param lid
     * @return
     */
    public static boolean delLiveSub(AppContext appContext, int userId, String userToken, int lid) {
        boolean isSuccess = false;

        try {
            String url = URLs.DELLIVESUB + "user_id=" + userId + "&user_token="
                    + formatInput(userToken) + "&lid=" + lid;
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("user_id", userId);
            params.put("user_token", userToken);
            params.put("lid", lid);
            url += "&sign=" + Sign.sign(params);

            String response = HttpHelper.get(url, URLs.ENCODE);
            JSONObject object = JSON.parseObject(response);
            if (object.containsKey("success")) {
                int code = object.getIntValue("success");
                if (code == 1) {
                    isSuccess = true;
                    MobclickAgent.onEvent(appContext, "NetWorkRequest_Action:dellivesub");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "get exception when delLiveSub, cause: " + e.getMessage());
        }

        return isSuccess;
    }

    /**
     * 中文编码
     * @param input
     * @return
     */
    private static String formatInput(String input) {
        try {
            if (input != null) {
                return URLEncoder.encode(input, URLs.ENCODE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
