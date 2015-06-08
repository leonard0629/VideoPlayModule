package com.jiuguo.app.bean;

import android.util.Log;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NewVideoUrl extends BaseBean {

    private final static String TAG = "NewVideoUrl";

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public static final int TypeYouku = 1;
    public static final int TypeLive = 2;
    public static final int TypeLocal = 3;
    private String baseURL;
    //acturally this is checkedid;
    private int videoId = -1;
    private String title;
    private int type = TypeYouku;

    private String[] playlist;
    /**
     * 用于判断是用旧接口还是新接口
     */
    private boolean isNew;

    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }

    private List<UrlBean> ListUrl = null;

    public List<UrlBean> getListUrl() {
        return ListUrl;
    }
    public void setListUrl(List<UrlBean> listUrl) {
        ListUrl = listUrl;
    }
    public String getBaseURL() {
        return baseURL;
    }
    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }
    public int getVideoId() {
        return videoId;
    }
    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public UrlBean getUrlBean(String type){
        if(ListUrl == null)  return null;
        for(UrlBean urlBean :ListUrl){
            if(urlBean.getType().equals(type)){
                return urlBean;
            }
        }
        return null;
    }
    public String[] getPlaylist() {
        return playlist;
    }
    public void setPlaylist(String[] playlist) {
        this.playlist = playlist;
    }

    public static NewVideoUrl parse(String json) {
        NewVideoUrl newvideoUrl = new NewVideoUrl();

        try {
            JSONObject object = JSONObject.parseObject(json);
            JSONObject sizeInfo = object.getJSONObject("size");
            JSONArray playlist = object.getJSONArray("playlist");
            List<UrlBean> listUrl = new ArrayList<UrlBean>();
            if(sizeInfo.containsKey("normal")&&sizeInfo.getLong("normal")!=0){
                UrlBean urlBean = new UrlBean();
                urlBean.setType("flv");
                urlBean.setShowName("标清");
                urlBean.setSize((int) ((sizeInfo.getLong("normal") / 1024 / 1024)));
                listUrl.add(urlBean);
            }
            if(sizeInfo.containsKey("high")&&sizeInfo.getLong("high")!=0){
                UrlBean urlBean = new UrlBean();
                urlBean.setType("mp4");
                urlBean.setShowName("高清");
                urlBean.setSize((int) ((sizeInfo.getLong("high") / 1024 / 1024)));
                listUrl.add(urlBean);
            }
            if(sizeInfo.containsKey("super")&&sizeInfo.getLong("super")!=0){
                UrlBean urlBean = new UrlBean();
                urlBean.setType("hd2");
                urlBean.setShowName("超清");
                urlBean.setSize((int) ((sizeInfo.getLong("super") / 1024 / 1024)));
                listUrl.add(urlBean);
            }
            newvideoUrl.setListUrl(listUrl);

            String[] plist = new String[playlist.size()];
            for(int i = 0;i<playlist.size();i++){
                plist[i]= playlist.getString(i);
            }
            newvideoUrl.setPlaylist(plist);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "get exception when parse, cause: " + e.getMessage());
        }


        return newvideoUrl;
    }

    public boolean isNew() {
        return isNew;
    }
    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }
}