package com.hellogeek.permission.strategy;

import com.hellogeek.permission.Integrate.Permission;

public class PathEvent {
    protected Permission permission;
    protected boolean isOpen;
    protected boolean isBack;
    protected int isAuto;//0:手动 1：自动

    public PathEvent(Permission permission, boolean isOpen) {
        this.permission = permission;
        this.isOpen = isOpen;
    }

    public PathEvent(Permission permission, boolean isOpen,int isAuto) {
        this.permission = permission;
        this.isOpen = isOpen;
        this.isAuto = isAuto;
    }

    public PathEvent(Permission permission, boolean isOpen, boolean isBack) {
        this.permission = permission;
        this.isOpen = isOpen;
        this.isBack = isBack;
    }

    public PathEvent(boolean isOpen) {
        this.isOpen = isOpen;
    }


    public boolean getIsBack() {
        return isBack;
    }

    public boolean getIsOpen() {
        return isOpen;
    }
    public int getIsAuto() {
        return isAuto;
    }

    public Permission getPermission() {
        return permission;
    }
}
