package com.jiuguo.app.core;

import android.util.Log;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gju.app.utils.HttpHelper;
import com.gju.app.utils.MD5;
import com.gju.app.utils.Sign;
import com.igexin.sdk.PushManager;
import com.jiuguo.app.bean.Video;
import com.jiuguo.app.common.URLs;
import com.jiuguo.app.ui.Login;
import com.umeng.analytics.MobclickAgent;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppClient {
    public static final String TAG = "AppClient";

    private final static String PLATFORM = "Android";

    /**
     * 获取视频地址新接口
     * 视频在一天之内更新的会用新接口，否则用旧接口
     *
     * @param appContext
     * @param checkedId
     * @param format
     * @return
     * @throws AppException
     */
    public static NewVideoUrl getNewYoukuUrl(AppContext appContext, int checkedId, String format) throws AppException {
        NewVideoUrl youkuUrl = null;
        String url = URLs.GetNewYouKu + "video_checkedid=" + checkedId + "&format=" + formatInput(format) + "&user_id=" + appContext.getLoginId();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("video_checkedid", checkedId);
        params.put("format", format);
        params.put("user_id", appContext.getLoginId());
        url += "&sign=" + Sign.sign(params);
        Log.i(TAG, "url:" + url);
        try {
            String response = HttpHelper.get(url, URLs.ENCODE);
            youkuUrl = NewVideoUrl.parse(response);
            youkuUrl.setType(NewVideoUrl.TypeYouku);

            MobclickAgent.onEvent(appContext, "NetWorkRequest_Action:newvideourl");
        } catch (Exception e) {
            e.printStackTrace();
            throw AppException.run(e);
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
     * @param userToken  if null,cannot finish mission     @return
     * @throws AppException
     */
    public static NewVideoUrl getYoukuUrl(AppContext appContext, Long videoId,
                                          int checkId, int userId, String userToken) throws AppException {
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

            //Log.i(TAG, url);
            response = HttpHelper.get(url, URLs.ENCODE);
            //Log.e(TAG, "response :" + response);
            object = JSON.parseObject(response);
            if (object.containsKey("valid")) {
                if (object.getIntValue("valid") == 0) {
                    videoUrl = object.getString("url");
                    youkuUrl.setBaseURL(videoUrl);
                    // Log.v(TAG, videoUrl);
                    List<UrlBean> urlBeans = youkuUrl.getListUrl();
                    for (UrlBean tmpUrl : urlBeans) {
                        tmpUrl.setUrl(youkuUrl.getBaseURL().replace("type=mp4",
                                "type=" + tmpUrl.getType()));
                    }
                    MobclickAgent.onEvent(appContext, "NetWorkRequest_Action:videourl");
                }
            } else {
                throw AppException.run(new Exception("Error:code 1"));
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            throw AppException.json(e);
        }
        return youkuUrl;
    }

    /**
     * 发送弹幕
     *
     * @param appContext
     * @param appContext
     * @param userId
     * @param userToken
     * @param checkId
     * @param videoTime
     * @param color
     * @param barrage    @return
     * @throws AppException
     */
    public static boolean postBarrage(AppContext appContext, int userId,
                                      String userToken, int checkId, int videoTime, String color,
                                      String barrage) throws AppException {
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
            throw AppException.run(e);
        }

        return isSuccess;
    }

    /**
     * 获取弹幕
     *
     * @param checkId
     * @return
     * @throws AppException
     */
    public static List<Barrage> getBarrages(AppContext appContext, int checkId) throws AppException {
        List<Barrage> barrages = null;

        try {
            String url = URLs.BARRAGE_GET_V2 + "video_checkedid=" + checkId;
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("video_checkedid", checkId);
            url += "&sign=" + Sign.sign(params);
//            Log.v(TAG, "getBarrages--url: " + url);
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
            // TODO: handle exception
            e.printStackTrace();
            throw AppException.json(e);
        }

        return barrages;
    }


    public static WeXinTrade getWeXinTrade(AppContext appContext, int userId,
                                           String userToken, int productId) throws AppException {
        WeXinTrade trade = null;

        try {
            String url = URLs.WEIXINTRADE_GET_V2 + "&user_id=" + userId
                    + "&user_token=" + formatInput(userToken) + "&fromapp=" + PLATFORM + "&fee="
                    + productId;

            String response = HttpHelper.get(url, URLs.ENCODE);
            JSONObject object = JSON.parseObject(response);
            trade = WeXinTrade.parse(object);
            MobclickAgent.onEvent(appContext, "NetWorkRequest_Action:weixinpay");

            return trade;
        } catch (Exception e) {
            // TODO: handle exception
            throw AppException.run(e);
        }
    }

    public static boolean getWeixinPayState(AppContext appContext, int userId, String userToken, String tradeId) throws AppException {
        boolean isSuccess = false;
        try {
            boolean isRetry = true;
            while (isRetry) {
                String url = URLs.WEIXINPAYSTATE_GET_V2 + "&user_id=" + userId
                        + "&user_token=" + formatInput(userToken) + "&tradeId=" + tradeId;

                String response = HttpHelper.get(url, URLs.ENCODE);
                JSONObject object = JSON.parseObject(response);
                if (object.containsKey("trade_state")) {
                    int type = object.getIntValue("trade_state");
                    if (type == 1) {
                        isSuccess = true;
                        MobclickAgent.onEvent(appContext, "NetWorkRequest_Action:weixinpaystate");
                    }
                    isRetry = false;
                }
            }
            return isSuccess;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            throw AppException.run(e);
        }
    }

    public static AlipayTrade getAlipayTrade(AppContext appContext, int userId,
                                             String userToken, int productId) throws AppException {
        AlipayTrade trade = null;

        try {
            String url = URLs.ALIPAYTRADE_GET_V2 + "&user_id=" + userId
                    + "&user_token=" + formatInput(userToken) + "&fromapp=" + PLATFORM + "&fee="
                    + productId;

            String response = HttpHelper.get(url, URLs.ENCODE);
            JSONObject object = JSON.parseObject(response);
            trade = AlipayTrade.parse(object);

            MobclickAgent.onEvent(appContext, "NetWorkRequest_Action:alipay");
            return trade;
        } catch (Exception e) {
            // TODO: handle exception
            throw AppException.run(e);
        }
    }


    /**
     * 送苹果
     * @param appContext
     * @param videoId
     * @return
     * @throws AppException
     */
    public static boolean postApple(AppContext appContext, int videoId)
            throws AppException {
        try {
            boolean result = false;
            String url = URLs.POSTAPPLE + "video_checkedid=" + videoId
                    + "&user_id=" + appContext.getLoginId() + "&user_token="
                    + appContext.getLoginToken();
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("video_checkedid", videoId);
            params.put("user_id", appContext.getLoginId());
            params.put("user_token", appContext.getLoginToken());
            url += "&sign=" + Sign.sign(params);
            // Log.v(TAG, url);

            String response = HttpHelper.get(url, URLs.ENCODE);
            JSONObject object = JSON.parseObject(response);
            int type = object.getIntValue("valid");
            if (type == 0) {
                result = true;
                MobclickAgent.onEvent(appContext, "NetWorkRequest_Action:postapple");
            }
            return result;

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            throw AppException.run(e);
        }

    }

    /**
     * 获取苹果
     * @param appContext
     * @param videoId
     * @return
     * @throws AppException
     */
    public static int getApple(AppContext appContext, int videoId)
            throws AppException {
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
            return count;

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            throw AppException.run(e);
        }

    }

    /**
     * 获取直播URL
     * @param appContext
     * @param id
     * @return
     * @throws AppException
     */
    public static NewVideoUrl getLiveUrl(AppContext appContext, int id)
            throws AppException {
        NewVideoUrl result = null;
        try {
            String url = URLs.GETLIVEURL + "id=" + id;
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("id", id);
            url += "&sign=" + Sign.sign(params);
//            Log.e("cyj","url: " + url);

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
            throw AppException.run(e);
        }
        return result;

    }

    /**
     * 获取直播订阅
     *
     * @param appContext
     * @param userId
     * @param userToken
     * @param lid
     * @return
     * @throws AppException
     */
    public static boolean getLiveSub(AppContext appContext, int userId,
                                     String userToken, int lid) throws AppException {
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
            throw AppException.run(e);
        }

        return isSub;
    }

    /**
     * 发送直播订阅
     *
     * @param appContext
     * @param userId
     * @param userToken
     * @param lid
     * @return
     * @throws AppException
     */
    public static boolean postLiveSub(AppContext appContext, int userId,
                                      String userToken, int lid) throws AppException {
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
            throw AppException.run(e);
        }

        return isSuccess;
    }

    public static boolean delLiveSub(AppContext appContext, int userId, String userToken, int lid) throws AppException {
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
            throw AppException.run(e);
        }

        return isSuccess;
    }

    /**
     * 部落索引
     *
     * @param appContext
     * @param uid
     * @param token
     * @return
     * @throws AppException
     */
    public static BlogHomeBean getBlogIndex(AppContext appContext, int uid,
                                            String token) throws AppException {
        try {

            String url = URLs.BlogIndex;
            Map<String, Object> params = new HashMap<String, Object>();

            if (appContext.isLogin()) {
                url += "anonymous=0" + "&uid=" + uid + "&token=" + formatInput(token);
                params.put("anonymous", "0");
                params.put("uid", uid);
                params.put("token", token);
            } else {
                url += "anonymous=1";
                params.put("anonymous", "1");
            }
            url += "&sign=" + Sign.sign(params);

//			Log.i(TAG, "url:" + url);
            String response = HttpHelper.get(url, URLs.ENCODE);
            MobclickAgent.onEvent(appContext, "NetWorkRequest_Action:blogindex");
            return BlogHomeBean.parse(response);
        } catch (Exception e) {
            e.printStackTrace();
            throw AppException.run(e);
        }
    }

    public static Boolean loginBlog(AppContext appContext) {
        try {
            String url = null;
            if (appContext.isLogin()) {
                url = URLs.BlogLogin + "user_id=" + appContext.getLoginId()
                        + "&user_token=" + formatInput(appContext.getLoginToken());
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("user_id", appContext.getLoginId());
                params.put("user_token", appContext.getLoginToken());
                url += "&sign=" + Sign.sign(params);
            } else {
                return false;
            }
            String response = HttpHelper.get(url, URLs.ENCODE);
            BlogUser blogUser = BlogUser.parse(response);
            if (blogUser == null) {
                return false;
            }
            appContext.saveBlogInfo(blogUser);
            MobclickAgent.onEvent(appContext, "NetWorkRequest_Action:bloglogin");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 部落登录
     *
     * @param appContext
     * @param userId
     * @param userToken
     * @return
     */
    private static boolean loginBlog(AppContext appContext, int userId, String userToken) {
        try {
            String url = null;
            url = URLs.BlogLogin + "user_id=" + userId + "&user_token=" + formatInput(userToken);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("user_id", userId);
            params.put("user_token", userToken);
            url += "&sign=" + Sign.sign(params);
            //Log.i(TAG, "url:" + url);
            String response = HttpHelper.get(url, URLs.ENCODE);
            BlogUser blogUser = BlogUser.parse(response);
            if (blogUser == null) {
                return false;
            }
            appContext.saveBlogInfo(blogUser);
            MobclickAgent.onEvent(appContext, "NetWorkRequest_Action:bloglogin");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 部落话题列表
     *
     * @param appContext
     * @param sid
     * @param lasttid
     * @return
     * @throws AppException
     */
    public static List<BlogTopicBean> getBlogTopicList(AppContext appContext, int sid, int lasttid)
            throws AppException {
        try {
            String url = URLs.BLOGTOPICLIST + "sid=" + sid;
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("sid", sid);
            if (lasttid != -1) {
                url += "&lasttid=" + lasttid;
                params.put("lasttid", lasttid);
            }
            url += "&sign=" + Sign.sign(params);

            //Log.i(TAG, "url:" + url);
            String response = HttpHelper.get(url, URLs.ENCODE);
            JSONObject jsonObject = JSON.parseObject(response);
            int count = jsonObject.getInteger("count");
            JSONArray topicArray = jsonObject.getJSONArray("topic");
            List<BlogTopicBean> blogTopicBeans = new ArrayList<BlogTopicBean>();
            for (int i = 0; i < count; i++) {
                blogTopicBeans.add(BlogTopicBean.parse(topicArray.get(i).toString()));
            }
            MobclickAgent.onEvent(appContext, "NetWorkRequest_Action:blogtopiclist");
            return blogTopicBeans;
        } catch (Exception e) {
            e.printStackTrace();
            throw AppException.run(e);
        }
    }

    /**
     * 部落话题
     *
     * @param appContext
     * @param sid
     * @param title
     * @param content
     * @param imgIds
     * @param anonymous
     * @return
     * @throws AppException
     */
    public static int pubBlogTopic(AppContext appContext, int sid, String title, String content, String imgIds, int anonymous) throws AppException {
        try {
            String url = URLs.BlogPubTopic + "uid="
                    + appContext.getmBlogUser().getUid() + "&token="
                    + formatInput(appContext.getmBlogUser().getToken()) + "&sid=" + sid
                    + "&title=" + formatInput(title) + "&content="
                    + formatInput(content) + "&imgids=" + formatInput(imgIds) + "&anonymous="
                    + anonymous;
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("uid", appContext.getmBlogUser().getUid());
            params.put("token", appContext.getmBlogUser().getToken());
            params.put("sid", sid);
            params.put("title", title);
            params.put("content", content);
            params.put("imgids", imgIds);
            params.put("anonymous", anonymous);
            url += "&sign=" + Sign.sign(params);

            //Log.i(TAG, "url:" + url);
            String response = HttpHelper.get(url, URLs.ENCODE);
            JSONObject jsonObject = JSON.parseObject(response);
            int code = jsonObject.getInteger("Code");
            if (code == 0) {
                MobclickAgent.onEvent(appContext, "NetWorkRequest_Action:blogpubtopic");
                return jsonObject.getIntValue("tid");
            }
            return -1;
        } catch (Exception e) {
            e.printStackTrace();
            throw AppException.run(e);
        }
    }

    /**
     * 部落话题
     *
     * @param appContext
     * @param tid
     * @return
     * @throws AppException
     */
    public static BlogTopicBean getBlogTopic(AppContext appContext, int tid) throws AppException {
        try {
            String url = URLs.BlOGTOPIC + "tid=" + tid;
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("tid", tid);
            url += "&sign=" + Sign.sign(params);

            //Log.i(TAG, "url:" + url);
            String response = HttpHelper.get(url, URLs.ENCODE);
            MobclickAgent.onEvent(appContext, "NetWorkRequest_Action:blogtopic");
            return BlogTopicBean.parse2(response);
        } catch (Exception e) {
            e.printStackTrace();
            throw AppException.run(e);
        }
    }

    /**
     * 发布回复接口
     * 部落回复
     *
     * @param appContext
     * @param tid        话题ID
     * @param content    内容
     * @param imgIds     图片ID，用“|”隔开，例如：1|2|3|4
     * @param anonymous  是否匿名，1为匿名，0为实名
     * @return
     * @throws AppException
     */
    public static boolean pubBlogReply(AppContext appContext, int tid, String content, String imgIds, int anonymous) throws AppException {
        try {
            String url = URLs.BLOGPUBREPLY + "uid="
                    + appContext.getmBlogUser().getUid() + "&token="
                    + formatInput(appContext.getmBlogUser().getToken()) + "&tid=" + tid
                    + "&content=" + formatInput(content) + "&imgids=" + formatInput(imgIds)
                    + "&anonymous=" + anonymous;
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("uid", appContext.getmBlogUser().getUid());
            params.put("token", appContext.getmBlogUser().getToken());
            params.put("tid", tid);
            params.put("content", content);
            params.put("imgids", imgIds);
            params.put("anonymous", anonymous);
            url += "&sign=" + Sign.sign(params);

//            Log.i("rzf", "url:pubBlogReply--" + url);
            String response = HttpHelper.get(url, URLs.ENCODE);
            JSONObject jsonObject = JSON.parseObject(response);
            int code = jsonObject.getInteger("Code");
            if (code == 0) {
                MobclickAgent.onEvent(appContext, "NetWorkRequest_Action:blogpubreply");
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw AppException.run(e);
        }
    }

    /**
     * 部落发布评论
     *
     * @param appContext
     * @param rid
     * @param content
     * @param anonymous
     * @return
     * @throws AppException
     */
    public static boolean pubBlogComment(AppContext appContext, int rid,
                                         String content, int anonymous) throws AppException {
        try {
            String url = URLs.BLOGPUBCOMMENT + "uid="
                    + appContext.getmBlogUser().getUid() + "&token="
                    + formatInput(appContext.getmBlogUser().getToken()) + "&rid=" + rid
                    + "&content=" + formatInput(content) + "&anonymous="
                    + anonymous;
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("uid", appContext.getmBlogUser().getUid());
            params.put("token", appContext.getmBlogUser().getToken());
            params.put("rid", rid);
            params.put("content", content);
            params.put("anonymous", anonymous);
            url += "&sign=" + Sign.sign(params);

//            Log.v("rzf", "url:pubBlogComment--" + url);
            String response = HttpHelper.get(url, URLs.ENCODE);
            JSONObject jsonObject = JSON.parseObject(response);
            int code = jsonObject.getInteger("Code");
            if (code == 0) {
                MobclickAgent.onEvent(appContext, "NetWorkRequest_Action:blogpubcomment");
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw AppException.run(e);
        }
    }

    /**
     * 部落赞
     *
     * @param appContext
     * @param tid
     * @param rid
     * @return
     * @throws AppException
     */
    public static int pubLike(AppContext appContext, int tid, int rid) throws AppException {
        try {
            String url = URLs.BLOGLIKE + "uid=" + appContext.getmBlogUser().getUid();
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("uid", appContext.getmBlogUser().getUid());
            if (tid == -1) {
                url += "&rid=" + rid;
                params.put("rid", rid);
            } else {
                url += "&tid=" + tid;
                params.put("tid", tid);
            }
            url += "&sign=" + Sign.sign(params);

//            Log.i("rzf", "praise url:" + url);
            String response = HttpHelper.get(url, URLs.ENCODE);
            JSONObject jsonObject = JSON.parseObject(response);
            int code = jsonObject.getInteger("Code");
            if (code == 1) {
                MobclickAgent.onEvent(appContext, "NetWorkRequest_Action:bloglike");
            }
            return code;
        } catch (Exception e) {
            e.printStackTrace();
            throw AppException.run(e);
        }
    }

    /**
     * 部落回复列表
     *
     * @param appContext
     * @param tid
     * @param lastrid
     * @return
     * @throws AppException
     */
    public static List<BlogReplyBean> getBlogReplyList(AppContext appContext, int tid, int lastrid) throws AppException {
        try {
            String url = URLs.BLOGREPLYLIST + "tid=" + tid + "&lastrid=" + lastrid;
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("tid", tid);
            params.put("lastrid", lastrid);
            url += "&sign=" + Sign.sign(params);

//			Log.i(TAG, "url:" + url);
            String response = HttpHelper.get(url, URLs.ENCODE);
            JSONObject jsonObject = JSON.parseObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("list");
            List<BlogReplyBean> blogReplyBeans = new ArrayList<BlogReplyBean>();
            for (int i = 0; i < jsonArray.size(); i++) {
                blogReplyBeans.add(jsonArray.getObject(i, BlogReplyBean.class));
            }
            MobclickAgent.onEvent(appContext, "NetWorkRequest_Action:blogreplylist");
            return blogReplyBeans;
        } catch (Exception e) {
            e.printStackTrace();
            throw AppException.run(e);
        }
    }

    public static BlogReplyBean getBlogReply(AppContext appContext, int rid) throws AppException {
        try {
            String url = URLs.BLOGREPLY + "rid=" + rid;
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("rid", rid);
            url += "&sign=" + Sign.sign(params);

//			Log.i(TAG, "url:" + url);
            String response = HttpHelper.get(url, URLs.ENCODE);
            JSONObject jsonObject = JSONObject.parseObject(response);
            MobclickAgent.onEvent(appContext, "NetWorkRequest_Action:blogreply");
            return BlogReplyBean.parse(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            throw AppException.run(e);
        }

    }

    /**
     * 部落评论列表
     *
     * @param appContext
     * @param rid
     * @param lastcid
     * @return
     * @throws AppException
     */
    public static List<BlogCommentBean> getMoreBlogReply(AppContext appContext, int rid, int lastcid) throws AppException {
        try {
            String url = URLs.BLOGCOMMENT + "rid=" + rid + "&lastcid=" + lastcid;
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("rid", rid);
            params.put("lastcid", lastcid);
            url += "&sign=" + Sign.sign(params);

//			Log.i(TAG, "url:" + url);
            String response = HttpHelper.get(url, URLs.ENCODE);
            JSONObject jsonObject = JSONObject.parseObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("list");
            List<BlogCommentBean> list = new ArrayList<BlogCommentBean>();
            for (int i = 0; i < jsonArray.size(); i++) {
                list.add(jsonArray.getObject(i, BlogCommentBean.class));
            }
            MobclickAgent.onEvent(appContext, "NetWorkRequest_Action:blogcommentlist");
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            throw AppException.run(e);
        }
    }

    /**
     * 部落举报
     *
     * @param appContext
     * @param rid
     * @param tid
     * @return
     * @throws AppException
     */
    public static boolean blogReport(AppContext appContext, int rid, int tid) throws AppException {
        try {
            String url = URLs.BLOGREPORT + "uid=" + appContext.getmBlogUser().getUid();
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("uid", appContext.getmBlogUser().getUid());
            if (tid == -1) {
                url += "&rid=" + rid;
                params.put("rid", rid);
            } else {
                url += "&tid=" + tid;
                params.put("tid", tid);
            }
            url += "&sign=" + Sign.sign(params);

            String response = HttpHelper.get(url, URLs.ENCODE);
            JSONObject jsonObject = JSON.parseObject(response);
            int code = jsonObject.getInteger("Code");
//            Log.v("rzf", "url-blogReport-jsonObject = " + jsonObject);
            if (code == 0) {
                MobclickAgent.onEvent(appContext, "NetWorkRequest_Action:blogreport");
                return true;
            } else {
                return false;
            }
        }catch(Exception e){
            e.printStackTrace();
            throw AppException.run(e);
        }
    }

    /**
     * 部落回复信息
     * @param appContext
     * @param rmid
     * @return
     * @throws AppException
     */
    public static List<BlogMessBean> blogReplyMessage(AppContext appContext, int rmid) throws AppException{
        try {
            String url = URLs.BLOGREPLYMESSAGE + "uid=" + appContext.getmBlogUser().getUid() + "&rmid=" + rmid;
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("uid", appContext.getmBlogUser().getUid());
            params.put("rmid", rmid);
            url += "&sign=" + Sign.sign(params);

            //Log.i(TAG, "url:" + url);
            String response = HttpHelper.get(url, URLs.ENCODE);
            JSONObject jsonObject = JSON.parseObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("list");
            List<BlogMessBean> list = new ArrayList<BlogMessBean>();
            for (int i = 0; i < jsonArray.size(); i++) {
                list.add(jsonArray.getObject(i, BlogMessBean.class));
            }
            MobclickAgent.onEvent(appContext, "NetWorkRequest_Action:blogreplymessage");
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            throw AppException.run(e);
        }
    }

    /**
     * 部落上传回复
     * @param appContext
     * @param content
     * @param cid
     * @param rid
     * @param tid
     * @param toUid
     * @param anonymous
     * @return
     * @throws AppException
     */
    public static boolean pubPostReply(AppContext appContext, String content, int cid, int rid, int tid, int toUid, int anonymous) throws AppException {
        try {
            String url = URLs.BlogPostReply + "fmuid=" + appContext.getmBlogUser().getUid() + "&touid=" + toUid + "&content=" + formatInput(content) + "&anonymous=" + anonymous;
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("fmuid", appContext.getmBlogUser().getUid());
            params.put("touid", toUid);
            params.put("content", content);
            params.put("anonymous", anonymous);
            if (tid == -1) {
                url += "&rid=" + rid + "&cid=" + cid;
                params.put("rid", rid);
                params.put("cid", cid);
            } else {
                url += "&tid=" + tid + "&rid=" + rid;
                params.put("tid", tid);
                params.put("rid", rid);
            }
            url += "&sign=" + Sign.sign(params);

//			Log.i(TAG, "url:" + url);
            String response = HttpHelper.get(url, URLs.ENCODE);
            JSONObject jsonObject = JSON.parseObject(response);
            int code = jsonObject.getInteger("Code");
            if (code == 0) {
                MobclickAgent.onEvent(appContext, "NetWorkRequest_Action:blogpostreply");
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw AppException.run(e);
        }
    }

    /**
     * 部落回复数量
     * @param appContext
     * @return
     * @throws AppException
     */
    public static int getReplyCount(AppContext appContext) throws AppException{
        try{
            int count = 0;
            String	url = URLs.GetReplyCount + "uid=" + appContext.getmBlogUser().getUid();
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("uid", appContext.getmBlogUser().getUid());
            url += "&sign=" + Sign.sign(params);

            //Log.i(TAG, "url:" + url);
            String response = HttpHelper.get(url, URLs.ENCODE);
            JSONObject jsonObject = JSON.parseObject(response);
            int code = jsonObject.getInteger("Code");
            if (code == 0) {
                count =  jsonObject.getInteger("count");
            }
            MobclickAgent.onEvent(appContext, "NetWorkRequest_Action:bloggetreplycount");
            return count;
        }catch(Exception e){
            e.printStackTrace();
            throw AppException.run(e);
        }
    }

    /**
     * 改变关注部落
     * @param appContext
     * @param gtype
     * @return
     * @throws AppException
     */
    public static boolean blogChangeSection(AppContext appContext,String gtype) throws AppException {
        try{
            String url = URLs.ChangeSection + "uid=" + appContext.getmBlogUser().getUid() + "&gtype=" + formatInput(gtype);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("uid", appContext.getmBlogUser().getUid());
            params.put("gtype", gtype);
            url += "&sign=" + Sign.sign(params);

            //Log.i(TAG, "url:" + url);
            String response = HttpHelper.get(url, URLs.ENCODE);
            JSONObject jsonObject = JSON.parseObject(response);
            int code = jsonObject.getInteger("Code");
            if (code == 0) {
                MobclickAgent.onEvent(appContext, "NetWorkRequest_Action:blogchangesection");
                return true;
            }else{
                return false;
            }
        }catch(Exception e){
            e.printStackTrace();
            throw AppException.run(e);
        }
    }

    /**
     * 校验手机号
     *
     * @param appContext
     * @param mobile 手机号码
     * @return 0为可以用，1为不可用
     * @throws AppException
     */
    public static int checkMobile(AppContext appContext, String mobile) throws AppException{

        try {
            String url = URLs.CHECKMOBILE + "mobile=" + formatInput(mobile);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("mobile", mobile);
            url += "&sign=" + Sign.sign(params);

//            Log.v(TAG, "checkMobile--url: " + url);
            String response = HttpHelper.get(url, URLs.ENCODE);
            JSONObject jsonObject = JSONObject.parseObject(response);
            int code = jsonObject.getIntValue("Code");
            MobclickAgent.onEvent(appContext, "NetWorkRequest_Action:checkmobile");
            return code;
        } catch (AppException e) {
            e.printStackTrace();
            throw AppException.run(e);
        }
    }

    /**
     * 校验验证码
     *
     * @param appContext
     * @param mobile 手机号码
     * @param zone 区域（中国大陆86）
     * @param code 验证码
     * @return 0成功，1失败
     * @throws AppException
     */
    public static int checkSMS(AppContext appContext, String mobile, String zone, String code) throws AppException{
        try {
            String url = URLs.CHECKSMS + "mobile=" + formatInput(mobile) + "&zone=" + formatInput(zone) + "&code=" + formatInput(code);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("mobile", mobile);
            params.put("zone", zone);
            params.put("code", code);
            url += "&sign=" + Sign.sign(params);

//            Log.v(TAG, "AppClientV2--checkSMS--url:" + url);
            String response = HttpHelper.get(url, URLs.ENCODE);
            JSONObject jsonObject = JSONObject.parseObject(response);
            MobclickAgent.onEvent(appContext, "NetWorkRequest_Action:checkSMS");
            return jsonObject.getIntValue("Code");
        } catch (AppException e) {
            e.printStackTrace();
            throw AppException.run(e);
        }
    }
    /**
     * 老用户绑定手机号
     *
     * @param appContext
     * @param user_id
     * @param token
     * @param type 绑定手机号为1，绑定qq号为2，绑定微博为3，绑定微信为4
     * @param thirdUid 可以是手机号码，QQ授权用户ID，微博授权用户ID，微信授权用户ID
     * @param thirdToken 依次为null，QQ授权用户token，微博授权用户token，微信授权用户token
     * @return
     * @throws AppException
     */
    public static boolean bindMobileAccount(AppContext appContext, int user_id, String token, int type, String thirdUid, String thirdToken) throws AppException{
        boolean isSuccess = false;
        String url = URLs.BINDINGACCOUNT + "user_id=" + user_id + "&token=" + formatInput(token) + "&type=" + type;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("user_id", user_id);
        params.put("token", token);
        params.put("type", type);
        switch (type) {
            case 1:
                url = url + "&mobile=" + formatInput(thirdUid);
                params.put("mobile", thirdUid);
                break;
            case 2:
                url = url + "&qquid=" + formatInput(thirdUid) + "&qqtoken=" + formatInput(thirdToken);
                params.put("qquid", thirdUid);
                params.put("qqtoken", thirdToken);
                break;
            case 3:
                url = url + "&wbuid=" + formatInput(thirdUid) + "&wbtoken=" + formatInput(thirdToken);
                params.put("wbuid", thirdUid);
                params.put("wbtoken", thirdToken);
                break;
            case 4:
                url = url + "&wxuid=" + formatInput(thirdUid) + "&wxtoken=" + formatInput(thirdToken);
                params.put("wxuid", thirdUid);
                params.put("wxtoken", thirdToken);
                break;
            default:
                break;
        }
        url += "&sign=" + Sign.sign(params);
//        Log.v(TAG, "bindMobileAccount-url = " + url);
        try {
            String response = HttpHelper.get(url, URLs.ENCODE);
            JSONObject jsonObject = JSONObject.parseObject(response);
            isSuccess = jsonObject.getInteger("Code") == 0 ? true : false;
            MobclickAgent.onEvent(appContext, "NetWorkRequest_Action:bindaccount");
        } catch (AppException e) {
            e.printStackTrace();
            throw AppException.run(e);
        }

        return isSuccess;
    }

    /**
     * 活动主页面接口
     *
     * @param appContext
     * @param user_id 用户ID
     * @return
     * @throws AppException
     */
    public static NewActiveBean referenceIndex(AppContext appContext, int user_id) throws AppException{
        NewActiveBean newActiveBean = null;
        String url = URLs.REFERENCEINDEX + "user_id=" + user_id;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("user_id", user_id);
        url += "&sign=" + Sign.sign(params);
        try {
//            Log.v(TAG, "referenceIndex--" + url);
            String response = HttpHelper.get(url, URLs.ENCODE);
            JSONObject jsonObject = JSONObject.parseObject(response);
            newActiveBean = NewActiveBean.parse(jsonObject);
            MobclickAgent.onEvent(appContext, "NetWorkRequest_Action:referenceindex");
        } catch (Exception e) {
            e.printStackTrace();
            throw AppException.run(e);
        }

        return newActiveBean;
    }
    /**
     * 提交邀请码接口
     *
     * @param appContext
     * @param user_id 用户ID
     * @param refereeId 邀请码
     * @return
     * @throws AppException
     */
    public static Map<String, Object> checkReference(AppContext appContext, int user_id, int refereeId) throws AppException{
        Map<String, Object> map = null;
        String url = URLs.CHECKREFERENCE + "user_id=" + user_id + "&refereeid=" + refereeId;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("user_id", user_id);
        params.put("refereeid_id", refereeId);
        url += "&sign=" + Sign.sign(params);

        try {
            String response = HttpHelper.get(url, URLs.ENCODE);
            JSONObject jsonObject = JSONObject.parseObject(response);
            boolean isSuccess = jsonObject.getInteger("Code") == 0;
            String message = jsonObject.getString("Message");
            map = new HashMap<>();
            map.put("Code", isSuccess);
            map.put("Message", message);
            MobclickAgent.onEvent(appContext, "NetWorkRequest_Action:checkreference");
        } catch (AppException e) {
            e.printStackTrace();
            throw AppException.run(e);
        }

        return map;
    }

    /**
     * 兑换奖励
     *
     * @param appContext
     * @param user_id 用户ID
     * @param user_token 用户token
     * @param prizeId 奖品ID
     * @return
     * @throws AppException
     */
    public static Map<String, Object> getReferencePrize(AppContext appContext, int user_id, String user_token, int prizeId) throws AppException{
        Map<String, Object> map = null;
        String url = URLs.GETREFERENCEPRIZE + "user_id=" + user_id + "&user_token=" + formatInput(user_token) + "&prizeid=" + prizeId;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("user_id", user_id);
        params.put("user_token", user_token);
        params.put("prizeid", prizeId);
        url += "&sign=" + Sign.sign(params);
//        Log.v(TAG, "getReferencePrize--" + url);
        try {
            String response = HttpHelper.get(url, URLs.ENCODE);
            JSONObject jsonObject = JSONObject.parseObject(response);
            boolean isSuccess = jsonObject.getInteger("Code") == 0;
            String message = jsonObject.getString("Message");
            map = new HashMap<>();
            map.put("Code", isSuccess);
            map.put("Message", message);
            MobclickAgent.onEvent(appContext, "NetWorkRequest_Action:getreferenceprize");
        } catch (AppException e) {
            e.printStackTrace();
            throw AppException.run(e);
        }

        return map;
    }

    /**
     * 奖品列表
     *
     * @param appContext
     * @param user_id 用户ID
     * @return
     * @throws AppException
     */
    public static NewActivePrizeShowBean referencePrizeList(AppContext appContext, int user_id) throws AppException{
        NewActivePrizeShowBean newActivePrizeShowBean = null;
        String url = URLs.REFERENCEPRIZELIST + "user_id=" + user_id;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("user_id", user_id);
        url += "&sign=" + Sign.sign(params);

//        Log.v(TAG, "referencePrizeList--" + url);
        try {
            String response = HttpHelper.get(url, URLs.ENCODE);
            JSONObject jsonObject = JSONObject.parseObject(response);
            newActivePrizeShowBean = NewActivePrizeShowBean.parse(jsonObject);
            MobclickAgent.onEvent(appContext, "NetWorkRequest_Action:referenceprizelist");
        } catch (AppException e) {
            e.printStackTrace();
            throw AppException.run(e);
        }

        return newActivePrizeShowBean;
    }
    /**
     * 用户兑换记录
     *
     * @param appContext
     * @param user_id
     * @return
     * @throws AppException
     */
    public static List<NewActiveTradeBean> referenceuserrecord(AppContext appContext, int user_id, String user_token) throws AppException{
        List<NewActiveTradeBean> newActivieTradeBeans = null;
        String url = URLs.REFERENCEUSERRECORD + "user_id=" + user_id + "&user_token=" + formatInput(user_token);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("user_id", user_id);
        params.put("user_token", user_token);
        url += "&sign=" + Sign.sign(params);
//        Log.v(TAG, "referenceuserrecord--" + url);
        try {
            String response = HttpHelper.get(url, URLs.ENCODE);
            JSONObject jsonObject = JSONObject.parseObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("Records");
            newActivieTradeBeans = NewActiveTradeBean.parse(jsonArray);
            MobclickAgent.onEvent(appContext, "NetWorkRequest_Action:referenceuserrecord");
        } catch (AppException e) {
            e.printStackTrace();
            throw AppException.run(e);
        }

        return newActivieTradeBeans;
    }
    /**
     * 设置密码接口
     *
     * @param appContext
     * @param user_mobile 手机号码
     * @param password 密码
     * @return 结果码 0 为成功 1为失败
     * @throws AppException
     */
    public static boolean setPassword(AppContext appContext, String user_mobile, String password) throws AppException{
        boolean isSuccess = false;
        String url = URLs.SETPASSWORD + "user_mobile=" + formatInput(user_mobile) + "&password=" + formatInput(password);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("user_mobile", user_mobile);
        params.put("password", password);
        url += "&sign=" + Sign.sign(params);
        try {
            String response = HttpHelper.get(url, URLs.ENCODE);
            JSONObject jsonObject = JSONObject.parseObject(response);
            isSuccess = jsonObject.getInteger("Code") == 0;
            MobclickAgent.onEvent(appContext, "NetWorkRequest_Action:setpassword");
        } catch (Exception e) {
            e.printStackTrace();
            throw AppException.run(e);
        }
        return isSuccess;
    }

    /**
     * 获取直播在线状态
     *
     * @param appContext
     * @param ids
     * @return
     * @throws AppException
     */
    public static Map<String, Object> getLiveStateList(AppContext appContext, String ids)
            throws AppException {
        Map<String, Object> result = new HashMap<String, Object>();
        if(ids != null && !"".equals(ids)) {
            try {
                String url = URLs.LIVE_STATE_LIST + "ids=" + formatInput(ids);
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("ids", ids);
                url += "&sign=" + Sign.sign(params);

                String response = HttpHelper.get(url, URLs.ENCODE);
                JSONArray jsonArr = JSON.parseArray(response);
                if (jsonArr != null && jsonArr.size() > 0) {
                    for (int i = 0; i < jsonArr.size(); i++) {
                        JSONObject json = jsonArr.getObject(i, JSONObject.class);
                        result.put(json.getString("id"), json.getInteger("isonline"));
                    }
                }

                MobclickAgent.onEvent(appContext, "NetWorkRequest_Action:LiveStateList");
            } catch (Exception e) {
                e.printStackTrace();
                throw AppException.run(e);
            }
        }

        return result;
    }

    /**
     * 获取有米积分墙显示状态
     *
     * @param appContext
     * @return
     * @throws AppException
     */
    public static boolean getYMAdIsShown(AppContext appContext) throws AppException {
        boolean isShown = false;
        try {
            String url = URLs.SHOW_YM_AD;

            String response = HttpHelper.get(url, URLs.ENCODE);
            JSONObject jsonObject = JSON.parseObject(response);
            if(jsonObject != null) {
                isShown = jsonObject.getInteger("IsShow") == 1 ? true : false;
            }

            MobclickAgent.onEvent(appContext, "NetWorkRequest_Action:ymadswitch");
        } catch (Exception e) {
            e.printStackTrace();
            throw AppException.run(e);
        }

        return isShown;
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
