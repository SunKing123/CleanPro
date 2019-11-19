package com.geek.webpage.entity;

import java.io.Serializable;

/**
 * 代码描述<p>
 *
 * @author anhuiqing
 * @since 2019/6/24 11:00
 */
public class WebPageEntity implements Serializable {
    public String title = "";
    public String url = "";
    public boolean isShowTitleBar = true;
    public boolean isDarkFont;
    public boolean isUserRead;
    public String type;
}
