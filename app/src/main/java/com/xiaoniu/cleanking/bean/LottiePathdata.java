package com.xiaoniu.cleanking.bean;

/**
 * @author zhengzhihao
 * @date 2020/6/11 11
 * @mail：zhengzhihao@hellogeek.com
 */
public class LottiePathdata {
    private String jsonPath;
    private String imgPath;

    public LottiePathdata(String jsonPath, String imgPath) {
        this.jsonPath = jsonPath;
        this.imgPath = imgPath;
    }

    public String getJsonPath() {
        return jsonPath;
    }

    public void setJsonPath(String jsonPath) {
        this.jsonPath = jsonPath;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }
}
