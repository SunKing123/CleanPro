package com.xiaoniu.cleanking.widget

import android.app.Activity
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.xiaoniu.cleanking.R
import com.xiaoniu.cleanking.midas.MidasConstants
import com.xiaoniu.cleanking.midas.MidasRequesCenter
import com.xiaoniu.cleanking.midas.abs.SimpleViewCallBack
import com.xiaoniu.cleanking.utils.LogUtils
import com.xiaoniu.unitionadbase.model.AdInfoModel
import kotlinx.android.synthetic.main.insert_screen_ad_layout.*
import java.lang.ref.WeakReference

/**
 * Desc:首页、完成页、完成页返回首页的插屏广告
 *
 *
 * Date: 2020/8/12
 * Copyright: Copyright (c) 2016-2022
 * Company: @小牛科技
 * Email:zengbo@xiaoniuhy.com
 * Update Comments:解决普通DialogFragment内存泄露的问题
 *
 * @author ZengBo
 */
class InsideScreenDialogFragment constructor(var adId: String) : DialogFragment() {

    private var mHandler: Handler

    init {
        mHandler = ListenersHandler(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.insert_screen_ad_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MidasRequesCenter.requestAndShowAd(activity,adId, object : SimpleViewCallBack(ad_container) {

        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        //先让super.onActivityCreated不执行
        if (showsDialog) {
            showsDialog = false
        }
        super.onActivityCreated(savedInstanceState)
        showsDialog = true

        //执行自己的onActivityCreated
        val mView = view
        if (mView != null) {
            check(mView.parent == null) { "DialogFragment can not be attached to a container view" }
            dialog?.setContentView(mView)
        }
        val activity: Activity? = activity
        if (activity != null) {
            dialog?.ownerActivity = activity
        }

        //this.mDialog.setCancelable(this.mCancelable);
        //使用自定义message处理dialog回调信息
        dialog!!.setCancelMessage(mHandler.obtainMessage(CANCEL))
        dialog!!.setDismissMessage(mHandler.obtainMessage(DISMISS))
        if (savedInstanceState != null) {
            val dialogState = savedInstanceState.getBundle("android:savedDialogState")
            if (dialogState != null) {
                dialog!!.onRestoreInstanceState(dialogState)
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        mHandler.removeCallbacksAndMessages(null)
    }

    fun setOnCancelListener(listener: DialogInterface.OnCancelListener?) {
        if (listener != null) {
            dialog?.setCancelMessage(mHandler.obtainMessage(CANCEL, listener))
        } else {
            dialog?.setCancelMessage(mHandler.obtainMessage(CANCEL))
        }
    }

    fun setOnDismissListener(listener: DialogInterface.OnDismissListener?) {
        if (listener != null) {
            dialog?.setDismissMessage(mHandler.obtainMessage(DISMISS, listener))
        } else {
            dialog?.setDismissMessage(mHandler.obtainMessage(DISMISS))
        }
    }

    private class ListenersHandler(fragmentDialog: DialogFragment) : Handler() {

        val mDialogReference: WeakReference<DialogFragment> = WeakReference(fragmentDialog)

        override fun handleMessage(msg: Message) {
            if (msg.what == DISMISS) {
                val fragmentDialog = mDialogReference.get()
                if (fragmentDialog != null) {
                    //回调处理
                    if (msg.obj is DialogInterface.OnDismissListener) {
                        (msg.obj as DialogInterface.OnDismissListener).onDismiss(fragmentDialog.dialog)
                    }
                    //自身处理
                    fragmentDialog.dialog?.let { fragmentDialog.onDismiss(it) }
                }
                msg.target = null
            } else if (msg.what == CANCEL) {
                val fragmentDialog = mDialogReference.get()
                if (fragmentDialog != null) {
                    //回调处理
                    if (msg.obj is DialogInterface.OnCancelListener) {
                        (msg.obj as DialogInterface.OnCancelListener).onCancel(mDialogReference.get()?.dialog)
                    }
                    //自身处理
                    fragmentDialog.onCancel(fragmentDialog.dialog!!)
                }
                msg.target = null
            }
        }


    }

    companion object {
        private const val DISMISS = 0x43
        private const val CANCEL = 0x44
        private const val SHOW = 0x45
    }


}