package com.source.analyst

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.webkit.WebView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity

class WebActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private var myOwnFilePath: ValueCallback<Array<Uri?>?>? = null

    private var link: String? = null
    private var exceptionPartOfUrl: String? = null

    val PICK_PICTURE_REQUESR_CODE = 733

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        webView.restoreState(savedInstanceState)
    }

    override fun onPause() {
        CookieManager.getInstance().flush()
        super.onPause()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        webView.saveState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        link = intent.getStringExtra("link")
        exceptionPartOfUrl = intent.getStringExtra("property")

        webView = WebView(this)

        webView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        setUpWebView()

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (webView.canGoBack()) {
                        webView.goBack()
                    }
                }
            }
        )

        setContentView(webView)

        Log.e("", "load url ${link.toString()}")

        webView.loadUrl(link!!)
    }

    private fun setUpWebView() {
        webView.webChromeClient =
            ClientsHandler.getWebChromeClient(this, PICK_PICTURE_REQUESR_CODE) {
                myOwnFilePath = it
            }

        webView.webViewClient = ClientsHandler.getWebViewClient(
            this@WebActivity,
            exceptionPartOfUrl
        )

        ClientsHandler.setFirstWebSettings(webView)
        ClientsHandler.setSecondWebSettings(webView)

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            PICK_PICTURE_REQUESR_CODE -> {
                if (resultCode == RESULT_OK && data?.data != null) {
                    myOwnFilePath?.onReceiveValue(arrayOf(data.data))
                } else {
                    myOwnFilePath?.onReceiveValue(null)
                }
            }

            else -> {}
        }
    }
}