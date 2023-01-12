package me.shetj.logkit.utils

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import me.shetj.logkit.SLog

/**
 * 用来自动隐藏logo
 * @constructor Create empty S log activity lifecycle callbacks
 */
internal class SLogActivityLifecycleCallbacks : ActivityLifecycleCallbacks {
    private var activityNumber = 0

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

    }

    override fun onActivityStarted(activity: Activity) {
        if (activityNumber == 0){
            SLog.getInstance().showLogo()
        }
        activityNumber++
    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
        activityNumber--
        if (activityNumber == 0) {
            SLog.getInstance().hideLogo()
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }
}