package com.xiaoniu.cleanking.jsbridge;

/**
 * 代码描述<p>
 *
 * @author anhuiqing
 * @since 2019/6/6 13:12
 */
public enum JsBridgeEnums {
    /**
     * 获取http 头部信息
     */
    HEADERS("headers"),
    /**
     * token 过期
     */
    TOKEN_OVERDUA("token_overdue"),
    /**
     * 分享
     */
    SHARE("share"),

    /**
     * 打开弹窗Webview
     */
    OPNE_DIALOG_WEBVIEW("opne_dialog_webview"),
    /**
     * 关闭弹窗Webview
     */
    CLOSE_DIALOG_WEBVIEW("close_dialog_webview"),
    /**
     * 刷新金币、人民币
     */
    REFRESH_RMB_COIN("refresh_rmb_coin"),
    /**
     * WebView - finish
     */
    WEB_PAGE_FINISH("web_page_finish"),
    /**
     * WebView - go back
     */
    WEB_PAGE_GO_BACK("web_page_go_back"),
    /**
     * login
     */
    LOGIN("login"),
    /**
     * 是否登录
     */
    ISLOGIN("is_login"),
    /**
     * start webview
     */
    START_WEBVIEW("start_webview"),
    /**
     * start invitecode
     */
    START_INVITECODE("start_invitecode"),
    /**
     * 点击红包播放激励视频
     */
    WEB_REDPACKET_AD("web_redpacket_ad");

    private String operate;

    private JsBridgeEnums(String operate) {
        this.operate = operate;
    }

    private String getOperate() {
        return operate;
    }

    public static JsBridgeEnums val(String operate) {
        for (JsBridgeEnums s : values()) {    //values()方法返回enum实例的数组
            if (operate.equals(s.getOperate()))
                return s;
        }
        return null;
    }
}
