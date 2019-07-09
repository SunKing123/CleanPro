package com.xiaoniu.cleanking.utils;

import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Created by lang.chen on 2019/7/2
 */
public class MusicFileUtils {


    /**
     * 获取音频播放时长
     * @param path
     * @return
     */
    public static String getPlayDuration(String path) {
        MediaPlayer player = new MediaPlayer();
        String duration="";
        try {
            //recordingFilePath（）为音频文件的路径
            player.setDataSource(path);
            player.prepare();
            //获取音频的时间
            duration= timeParse(player.getDuration());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            player.release();
        }
        return duration;
    }


    /**
     * 获取音频播放时长
     * @param path
     * @return
     */
    public static String getPlayDuration2(String path) {
        MediaPlayer player = new MediaPlayer();
        String duration="";
        try {
            //recordingFilePath（）为音频文件的路径
            player.setDataSource(path);
            player.prepare();
            //获取音频的时间
            duration= timeParse2(player.getDuration());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            player.release();
        }


        return duration;
    }


    /**
     *
     * @param duration 音乐时长
     * @return
     */
    public static String timeParse(long duration) {
        String time = "" ;
        long minute = duration / 60000 ;
        long seconds = duration % 60000 ;
        long second = Math.round((float)seconds/1000) ;
        if( minute < 10 ){
            time += "0" ;
        }
        time += minute+"''" ;
        if( second < 10 ){
            time += "0" ;
        }
        time += second+"'" ;
        return time ;
    }

    /**
     *
     * @param duration 音乐时长
     * @return
     */
    public static String timeParse2(long duration) {
        String time = "" ;
        long minute = duration / 60000 ;
        long seconds = duration % 60000 ;
        long second = Math.round((float)seconds/1000) ;
        if( minute < 10 ){
            time += "0" ;
        }
        time += minute+"分" ;
        if( second < 10 ){
            time += "0" ;
        }
        time += second+"秒" ;
        return time ;
    }
}
