package com.xiaoniu.cleanking.ui.newclean.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.LayoutAnimationController
import com.xiaoniu.cleanking.R
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent
import com.xiaoniu.cleanking.base.BaseActivity
import com.xiaoniu.cleanking.base.ScanDataHolder
import com.xiaoniu.cleanking.bean.JunkResultWrapper
import com.xiaoniu.cleanking.ui.main.bean.JunkGroup
import com.xiaoniu.cleanking.ui.newclean.adapter.SpeedUpResultAdapter
import com.xiaoniu.cleanking.ui.newclean.bean.ScanningResultType
import com.xiaoniu.cleanking.ui.newclean.interfice.ClickListener
import com.xiaoniu.cleanking.ui.newclean.presenter.SpeedUpResultPresenter
import com.xiaoniu.cleanking.ui.newclean.util.AlertDialogUtil
import com.xiaoniu.cleanking.utils.AndroidUtil
import com.xiaoniu.cleanking.utils.LayoutAnimationHelper
import com.xiaoniu.cleanking.utils.NumberUtils
import com.xiaoniu.cleanking.utils.OnItemClickListener
import com.xiaoniu.cleanking.utils.update.PreferenceUtil
import com.xiaoniu.cleanking.widget.CustomLinearLayoutManger
import com.xiaoniu.common.utils.StatisticsUtils
import com.xiaoniu.common.utils.StatusBarUtil
import com.xiaoniu.common.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_speedup_result.*
import java.util.*
import kotlin.collections.LinkedHashMap

class SpeedUpResultActivity : BaseActivity<SpeedUpResultPresenter>(), OnItemClickListener<JunkResultWrapper> {
    override fun inject(activityComponent: ActivityComponent) {
        activityComponent.inject(this)
    }

    private var mAppSize = 0

    companion object {
        const val SPEEDUP_APP_SIZE: String = "speedup_app_size"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.setTransparentForWindow(this)
    }

    private var checkedResultSize = ""
    override fun getLayoutId() = R.layout.activity_speedup_result


    private val mScanResultAdapter: SpeedUpResultAdapter by lazy {
        SpeedUpResultAdapter(this)
    }

    private val randomValue = NumberUtils.mathRandom(10, 30)

    override fun initView() {
        StatisticsUtils.onPageStart("boost_scan_result_page_view_page", "用户在加速诊断页浏览")
        mAppSize = intent.getIntExtra(SPEEDUP_APP_SIZE, 0)
        rv_content_list.layoutManager = CustomLinearLayoutManger(this)
        rv_content_list.adapter = mScanResultAdapter
        //计算用户选中需要清理的垃圾文件，并且跳转清理界面
        tv_clean_junk.setOnClickListener {

            if (mScanResultAdapter.allDataList.none { it.firstJunkInfo.isAllchecked }) {
                ToastUtils.showShort("至少选择一个APP进行加速")
            } else {
                //保存本次清理完成时间 保证每次清理时间间隔为3分钟

                StatisticsUtils.trackClick("accelerate_clean_button_click", "用户在加速诊断页点击【一键加速】按钮",
                        "boost_scan_result_page", "boost_scan_result_page")

                if (PreferenceUtil.getCleanTime()) {
                    PreferenceUtil.saveCleanTime()
                }
                ScanDataHolder.getInstance().junkResultWrapperList = mScanResultAdapter.allDataList.filter { it.firstJunkInfo.isAllchecked }
                val intent = Intent(this@SpeedUpResultActivity, SpeedUpClearActivity::class.java)
                intent.putExtra(SpeedUpClearActivity.SPEED_UP_NUM, randomValue)
                startActivity(intent)
                finish()
            }
        }

        tv_back.setOnClickListener {
            backClick(false)
        }
        speed_up_value.text = "强力加速彻底清理后速度可提升$randomValue%"
        initData()
    }


    private fun backClick(isSystemBack: Boolean) {
        /* if (isBackClick) {
             return
         }*/
        AlertDialogUtil.alertBanLiveDialog(this, "确认要退出吗？", "常驻软件过多会造成手机卡顿！", "一键加速", "确认退出", object : ClickListener {
            override fun clickOKBtn() {
                tv_clean_junk.performClick()
            }

            override fun cancelBtn() {
                if (isSystemBack) {
                    StatisticsUtils.trackClick("system_return_back_click", "用户在加速诊断页返回",
                            "boost_scan_result_page", "boost_scan_result_page")
                } else {
                    StatisticsUtils.trackClick("return_click", "用户在加速诊断页返回",
                            "boost_scan_result_page", "boost_scan_result_page")
                }
                finish()
            }
        }, Color.parseColor("#06C581"), Color.parseColor("#727375"))
    }

    override fun onBackPressed() {
        backClick(true)
    }

    fun initData() {
        //只扫描内存垃圾
        //构造清理数据模型
        val groupLinkedHashMap = ScanDataHolder.getInstance().getmJunkGroups().filterKeys { it.type == 5 }
        if (groupLinkedHashMap.isEmpty()) {
            //伪造数据
            val un: Long = 80886656
            val counterfeit = LinkedHashMap<ScanningResultType, JunkGroup>(mAppSize)
            val appList = AndroidUtil.getRandomMaxCountInstallApp(this, mAppSize)
            appList.forEach {
                it.totalSize = (Math.random() * un).toLong() + un
                it.isAllchecked = true
            }
            val junkGroup = JunkGroup(ScanningResultType.MEMORY_JUNK.title, ScanningResultType.MEMORY_JUNK.type)
            junkGroup.mChildren = appList
            counterfeit[ScanningResultType.MEMORY_JUNK] = junkGroup
            mPresenter.buildJunkResultModel(counterfeit, mAppSize)
        } else {
            groupLinkedHashMap[ScanningResultType.MEMORY_JUNK]?.mChildren?.forEach {
                it.isAllchecked = true
            }
            mPresenter.buildJunkResultModel(groupLinkedHashMap as LinkedHashMap<ScanningResultType, JunkGroup>, mAppSize)
        }


    }

    override fun netError() {
    }

    fun setCheckedJunkResult(resultSize: String) {
        checkedResultSize = resultSize
        //   tv_checked_total.text = getString(R.string.scan_result_check_total, resultSize)
        // tv_clean_junk.text = getString(R.string.clean_btn, resultSize)
    }

    fun setInitSubmitResult(junkResultWrappers: List<JunkResultWrapper>) {
        tv_junk_total.text = junkResultWrappers.size.toString()
        //首次填充数据
        mScanResultAdapter.submitList(junkResultWrappers)
        //只在首次进入的时候添加一个从右边进入的动画
        showInitDataAnimator()
    }


    fun setJunkTotalResultSize(totalSize: String, unit: String, number: Long) {
        val extParam = HashMap<String, Any>()
        extParam["garbage_file_size"] = number
        //  tv_junk_total.text = totalSize
        // tv_junk_unit.text = unit
    }

    /***
     * 展示recyclerView进入动画(非Insert动画)
     */
    private fun showInitDataAnimator() {
        val controller = LayoutAnimationController(LayoutAnimationHelper.getAnimationSetFromRight())
        controller.delay = 0.1f
        controller.order = LayoutAnimationController.ORDER_NORMAL
        rv_content_list.layoutAnimation = controller
        mScanResultAdapter.notifyDataSetChanged()
        rv_content_list.scheduleLayoutAnimation()
    }

    override fun onItemClick(view: View?, data: JunkResultWrapper, position: Int) {
        when (view?.id) {
            R.id.iv_check_state -> mPresenter.updateJunkContentCheckState(data)
        }
    }

    fun setSubmitResult(buildJunkDataModel: List<JunkResultWrapper>) {
        tv_junk_total.text = buildJunkDataModel.filter { it.firstJunkInfo.isAllchecked }.size.toString()
        //非首次填充，根据数据变更动态填充数据
        mScanResultAdapter.submitList(buildJunkDataModel)
    }

    override fun onDestroy() {
        super.onDestroy()
        StatisticsUtils.onPageEnd("boost_scan_result_page_view_page", "用户在加速诊断页浏览", "boost_scan_result_page", "boost_scan_result_page")
    }
}