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
        try {
            //recordingFilePath（）为音频文件的路径
            player.setDataSource(path);
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //获取音频的时间
        String duration = timeParse(player.getDuration());
        player.release();//记得释放资源

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
}
