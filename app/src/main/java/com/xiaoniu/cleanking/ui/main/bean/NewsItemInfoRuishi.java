package com.xiaoniu.cleanking.ui.main.bean;

import java.util.List;

/**
 * @author zhengzhihao
 * @date 2019/10/16 14
 * @mail：zhengzhihao@hellogeek.com
 */
public class NewsItemInfoRuishi {


    /**
     * update_time : 2019-10-15 17:22:42
     * voffset : 1571207603272
     * timestamp : 1571131362
     * offset :
     * source : 资讯播报
     * cat : 健康
     * score : 215
     * imp_tracking : ["http://m.yuexinwen.cn/bd/impm?ext=MgkyNgk0NwnlgaXlurcJCQkJCQkJCQkJCQk1OC4yNDcuMC4xMAkJCXpqUWYzTTdlYjNVTTE1NzEyMDc2MDMuMjcxCTEJLTEJMTcJd2xfN2Q3Yzg0MjhjOGIzOWQ1NTc2Yzk5MDM0ZWI2NDg0M2EJCQlzMDEwQjMwTUkzek1WSk5mZgkxNTcxMjA3NjAzCTEJ5YGl5bq3CQkJCQkJCQ==&r=jRYVQEz7"]
     * source_id : 17
     * ext : 17,
     * is_video : false
     * big_img : http://img.cashslide.cn/app/hippo/20191015/d141eb70ab840db61290208df3dc83467f2ddd78.jpeg
     * title : 健身狂魔孙俪又晒瑜伽照，网友：“健身博主又上线了”
     * is_ad : false
     * clk_url : http://m.yuexinwen.cn/bd/clkm?ext=MgkyNgk0NwnlgaXlurcJCQkJCQkJCQkJCQk1OC4yNDcuMC4xMAkJCXpqUWYzTTdlYjNVTTE1NzEyMDc2MDMuMjcxCTEJLTEJMTcJd2xfN2Q3Yzg0MjhjOGIzOWQ1NTc2Yzk5MDM0ZWI2NDg0M2EJCQlzMDEwQjMwTUkzek1WSk5mZgkxNTcxMjA3NjAzCTEJ5YGl5bq3CQkJCQkJCQ==&g=http%3A%2F%2Fm.yuexinwen.cn%2Fbd%2Fnews%2Fa%3Fsubmedia%3D47%26conn%3D%26category%3D%25E5%2581%25A5%25E5%25BA%25B7%26sid%3D17%26mac%3D%26anid%3D%26t%3D2%26sw%3D%26osv%3D%26carrier%3D%26ref%3D1%26model%3D%26ua%3D%26ip%3D58.247.0.10%26make%3D%26devicetype%3D%26sh%3D%26imei%3D%26newsid%3Dwl_7d7c8428c8b39d5576c99034eb64843a%26os%3D%26userid%3Ds010B30MI3zMVJNff%26media%3D26
     * detail_api_work : true
     * id : wl_7d7c8428c8b39d5576c99034eb64843a
     * images : [{"url":"http://img.cashslide.cn/app/hippo/20191015/d141eb70ab840db61290208df3dc83467f2ddd78.jpeg"},{"url":"http://img.cashslide.cn/app/hippo/20191015/fef8cd2b2ab360b7833376fc10ce815c8a8ea71e.gif"},{"url":"http://img.cashslide.cn/app/hippo/20191015/b2a6a2539ade38ebdd208916709f98d3dac2e6e1.jpeg"}]
     */

    private String update_time;
    private String voffset;
    private int timestamp;
    private String offset;
    private String source;
    private String cat;
    private int score;
    private String source_id;
    private String ext;
    private boolean is_video;
    private String big_img;
    private String title;
    private boolean is_ad;
    private String clk_url;
    private boolean detail_api_work;
    private String id;
    private List<String> imp_tracking;
    private List<ImagesBean> images;

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getVoffset() {
        return voffset;
    }

    public void setVoffset(String voffset) {
        this.voffset = voffset;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getCat() {
        return cat;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getSource_id() {
        return source_id;
    }

    public void setSource_id(String source_id) {
        this.source_id = source_id;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public boolean isIs_video() {
        return is_video;
    }

    public void setIs_video(boolean is_video) {
        this.is_video = is_video;
    }

    public String getBig_img() {
        return big_img;
    }

    public void setBig_img(String big_img) {
        this.big_img = big_img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isIs_ad() {
        return is_ad;
    }

    public void setIs_ad(boolean is_ad) {
        this.is_ad = is_ad;
    }

    public String getClk_url() {
        return clk_url;
    }

    public void setClk_url(String clk_url) {
        this.clk_url = clk_url;
    }

    public boolean isDetail_api_work() {
        return detail_api_work;
    }

    public void setDetail_api_work(boolean detail_api_work) {
        this.detail_api_work = detail_api_work;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getImp_tracking() {
        return imp_tracking;
    }

    public void setImp_tracking(List<String> imp_tracking) {
        this.imp_tracking = imp_tracking;
    }

    public List<ImagesBean> getImages() {
        return images;
    }

    public void setImages(List<ImagesBean> images) {
        this.images = images;
    }

    public static class ImagesBean {
        /**
         * url : http://img.cashslide.cn/app/hippo/20191015/d141eb70ab840db61290208df3dc83467f2ddd78.jpeg
         */

        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
