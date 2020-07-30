package com.xiaoniu.cleanking.ui.viruskill.fragment

import android.os.Bundle
import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.DeviceUtils
import com.xiaoniu.cleanking.R
import com.xiaoniu.cleanking.ui.viruskill.ITransferPagePerformer
import com.xiaoniu.cleanking.ui.viruskill.model.ScanTextItemModel
import com.xiaoniu.common.utils.Points
import com.xiaoniu.common.utils.StatisticsUtils
import kotlinx.android.synthetic.main.fragment_viruskill_clean_layout.*
import android.os.CountDownTimer as AndroidOsCountDownTimer

/**
 * Created by xinxiaolong on 2020/7/23.
 * email：xinxiaolong123@foxmail.com
 */
class VirusCleanFragment : com.jess.arms.base.SimpleFragment() {

    lateinit var transfer: ITransferPagePerformer
    lateinit var pList: ArrayList<ScanTextItemModel>
    lateinit var nList: ArrayList<ScanTextItemModel>
    var aList = ArrayList<ScanTextItemModel>()
    lateinit var timer: AndroidOsCountDownTimer

    var gridLength = 0;
    var gridIndex = -1;

    object IntentKey {
        const val P_LIST = "P_LIST"
        const val N_LIST = "N_LIST"
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_viruskill_clean_layout;
    }

    override fun setupFragmentComponent(appComponent: AppComponent) {

    }

    override fun setData(data: Any?) {

    }

    override fun initData(savedInstanceState: Bundle?) {
        pList = arguments!!.getParcelableArrayList(VirusScanResultFragment.IntentKey.P_LIST)
        nList = arguments!!.getParcelableArrayList(VirusScanResultFragment.IntentKey.N_LIST)
        aList.addAll(pList)
        aList.addAll(nList)
        gridLength = 100 / aList.size

        initView()
    }


    fun initView() {
        lottie.startRotationAnimation()
        StatisticsUtils.onPageStart(Points.Virus.CLEAN_FINISH_PAGE_EVENT_CODE, Points.Virus.CLEAN_FINISH_PAGE_EVENT_NAME)
        timer = object : AndroidOsCountDownTimer(5000, 50) {
            override fun onTick(millisUntilFinished: Long) {
                if (null != tv_clean_item && null != txtPro && null != progressBar) {
                    val pro = 100 - millisUntilFinished / 50
                    txtPro.text = pro.toString()
                    progressBar.progress = pro.toInt()
                    updateCleanItem(pro.toInt())
                }
            }

            override fun onFinish() {
                transfer.cleanComplete()
                StatisticsUtils.onPageEnd(Points.Virus.CLEAN_FINISH_PAGE_EVENT_CODE, Points.Virus.CLEAN_FINISH_PAGE_EVENT_NAME, "", Points.Virus.CLEAN_FINISH_PAGE)
            }
        }
        timer.start()
    }

    fun updateCleanItem(progress: Int) {
        var thisIndex = progress / gridLength
        if (thisIndex > gridIndex) {
            var model = popModel()
            if (model != null) {
                tv_clean_item.setText("优化" + model.name)
                gridIndex = thisIndex
            };
        }
    }

    fun popModel(): ScanTextItemModel? {
        if (aList.size > 0)
            return aList.removeAt(0)
        else
            return null
    }

    fun setTransferPagePerformer(transfer: ITransferPagePerformer) {
        this.transfer = transfer
    }

}
