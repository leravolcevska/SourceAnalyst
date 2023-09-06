package com.source.analyst

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ClientsHandler {
    fun getWebViewClient(activity: Activity, exceptionTitle: String?) = object : WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)

            if (
                exceptionTitle != null && url?.contains(exceptionTitle) == true
            ) {
                Transfer.openMain(activity)
            } else {
                if (url == null) return

                CoroutineScope(Dispatchers.IO).launch {
                    AnalyzeSharedPref.saveCache(activity, url)
                }
            }
        }

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            val url = request?.url?.toString() ?: return false
            try {
                if (url.startsWith(
                        "${Filter.deFilter("tffbe")}://${Filter.deFilter("f")}.${Filter.deFilter("yq")}/${
                            Filter.deFilter(
                                "vauzotmf"
                            )
                        }"
                    )
                ) {
                    Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                        activity.startActivity(this)
                    }
                }
                return when {
                    url.startsWith("${Filter.deFilter("tffb")}://") || url.startsWith(
                        "${Filter.deFilter("tffbe")}://"
                    ) -> false

                    else -> {
                        when {
                            url.startsWith(WebView.SCHEME_TEL) -> {

                                Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse(url)
                                    Intent.createChooser(this, Filter.deFilter("Omxx"))
                                        .run {
                                            activity.startActivity(this)
                                        }
                                }
                            }

                            url.startsWith("${Filter.deFilter("ymuxfa")}:") -> {

                                Intent(Intent.ACTION_SEND).apply {
                                    type = "plain/text"
                                    putExtra(
                                        Intent.EXTRA_EMAIL,
                                        url.replace("${Filter.deFilter("ymuxfa")}:", "")
                                    )
                                    Intent.createChooser(this, Filter.deFilter("Ymux"))
                                        .run {
                                            activity.startActivity(this)
                                        }
                                }
                            }
                        }

                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        activity.startActivity(intent)
                        true
                    }
                }
            } catch (e: Exception) {
                return true
            }
        }

    }

    fun getWebChromeClient(
        activity: Activity,
        requestCode: Int,
        onShowFileChooserResult: (filePath: ValueCallback<Array<Uri?>?>) -> Unit
    ) = object : WebChromeClient() {
        override fun onShowFileChooser(
            view: WebView?,
            filePath: ValueCallback<Array<Uri?>?>,
            fileChooserParams: FileChooserParams?
        ): Boolean {
            Log.e("", "onShowFileChooser")
            onShowFileChooserResult(filePath)

            val permissionList = listOf(Manifest.permission.READ_EXTERNAL_STORAGE)

            Dexter.withContext(activity)
                .withPermissions(permissionList)
                .withListener(object : MultiplePermissionsListener {
                    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
                    override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                        val pickIntent = Intent(Intent.ACTION_GET_CONTENT)
                        pickIntent.type = "image/*"

                        activity.startActivityForResult(pickIntent, requestCode)
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: MutableList<PermissionRequest>?,
                        p1: PermissionToken?
                    ) {
                        p1?.continuePermissionRequest()
                    }
                }).check()
            return true
        }
    }

    fun setFirstWebSettings(webView: WebView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            webView.importantForAutofill = WebView.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS
        }

        webView.apply {
            isSaveEnabled = true
            isVerticalScrollBarEnabled = false
            isFocusableInTouchMode = true
            isFocusable = true
            isHorizontalScrollBarEnabled = false

            CookieManager.getInstance().apply cookies@{
                setAcceptCookie(true)
                setAcceptThirdPartyCookies(this@apply, true)
            }

            setLayerType(WebView.LAYER_TYPE_HARDWARE, null)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun setSecondWebSettings(webView: WebView) {
        with(webView.settings) {
            mixedContentMode = 0
            javaScriptCanOpenWindowsAutomatically = true
            domStorageEnabled = true
            displayZoomControls = false
            cacheMode = android.webkit.WebSettings.LOAD_DEFAULT
            allowFileAccess = true
            loadWithOverviewMode = true
            allowContentAccess = true
            databaseEnabled = true
            useWideViewPort = true
            javaScriptEnabled = true
            setSupportMultipleWindows(false)
            builtInZoomControls = true
            @Suppress("DEPRECATION")
            setEnableSmoothTransition(true)
            loadsImagesAutomatically = true
            userAgentString = userAgentString.replace("; wv", "")
            @Suppress("DEPRECATION") if (Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.O_MR1) {
                saveFormData = true
            }
        }
    }
}