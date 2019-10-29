package com.xiaoniu.cleanking.utils;

/**
 * Created by fengpeihao on 2017/6/5.
 */

public class MessageEvent {
    private String type;
    private String message;
    private String str_extra;
    private String str_extra2;
    private int index;


    public MessageEvent(String type, int index) {
        this.type = type;
        this.index = index;
    }


    private String path;
    private String name;
    private String url;
    private String title;
    private int upmoney;

    public int getUpmoney() {
        return upmoney;
    }

    public void setUpmoney(int upmoney) {
        this.upmoney = upmoney;
    }

    private boolean isChanged;

    public MessageEvent(String type) {
        this.type = type;
    }

    public MessageEvent(String type, boolean isChanged) {
        this.type = type;
        this.isChanged = isChanged;
    }

    public MessageEvent(String path, String name, String url, String title) {
        this.path = path;
        this.name = name;
        this.url = url;
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public MessageEvent() {
    }

    public boolean isChanged() {
        return isChanged;
    }

    public void setChanged(boolean changed) {
        isChanged = changed;
    }

    public MessageEvent(String type, String message) {
        this.type = type;
        this.message = message;
    }

    public MessageEvent(String type, String message, String str_extra) {
        this.type = type;
        this.str_extra = str_extra;
        this.message = message;
    }

    public MessageEvent(String type, String message, int upmoney) {
        this.type = type;
        this.upmoney = upmoney;
        this.message = message;
    }


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setStr_extra2(String str_extra2) {
        this.str_extra2 = str_extra2;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public String getStr_extra() {
        return str_extra;
    }

    public String getStr_extra2() {
        return str_extra2;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
