//package com.nttsolmare.game.android.privacyshower.splash
//
//import android.app.Activity
//import android.content.Intent
//import androidx.activity.ComponentActivity
//import com.nttsolmare.game.android.privacyshower.core.GameClassData
//import com.nttsolmare.game.android.fithlib.core.util.setupNotificationHandler
//import com.nttsolmare.game.android.privacyshower.splash.domain.model.AppsData
//import com.nttsolmare.game.android.privacyshower.splash.domain.model.InputData
//import com.nttsolmare.game.android.privacyshower.splash.domain.model.ReferrerData
//import com.nttsolmare.game.android.privacyshower.splash.domain.model.ValuesData
//import com.nttsolmare.game.android.privacyshower.splash.domain.operation.SplashOperator
//import com.nttsolmare.game.android.fithlib.core.util.Collector
//import com.nttsolmare.game.android.fithlib.core.util.crypto_manager.CryptoManager
//import com.nttsolmare.game.android.privacyshower.core.util.crypto_manager.CryptoManagerImpl
//import com.nttsolmare.game.android.fithlib.core.util.web.WebActivity
//import com.nttsolmare.game.android.privacyshower.web.ui.WebActivityImpl
//import kotlinx.coroutines.*
//
//class Fither(
//    private val inputData: InputData
//) {
//    private val crypto: CryptoManager = CryptoManagerImpl()
//    suspend fun startWork(
//        activity: ComponentActivity,
//        gameActivityClass: Class<out Activity>,
//        tracker: String,
//        title: String,
//        onAppsFlyer: Boolean,
//    ) {
//        val webActivity: Class<out WebActivity> = WebActivityImpl::class.java
//        val splashOperator = SplashOperator(webActivity = webActivity, activity)
//
//        setupNotificationHandler(activity, title)
//
//        delay(1500)
//
//        GameClassData.gameClass = gameActivityClass
//
//        CoroutineScope(Dispatchers.IO).launch {
//            splashOperator.oneSignalInit(inputData.OneSignalId)
//        }
//        val savedLink = withContext(Dispatchers.IO) {
//            splashOperator.getLink()
//        }
//        if (splashOperator.validLink(savedLink) && savedLink != null) {
//            splashOperator.openWeb(activity, savedLink, true, title)
//        } else {
//            if (tracker == "" || tracker == "null") {
//                val gameIntent = Intent(activity, gameActivityClass)
//                activity.startActivity(gameIntent)
//                activity.finishAndRemoveTask()
//            } else {
//                @Suppress("DeferredResultUnused")
//                withContext(Dispatchers.IO) {
//                    val collector = Collector(setOf("AD", "RD", "DL", "SVD")) { resultMap ->
//                        val valuesData = resultMap.getOrDefault(
//                            "SVD", null
//                        ) as ValuesData?
//
//                        val toolsData = splashOperator.parseValues(
//                            appsData = resultMap.getOrDefault(
//                                "AD", null
//                            ) as AppsData?,
//                            referrerData = resultMap.getOrDefault(
//                                "RD", null
//                            ) as ReferrerData?,
//                            deepLink = resultMap.getOrDefault(
//                                "DL", null
//                            ) as String?
//                        )
//
//                        val link = splashOperator.generateLink(
//                            tracker = tracker,
//                            inputData = inputData,
//                            parseToolsData = toolsData,
//                            valuesData = valuesData
//                        )
//
//                        splashOperator.openWeb(activity, link, false, title)
//
//                        CoroutineScope(Dispatchers.IO).launch {
//                            splashOperator.sendOneSignal(
//                                push = toolsData.sL?.getOrNull(1),
//                                appsId = valuesData?.appsFlyerId,
//                                crypto = crypto
//                            )
//                        }
//
//                    }
//
//                    async {
//                        if (onAppsFlyer) {
//                            splashOperator.appsFlyerInit(
//                                activity,
//                                inputData.AppsFlayerKey
//                            ) { appsData ->
//                                collector.sendData("AD", appsData)
//                            }
//                        } else {
//                            collector.sendData("AD", null)
//                        }
//
//                        splashOperator.initReferrer(activity) { referrer ->
//                            val referrerData =
//                                splashOperator.getReferrerData(
//                                    referrer,
//                                    inputData.FBDecryptionKey,
//                                    crypto = crypto
//                                )
//
//                            collector.sendData("RD", referrerData)
//                        }
//
//                        splashOperator.getDeepLink(
//                            fbId = inputData.FBID,
//                            fbToken = inputData.FBToken
//                        ) { deepLink ->
//                            collector.sendData("DL", deepLink)
//                        }
//
//                        splashOperator.getSystemValues { systemValuesData ->
//                            collector.sendData("SVD", systemValuesData)
//                        }
//                    }
//                }
//            }
//
//        }
//    }
//
//}