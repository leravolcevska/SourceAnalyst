package com.source.analyst

import android.content.Context
import com.onesignal.OneSignal

object OneSignalHandler {
    fun initializeOneSignal(context: Context, oneSignalId: String) {
        OneSignal.initWithContext(context)
        OneSignal.setAppId(oneSignalId)
        OneSignal.setLogLevel(
            OneSignal.LOG_LEVEL.VERBOSE,
            OneSignal.LOG_LEVEL.NONE
        )
    }

    fun sendOneSignalTag(googleAdId: String?, sendText: String, tag: String) {
        if (googleAdId == null) return

        OneSignal.setExternalUserId(googleAdId)
        OneSignal.sendTag(tag, sendText)
    }
}