package com.xiaoniu.cleanking.ui.main.bean;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * @author XiLei
 * @date 2019/11/2.
 * description：保存选择的游戏加速应用
 */
@Entity(tableName = "GameSelect")
public class GameSelectEntity implements Serializable {

    @NotNull
    @PrimaryKey
    private int position;
    private String appName;
    private byte[] garbageIcon;

    public GameSelectEntity(int position, String appName, byte[] garbageIcon) {
        this.position = position;
        this.appName = appName;
        this.garbageIcon = garbageIcon;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public byte[] getGarbageIcon() {
        return garbageIcon;
    }

    public void setGarbageIcon(byte[] garbageIcon) {
        this.garbageIcon = garbageIcon;
    }
}
