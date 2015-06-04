package com.jiuguo.app.network;

public class URLs {
    public static final String ENCODE = "utf-8";

    public static final String HOST_IP = "";

    /**
     * development
     */
    private final static String HOST_DEVELOPMENT = "http://testapi.jiuguo2009.cn/api/";
    /**
     * production
     */
    private final static String HOST_PRODUCTION = "http://api.jiuguo2009.cn/api/";

    private static final String HOST_V3 = HOST_DEVELOPMENT;
    private static final String HOST_V2 = "http://jiuguo2009.cn/openapi_v2.aspx?";

    private static final String HOST_UPLOAD = "http://upload.jiuguo2009.cn/api/";

    public static final String HOMEURL_V2 = HOST_V3 + "action=index2?";
    public static final String REGISTER_V3 = HOST_V3 + "action=register?";
    public static final String LOGIN_V3 = HOST_V3 + "action=login?";
    public static final String USERINFO_V2 = HOST_V3 + "action=userinfo?";
    public static final String CHANGE_PWD_V2 = HOST_V3 + "action=changepw?";
    public static final String CHECK_POST_V2 = HOST_V3 + "action=postcheck?";
    public static final String CHANNELINFO_V2 = HOST_V3 + "action=channel?";
    public static final String VIDEOINFO_V2 = HOST_V3 + "action=videoinfo?";
    public static final String RECOMMANDVIDEO_V2 = HOST_V3 + "action=recommendvideo?";
    public static final String VIDEOURL_V2 = HOST_V3 + "action=videourl?";
    public static final String HOTINFO_V3 = HOST_V3 + "action=hot2?";
    public static final String SUBSCRIBE_POST_V2 = HOST_V3 + "action=postsubscribe?";
    public static final String SUBSCRIBE_GET_V2 = HOST_V3 + "action=getsubscribe?";
    public static final String SUBSCRIBE_DEL_V2 = HOST_V3 + "action=delsubscribe?";
    public static final String BARRAGE_POST_V2 = HOST_V3 + "action=postbarrage?";
    public static final String BARRAGE_GET_V2 = HOST_V3 + "action=getbarrage?";

    public static final String WEIXINTRADE_GET_V2 = HOST_V2 + "action=weixinpay";
    public static final String WEIXINPAYSTATE_GET_V2 = HOST_V2 + "action=weixinpaystate";
    public static final String ALIPAYTRADE_GET_V2 = HOST_V2 + "action=alipay";
    public static final String POSTAPPLE = HOST_V3 + "action=postapple?";
    public static final String GETVIDEOAPPLE = HOST_V3 + "action=getvideoapple?";
    public static final String GETLIVEURL = HOST_V3 + "action=getliveurl?";
    public static final String GETLIVESUB = HOST_V3 + "action=getlivesub?";
    public static final String POSTLIVESUB = HOST_V3 + "action=postlivesub?";
    public static final String DELLIVESUB = HOST_V3 + "action=dellivesub?";

    /**
     * 部落接口-部落首页接口
     * 部落首页数据接口
     */
    public static final String BlogIndex = HOST_V3 + "action=blogindex?";
    /**
     * 部落接口-登录接口
     * 判断用户是否登录APP，当用户登录APP时调用该接口获取用户部落信息，否则不进行调用
     */
    public static final String BlogLogin = HOST_V3 + "action=bloglogin?";
    /**
     * 部落接口-话题列表接口
     */
    public static final String BLOGTOPICLIST = HOST_V3 + "action=blogtopiclist?";
    /**
     * 部落接口-发布话题接口
     */
    public static final String BlogPubTopic = HOST_V3 + "action=blogpubtopic?";
    /**
     * 部落接口-话题详情页接口
     */
    public static final String BlOGTOPIC = HOST_V3 + "action=blogtopic?";
    /**
     * 部落接口-上拉加载更多回复
     * 话题详情页上拉加载更多回复
     */
    public static final String BLOGREPLYLIST = HOST_V3 + "action=blogreplylist?";
    /**
     * 部落接口-回复详情页
     * 点击回复进入回复的详情页调用此接口，默认返回20条评论
     */
    public static final String BLOGREPLY = HOST_V3 + "action=blogreply?";
    /**
     * 部落接口-上拉加载更多评论
     * 加载更多评论，每次加载20条
     */
    public static final String BLOGCOMMENT = HOST_V3 + "action=blogcommentlist?";
    /**
     * 部落接口-上传图片接口
     */
    public static final String BlOGUPLOAD = HOST_UPLOAD + "action=blogupload?";
    /**
     * 部落接口-发布回复接口
     */
    public static final String BLOGPUBREPLY = HOST_V3 + "action=blogpubreply?";
    /**
     * 部落接口-发布评论接口
     */
    public static final String BLOGPUBCOMMENT = HOST_V3 + "action=blogpubcomment?";
    /**
     * 赞与取消赞接口
     */
    public static final String BLOGLIKE = HOST_V3 + "action=bloglike?";
    /**
     * 部落接口-回复评论消息列表
     * 用户收到的回复评论列表
     */
    public static final String BLOGREPLYMESSAGE = HOST_V3 + "action=blogreplymessage?";
    /**
     * 举报接口
     */
    public static final String BLOGREPORT = HOST_V3 + "action=blogreport?";
    /**
     * 部落接口-回复接口
     */
    public static final String BlogPostReply = HOST_V3 + "action=blogpostreply?";
    /**
     * 部落接口-获取最新回复数量接口
     */
    public static final String GetReplyCount = HOST_V3 + "action=bloggetreplycount?";
    /**
     * 部落接口-修改用户关注板块接口
     */
    public static final String ChangeSection = HOST_V3 + "action=blogchangesection?";

    /**
     * 获取视频地址新接口
     */
    public static final String GetNewYouKu = HOST_V3 + "action=newvideourl?";
    /**
     * 校验手机号
     */
    public static final String CHECKMOBILE = HOST_V3 + "action=checkmobile?";
    /**
     * 校验验证码
     */
    public static final String CHECKSMS = HOST_V3 + "action=checkSMS?";
    /**
     * 老用户手机，QQ，微博，微信绑定接口
     */
    public static final String BINDINGACCOUNT = HOST_V3 + "action=bindaccount?";
    /**
     * 活动主页面接口
     */
    public static final String REFERENCEINDEX = HOST_V3 + "action=referenceindex?";
    /**
     * 提交邀请码接口
     */
    public static final String CHECKREFERENCE = HOST_V3 + "action=checkreference?";
    /**
     * 兑换奖励
     */
    public static final String GETREFERENCEPRIZE = HOST_V3 + "action=getreferenceprize?";
    /**
     * 奖品列表
     */
    public static final String REFERENCEPRIZELIST = HOST_V3 + "action=referenceprizelist?";
    /**
     * 用户兑换记录
     */
    public static final String REFERENCEUSERRECORD = HOST_V3 + "action=referenceuserrecord?";
    /**
     * 设置密码接口
     */
    public static final String SETPASSWORD = HOST_V3 + "action=setpassword?";

    /**
     * 查询直播是否在线
     */
    public final static String LIVE_STATE_LIST = HOST_V3 + "action=LiveStateList?";

    /**
     * 是否显示有米积分墙
     */
    public final static String SHOW_YM_AD = HOST_V3 + "action=ymadswitch?";
}
