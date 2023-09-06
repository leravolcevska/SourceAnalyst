package com.source.analyst

import android.app.Activity
import android.content.Intent
import com.nttsolmare.game.android.privacyshower.core.common.Const

object Transfer {
    private var mainActivity: Class<out Activity>? = null
    fun openWeb(
        activity: Activity,
        link: String,
        isCache: Boolean,
        exceptionTitle: String,
        secondActivity: Class<out Activity>
    ) {
        val intent = Intent(activity, secondActivity)

        intent.putExtra(Const.LINK_KEY, link)
        intent.putExtra(Const.CACHE_STATUS, isCache)
        intent.putExtra(Const.TITLE_KEY, exceptionTitle)

        activity.startActivity(intent)
        activity.finish()
    }

    fun openMain(
        activity: Activity,
    ) {
        if(mainActivity == null) return
        val intent = Intent(activity, mainActivity)

        activity.startActivity(intent)
        activity.finish()
    }

    fun setMainActivity(mainActivity: Class<out Activity>){
        this.mainActivity = mainActivity
    }
}