package com.source.analyst

import android.content.Context
import android.util.Log
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.facebook.FacebookSdk
import com.facebook.applinks.AppLinkData
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URLDecoder
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object RemoteHandler {
    data class RemoteData(
        val campaign: String?,
        val isFromFacebook: Boolean,
    )

    suspend fun get(
        context: Context,
        fbId: String,
        fbToken: String,
        fbDescriptionKey: String
    ): RemoteData {
        suspend fun referrerInit(context: Context, onReceive: (String?) -> Unit) {
            withContext(Dispatchers.IO) {
                try {
                    val referrerClient = InstallReferrerClient.newBuilder(context).build()

                    val installReferrerStateListener = object : InstallReferrerStateListener {
                        override fun onInstallReferrerSetupFinished(responseCode: Int) {
                            if (responseCode == InstallReferrerClient.InstallReferrerResponse.OK) {
                                val installReferrer = referrerClient.installReferrer.installReferrer

                                onReceive(installReferrer)
                            } else onReceive(null)

                            referrerClient.endConnection()
                        }

                        override fun onInstallReferrerServiceDisconnected() {
                            onReceive(null)
                        }
                    }

                    referrerClient.startConnection(installReferrerStateListener)
                } catch (e: Exception) {
                    if (e is CancellationException) throw CancellationException()

                    onReceive(null)
                }
            }
        }

        suspend fun getReferrer(
            context: Context,
            fbDescriptionKey: String,
            onReceive: (String?) -> Unit
        ) {
            withContext(Dispatchers.IO) {
                referrerInit(context) { referrerData ->
                    try {
                        if (referrerData.isNullOrBlank()) {
                            onReceive(null)
                            return@referrerInit
                        }

                        fun String.decodeHex(): ByteArray {
                            check(length % 2 == 0) { "" }
                            return chunked(2).map { it.toInt(16).toByte() }.toByteArray()
                        }

                        val decodeUrl = URLDecoder.decode(
                            referrerData.split("${Filter.deFilter("gfy")}_${Filter.deFilter("oazfqzf")}=")
                                .getOrNull(1) ?: "null",
                            "utf-8"
                        )

                        val source =
                            JSONObject(JSONObject(decodeUrl)[Filter.deFilter("eagdoq")].toString())

                        val message = source[Filter.deFilter("pmfm")].toString().decodeHex()

                        val specKey =
                            SecretKeySpec(fbDescriptionKey.decodeHex(), "AES/GCM/NoPadding")

                        val nonceSpec =
                            IvParameterSpec(source[Filter.deFilter("zazoq")].toString().decodeHex())

                        val chipper = Cipher.getInstance("AES/GCM/NoPadding")

                        chipper.init(Cipher.DECRYPT_MODE, specKey, nonceSpec)

                        val resultString = JSONObject(String(chipper.doFinal(message)))

                        resultString.getString(
                            "${Filter.deFilter("omybmusz")}_${Filter.deFilter("sdagb")}_${
                                Filter.deFilter(
                                    "zmyq"
                                )
                            }"
                        ).toString().also { campaign ->
                            onReceive(campaign)
                        }
                    } catch (e: Exception) {
                        if (e is CancellationException) throw CancellationException()

                        onReceive(null)
                    }
                }
            }
        }

        val scope = CoroutineScope(coroutineContext)

        return suspendCoroutine<RemoteData> { resultContinuation ->
            scope.launch(Dispatchers.IO) {
                val facebookCampaignSharedFlow = MutableSharedFlow<String?>()
                val referrerCampaignSharedFlow = MutableSharedFlow<String?>()

                launch(Dispatchers.Main.immediate) {
                    combine(
                        facebookCampaignSharedFlow,
                        referrerCampaignSharedFlow
                    ) { fbCampaign, referrerCampaign ->
                        val isFromFacebook = fbCampaign != null

                        Log.e("", "combine")

                        resultContinuation.resume(
                            RemoteData(
                                campaign = fbCampaign ?: referrerCampaign, isFromFacebook
                            )
                        )
                    }.collectLatest { }
                }

                launch(SupervisorJob() + Dispatchers.IO) {
                    Log.e("", "trying to get fb")
                    val facebookCampaign = suspendCoroutine { continuation ->
                        Log.e("", "fb inti..")

                        FacebookSdk.apply {
                            setApplicationId(fbId)
                            setClientToken(fbToken)
                            sdkInitialize(context)
                            setAdvertiserIDCollectionEnabled(true)
                            setAutoInitEnabled(true)
                            fullyInitialize()
                        }

                        AppLinkData.fetchDeferredAppLinkData(context) {
                            Log.e("", "fb response = $it")
                            continuation.resume(it?.targetUri?.toString())
                        }
                    }

                    Log.e("", "fbCampaign = $facebookCampaign")

                    facebookCampaignSharedFlow.emit(facebookCampaign)
                }

                launch {
                    val referrerCampaign = suspendCoroutine { continuation ->
                        scope.launch {
                            getReferrer(context, fbDescriptionKey) { campaign ->
                                continuation.resume(campaign)

                            }
                        }
                    }

                    Log.e("", "referrer = $referrerCampaign")

                    referrerCampaignSharedFlow.emit(referrerCampaign)
                }

            }
        }
    }
}
