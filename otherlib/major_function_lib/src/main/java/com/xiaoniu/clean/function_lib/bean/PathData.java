package com.xiaoniu.clean.function_lib.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * @author zhengzhihao
 * @date 2019/10/15 15
 * @mail：zhengzhihao@hellogeek.com
 */
public class PathData implements Parcelable {


    /**
     * packName : com.tencent.mm
     * appName : 微信
     * fileList : [{"folderName":"Tencent","type":1}]
     */

    private String packName;
    private String appName;
    private List<FileListBean> fileList;

    public String getPackName() {
        return packName;
    }

    public void setPackName(String packName) {
        this.packName = packName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public List<FileListBean> getFileList() {
        return fileList;
    }

    public void setFileList(List<FileListBean> fileList) {
        this.fileList = fileList;
    }




    public static class FileListBean implements Parcelable {
        /**
         * folderName : Tencent
         * type : 1
         */

        private String folderName;
        private int type;

        public String getFolderName() {
            return folderName;
        }

        public void setFolderName(String folderName) {
            this.folderName = folderName;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.folderName);
            dest.writeInt(this.type);
        }

        public FileListBean() {
        }

        protected FileListBean(Parcel in) {
            this.folderName = in.readString();
            this.type = in.readInt();
        }

        public static final Creator<FileListBean> CREATOR = new Creator<FileListBean>() {
            @Override
            public FileListBean createFromParcel(Parcel source) {
                return new FileListBean(source);
            }

            @Override
            public FileListBean[] newArray(int size) {
                return new FileListBean[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.packName);
        dest.writeString(this.appName);
        dest.writeTypedList(this.fileList);
    }

    public PathData() {
    }

    protected PathData(Parcel in) {
        this.packName = in.readString();
        this.appName = in.readString();
        this.fileList = in.createTypedArrayList(FileListBean.CREATOR);
    }

    public static final Creator<PathData> CREATOR = new Creator<PathData>() {
        @Override
        public PathData createFromParcel(Parcel source) {
            return new PathData(source);
        }

        @Override
        public PathData[] newArray(int size) {
            return new PathData[size];
        }
    };
}
