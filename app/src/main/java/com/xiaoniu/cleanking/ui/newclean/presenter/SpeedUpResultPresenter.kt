package com.xiaoniu.cleanking.ui.newclean.presenter

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import com.xiaoniu.cleanking.base.RxPresenter
import com.xiaoniu.cleanking.bean.JunkResultWrapper
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo
import com.xiaoniu.cleanking.ui.main.bean.JunkGroup
import com.xiaoniu.cleanking.ui.main.model.MainModel
import com.xiaoniu.cleanking.ui.newclean.activity.SpeedUpResultActivity
import com.xiaoniu.cleanking.ui.newclean.bean.ScanningResultType
import com.xiaoniu.cleanking.utils.AndroidUtil
import com.xiaoniu.cleanking.utils.CleanUtil
import com.xiaoniu.cleanking.utils.CollectionUtils
import com.xiaoniu.cleanking.utils.FileUtils
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class SpeedUpResultPresenter @Inject constructor(activity: RxAppCompatActivity) : RxPresenter<SpeedUpResultActivity, MainModel>() {
    var mActivity: RxAppCompatActivity = activity

    /**
     * 垃圾清理类别
     */
    private val junkTitleMap = LinkedHashMap<ScanningResultType, JunkGroup>()

    /**
     * 垃圾清理内容
     */
    private val junkContentMap = LinkedHashMap<ScanningResultType, ArrayList<FirstJunkInfo>>()


    fun buildJunkResultModel(scanningResultMap: LinkedHashMap<ScanningResultType, JunkGroup>, appSize: Int) {
        //拆分清理数据类别
        splitJunkGroup(scanningResultMap, appSize)
        //构建清理数据展示模型
        initBuildJunkDataModel()
        //展示垃圾总量
        calJunkTotalSize()
    }


    /**
     * 拆分出来清理类别 & 清理内容，方便数据展示
     */
    private fun splitJunkGroup(scanningResultMap: LinkedHashMap<ScanningResultType, JunkGroup>?, appSize: Int) {
        if (scanningResultMap != null && scanningResultMap.size > 0) {
            junkTitleMap.clear()
            junkContentMap.clear()
            //拆分清理内容
            for ((key, value) in scanningResultMap) {
                junkTitleMap[key] = value
                //过滤掉系统应用
                val data = value.mChildren.filter {
                    !FileUtils.isSystemApK(it.appPackageName)
                }
                //val max = if (data.size >= appSize) appSize else data.size
                val newData = ArrayList<FirstJunkInfo>()
                //当第三方应用不满足个数的时候，取系统应用
                if (data.size >= appSize) {
                    for (index in 0 until appSize) {
                        newData.add(data[index])
                    }
                } else {
                    newData.addAll(data)
                    newData.addAll(AndroidUtil.getSystemInstallApps(mActivity, appSize - newData.size))
                    newData.forEach {
                        it.isAllchecked = true
                    }
                }
                junkContentMap[key] = newData
                //junkContentMap[key] = data.slice(IntRange(0, max-1)) as ArrayList<FirstJunkInfo>
            }
        }
    }


    /**
     * 构造清理数据模型
     */
    private fun initBuildJunkDataModel() {
        mView?.let {
            val firstJunkList: ArrayList<FirstJunkInfo>? = junkContentMap[ScanningResultType.APK_JUNK]
            if (!CollectionUtils.isEmpty(firstJunkList)) {
                var checkedCount = 0
                for (firstJunkInfo in firstJunkList!!) {
                    if (firstJunkInfo.isAllchecked) {
                        checkedCount++
                    }
                }
                val junkGroup = junkTitleMap[ScanningResultType.APK_JUNK]
                if (junkGroup != null) {
                    junkGroup.isCheckPart = checkedCount != 0 && checkedCount != firstJunkList.size
                    if (checkedCount == 0) {
                        junkGroup.isChecked = false
                    } else {
                        junkGroup.isChecked = checkedCount == firstJunkList.size
                    }
                    junkTitleMap[ScanningResultType.APK_JUNK] = junkGroup
                }
            }
            it.setInitSubmitResult(buildJunkDataModel())
        }


    }


    private fun buildJunkDataModel(): List<JunkResultWrapper> {
        val junkResultWrappers: MutableList<JunkResultWrapper> = ArrayList()
        for ((key, value) in junkTitleMap) {
            val firstJunkInfoList = junkContentMap[key]
            if (firstJunkInfoList != null && firstJunkInfoList.size > 0) {
                //添加类别数据
                //  junkResultWrappers.add(JunkResultWrapper(JunkResultWrapper.ITEM_TYPE_TITLE, key, value))
                if (value.isExpand) {
                    //遍历添加内容数据
                    for (firstJunkInfo in firstJunkInfoList) {
                        junkResultWrappers.add(JunkResultWrapper(JunkResultWrapper.ITEM_TYPE_CONTENT, key, firstJunkInfo))
                    }
                }
            }
        }

        //设置选中状态垃圾总量
        mView?.let {
            val checkedJunkResult: Long = setCheckedJunkResult()
            val countEntity = CleanUtil.formatShortFileSize(checkedJunkResult)
            it.setCheckedJunkResult(countEntity.resultSize)
        }
        return junkResultWrappers
    }


    /**
     * 计算扫描到的垃圾总量
     */
    private fun calJunkTotalSize() {
        var totalSize: Long = 0
        for ((_, value) in junkTitleMap) {
            totalSize += value.mSize
        }
        val mCountEntity = CleanUtil.formatShortFileSize(totalSize)
        //展示扫描到的垃圾总量
        mView?.setJunkTotalResultSize(mCountEntity.totalSize, mCountEntity.unit, mCountEntity.number)
    }

    /**
     * 获取选中的垃圾清理量
     */
    private fun setCheckedJunkResult(): Long {
        var checkedTotalSize: Long = 0
        for ((_, value) in junkContentMap) {
            if (!CollectionUtils.isEmpty(value)) {
                for (firstJunkInfo in value) {
                    if (firstJunkInfo.isAllchecked) {
                        checkedTotalSize += firstJunkInfo.totalSize
                    } else if (firstJunkInfo.isIsthreeLevel && firstJunkInfo.isCarefulIsChecked) {
                        checkedTotalSize += firstJunkInfo.careFulSize
                    } else if (firstJunkInfo.isIsthreeLevel && firstJunkInfo.isUncarefulIsChecked) {
                        checkedTotalSize += firstJunkInfo.uncarefulSize
                    }
                }
            }
        }
        return checkedTotalSize
    }


    fun updateJunkContentCheckState(wrapper: JunkResultWrapper) {
        val firstJunkList: ArrayList<FirstJunkInfo>? = junkContentMap[wrapper.scanningResultType]
        if (!CollectionUtils.isEmpty(firstJunkList)) {
            var checkedCount = 0
            for (firstJunkInfo in firstJunkList!!) {
                if (firstJunkInfo == wrapper.firstJunkInfo) {
                    if (firstJunkInfo.isIsthreeLevel) {
                        if (firstJunkInfo.careFulSize > 0 && firstJunkInfo.uncarefulSize > 0) {
                            if (firstJunkInfo.isUncarefulIsChecked && firstJunkInfo.isCarefulIsChecked) {
                                firstJunkInfo.isCarefulIsChecked = false
                                firstJunkInfo.isUncarefulIsChecked = false
                                firstJunkInfo.isAllchecked = false
                            } else if (firstJunkInfo.isUncarefulIsChecked || firstJunkInfo.isCarefulIsChecked) {
                                firstJunkInfo.isCarefulIsChecked = true
                                firstJunkInfo.isUncarefulIsChecked = true
                                firstJunkInfo.isAllchecked = true
                            }
                        } else if (firstJunkInfo.careFulSize > 0 && firstJunkInfo.uncarefulSize == 0L) {
                            firstJunkInfo.isCarefulIsChecked = !firstJunkInfo.isCarefulIsChecked
                            firstJunkInfo.isAllchecked = !firstJunkInfo.isAllchecked
                        } else if (firstJunkInfo.careFulSize == 0L && firstJunkInfo.uncarefulSize > 0) {
                            firstJunkInfo.isUncarefulIsChecked = !firstJunkInfo.isUncarefulIsChecked
                            firstJunkInfo.isAllchecked = !firstJunkInfo.isAllchecked
                        }
                    } else {
                        firstJunkInfo.isAllchecked = !firstJunkInfo.isAllchecked
                    }
                }
                if (firstJunkInfo!!.isAllchecked) {
                    checkedCount++
                }
            }
            junkContentMap[wrapper.scanningResultType] = firstJunkList
            val junkGroup = junkTitleMap[wrapper.scanningResultType]
            if (junkGroup != null) {
                junkGroup.isCheckPart = checkedCount != 0 && checkedCount != firstJunkList.size
                if (checkedCount == 0) {
                    junkGroup.isChecked = false
                } else {
                    junkGroup.isChecked = checkedCount == firstJunkList.size
                }
                junkTitleMap[wrapper.scanningResultType] = junkGroup
            }
            refreshResultModel()
        }
    }


    /**
     * 刷新界面展示
     */
    private fun refreshResultModel() {
        mView.setSubmitResult(buildJunkDataModel())
    }
}