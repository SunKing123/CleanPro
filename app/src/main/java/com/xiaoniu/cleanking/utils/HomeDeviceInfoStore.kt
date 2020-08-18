package com.xiaoniu.cleanking.utils

/**
 * Created by xinxiaolong on 2020/8/18.
 * email：xinxiaolong123@foxmail.com
 */
class HomeDeviceInfoStore {

    companion object {
        @Volatile
        private var instance: HomeDeviceInfoStore? = null
        fun getInstance() =
                instance ?: synchronized(this) {
                    instance
                            ?: HomeDeviceInfoStore().also { instance = it }
                }
    }

    private constructor() {

    }



}
