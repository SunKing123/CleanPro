package com.hellogeek.permission.Integrate;

public enum Permission {
    // 无障碍权限 有权查看应用使用情况
    SUSPENDEDTOAST("悬浮框", "桌面快捷图标，及时消除残留垃圾", 1001),
    SELFSTARTING("自启动", "实时保护手机，防御未知风险", 1002),
    LOCKDISPALY("锁屏显示"),
    BACKSTAGEPOPUP("后台弹出界面"),
    SYSTEMSETTING("允许修改系统设置"),
    REPLACEACLLPAGE("替换来电页面"),
    NOTIFICATIONBAR("通知管理", "清理烦人通知，拦截骚扰消息", 1003),
    NOTICEOFTAKEOVER("接管来电通知"),
    PACKAGEUSAGESTATS("查看应用使用情况", "释放更多空间，告别卡顿烦恼", 1004);

    private String permissionName;

    private String permissionDesc;

    private Integer requestCode;

    Permission(String permissionName) {
        this.permissionName = permissionName;
    }

    Permission(String permissionName, String permissionDesc, int requestCode) {
        this.permissionName = permissionName;
        this.permissionDesc = permissionDesc;
        this.requestCode = requestCode;
    }

    public String getName() {
        return permissionName;
    }

    public String getPermissionDesc() {
        return permissionDesc;
    }

    public Integer getRequestCode() {
        return requestCode;
    }

}
