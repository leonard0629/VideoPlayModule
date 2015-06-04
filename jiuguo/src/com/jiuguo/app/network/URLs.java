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
     * ����ӿ�-������ҳ�ӿ�
     * ������ҳ���ݽӿ�
     */
    public static final String BlogIndex = HOST_V3 + "action=blogindex?";
    /**
     * ����ӿ�-��¼�ӿ�
     * �ж��û��Ƿ��¼APP�����û���¼APPʱ���øýӿڻ�ȡ�û�������Ϣ�����򲻽��е���
     */
    public static final String BlogLogin = HOST_V3 + "action=bloglogin?";
    /**
     * ����ӿ�-�����б�ӿ�
     */
    public static final String BLOGTOPICLIST = HOST_V3 + "action=blogtopiclist?";
    /**
     * ����ӿ�-��������ӿ�
     */
    public static final String BlogPubTopic = HOST_V3 + "action=blogpubtopic?";
    /**
     * ����ӿ�-��������ҳ�ӿ�
     */
    public static final String BlOGTOPIC = HOST_V3 + "action=blogtopic?";
    /**
     * ����ӿ�-�������ظ���ظ�
     * ��������ҳ�������ظ���ظ�
     */
    public static final String BLOGREPLYLIST = HOST_V3 + "action=blogreplylist?";
    /**
     * ����ӿ�-�ظ�����ҳ
     * ����ظ�����ظ�������ҳ���ô˽ӿڣ�Ĭ�Ϸ���20������
     */
    public static final String BLOGREPLY = HOST_V3 + "action=blogreply?";
    /**
     * ����ӿ�-�������ظ�������
     * ���ظ������ۣ�ÿ�μ���20��
     */
    public static final String BLOGCOMMENT = HOST_V3 + "action=blogcommentlist?";
    /**
     * ����ӿ�-�ϴ�ͼƬ�ӿ�
     */
    public static final String BlOGUPLOAD = HOST_UPLOAD + "action=blogupload?";
    /**
     * ����ӿ�-�����ظ��ӿ�
     */
    public static final String BLOGPUBREPLY = HOST_V3 + "action=blogpubreply?";
    /**
     * ����ӿ�-�������۽ӿ�
     */
    public static final String BLOGPUBCOMMENT = HOST_V3 + "action=blogpubcomment?";
    /**
     * ����ȡ���޽ӿ�
     */
    public static final String BLOGLIKE = HOST_V3 + "action=bloglike?";
    /**
     * ����ӿ�-�ظ�������Ϣ�б�
     * �û��յ��Ļظ������б�
     */
    public static final String BLOGREPLYMESSAGE = HOST_V3 + "action=blogreplymessage?";
    /**
     * �ٱ��ӿ�
     */
    public static final String BLOGREPORT = HOST_V3 + "action=blogreport?";
    /**
     * ����ӿ�-�ظ��ӿ�
     */
    public static final String BlogPostReply = HOST_V3 + "action=blogpostreply?";
    /**
     * ����ӿ�-��ȡ���»ظ������ӿ�
     */
    public static final String GetReplyCount = HOST_V3 + "action=bloggetreplycount?";
    /**
     * ����ӿ�-�޸��û���ע���ӿ�
     */
    public static final String ChangeSection = HOST_V3 + "action=blogchangesection?";

    /**
     * ��ȡ��Ƶ��ַ�½ӿ�
     */
    public static final String GetNewYouKu = HOST_V3 + "action=newvideourl?";
    /**
     * У���ֻ���
     */
    public static final String CHECKMOBILE = HOST_V3 + "action=checkmobile?";
    /**
     * У����֤��
     */
    public static final String CHECKSMS = HOST_V3 + "action=checkSMS?";
    /**
     * ���û��ֻ���QQ��΢����΢�Ű󶨽ӿ�
     */
    public static final String BINDINGACCOUNT = HOST_V3 + "action=bindaccount?";
    /**
     * ���ҳ��ӿ�
     */
    public static final String REFERENCEINDEX = HOST_V3 + "action=referenceindex?";
    /**
     * �ύ������ӿ�
     */
    public static final String CHECKREFERENCE = HOST_V3 + "action=checkreference?";
    /**
     * �һ�����
     */
    public static final String GETREFERENCEPRIZE = HOST_V3 + "action=getreferenceprize?";
    /**
     * ��Ʒ�б�
     */
    public static final String REFERENCEPRIZELIST = HOST_V3 + "action=referenceprizelist?";
    /**
     * �û��һ���¼
     */
    public static final String REFERENCEUSERRECORD = HOST_V3 + "action=referenceuserrecord?";
    /**
     * ��������ӿ�
     */
    public static final String SETPASSWORD = HOST_V3 + "action=setpassword?";

    /**
     * ��ѯֱ���Ƿ�����
     */
    public final static String LIVE_STATE_LIST = HOST_V3 + "action=LiveStateList?";

    /**
     * �Ƿ���ʾ���׻���ǽ
     */
    public final static String SHOW_YM_AD = HOST_V3 + "action=ymadswitch?";
}
