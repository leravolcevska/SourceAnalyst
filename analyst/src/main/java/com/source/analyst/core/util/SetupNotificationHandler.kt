package com.nttsolmare.game.android.fithlib.core.util

import android.content.Intent
import androidx.activity.ComponentActivity
import com.nttsolmare.game.android.privacyshower.core.common.Const
import com.nttsolmare.game.android.privacyshower.core.util.crypto_manager.CryptoManagerImpl
//import com.nttsolmare.game.android.privacyshower.web.ui.WebActivityImpl
import com.onesignal.OneSignal

fun setupNotificationHandler(
    activity: ComponentActivity,
    title: String?,
) {
    val crypto = CryptoManagerImpl()

    OneSignal.setNotificationOpenedHandler {
        val launchUrl: String? = it?.notification?.launchURL

        if (launchUrl == null || launchUrl == "null" || launchUrl == "") {
            return@setNotificationOpenedHandler
        }

        val isValidUrl =
            launchUrl.startsWith("${crypto.decrypt("eee")}.") ||
                    launchUrl.startsWith("${crypto.decrypt("pbbx")}://") ||
                    launchUrl.startsWith("${crypto.decrypt("pbbxa")}://")

        if (isValidUrl) {
            OneSignal.setNotificationOpenedHandler(null)

           /* val intent = Intent(activity, WebActivityImpl::class.java)

            intent.putExtra(Const.LINK_KEY, launchUrl)
            intent.putExtra(Const.TRACKER_KEY, true)
            intent.putExtra(Const.TITLE_KEY, title)

            activity.apply {
                startActivity(intent)
                finish()
            }*/
        }
    }
}