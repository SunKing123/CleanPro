package com.xiaoniu.cleanking.ui.newclean.util

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.xiaoniu.cleanking.ui.finish.NewCleanFinishPlusActivity
import com.xiaoniu.cleanking.ui.newclean.activity.NewCleanFinishActivity

/**
 * @author XiLei
 * @date 2019/11/22.
 * description：跳完成页面
 */
class StartFinishActivityUtil {

    companion object {
        fun gotoFinish(context: Context, intent: Intent) {
            intent.setClass(context, NewCleanFinishPlusActivity::class.java);
            context.startActivity(intent)
        }
        fun gotoFinish(context: Context, bundle:Bundle) {
            var intent=Intent()
            intent.setClass(context, NewCleanFinishPlusActivity::class.java);
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }
}
