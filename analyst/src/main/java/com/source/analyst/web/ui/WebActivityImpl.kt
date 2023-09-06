/*
package com.nttsolmare.game.android.privacyshower.web.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.CookieManager
import android.widget.Toast
import com.nttsolmare.game.android.privacyshower.core.GameClassData
import com.nttsolmare.game.android.privacyshower.core.common.Const
import com.nttsolmare.game.android.fithlib.core.util.crypto_manager.CryptoManager
import com.nttsolmare.game.android.privacyshower.core.util.crypto_manager.CryptoManagerImpl
import com.nttsolmare.game.android.fithlib.core.util.setupNotificationHandler
import com.nttsolmare.game.android.fithlib.core.util.web.WebActivity
//import com.nttsolmare.game.android.privacyshower.databinding.ActivityWebImplBinding
import com.nttsolmare.game.android.privacyshower.web.opearator.WebOperator
import com.nttsolmare.game.android.privacyshower.web.util.WebChromeClient
import com.nttsolmare.game.android.privacyshower.web.util.WebViewClient
//import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
internal class WebActivityImpl @Inject constructor(
) : AppCompatActivity(), WebActivity {

    private var myUri: Uri? = null

    @Inject
    lateinit var operator: WebOperator

    private lateinit var binding: ActivityWebImplBinding

    @SuppressLint("AnnotateVersionCheck")
    private val isVersionLvlHigh = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

    private val webChromeClient = WebChromeClient(this)

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        binding.web.restoreState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.web.saveState(outState)
    }

    override fun onPause() {
        CookieManager.getInstance().flush()
        super.onPause()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val crypto : CryptoManager = CryptoManagerImpl()

        binding = ActivityWebImplBinding.inflate(layoutInflater)

        setContentView(binding.root)

        Toast.makeText(this,"Loading..",Toast.LENGTH_LONG).show()

        var url = intent.getStringExtra(Const.LINK_KEY) as String

        var isSavingIsNotNecessary = intent.getBooleanExtra(Const.TRACKER_KEY, true)

        val title: String? = intent.getStringExtra(Const.TITLE_KEY)

        setupNotificationHandler(this, title = title)

        operator.setWebSettings(binding.web)

        operator.handleOnBackPressed(this,binding.web,url,onBackPressedDispatcher)

        binding.web.webChromeClient = webChromeClient

        binding.web.webViewClient = WebViewClient(
            activity = this@WebActivityImpl,
            openGameCullBack = {
                val intent = Intent(this@WebActivityImpl, GameClassData.gameClass)
                 startActivity(intent)
                 finish()
            },
            saveLinkCoolBack = { newUrl ->
                if (isSavingIsNotNecessary) return@WebViewClient

                url = newUrl

                isSavingIsNotNecessary = true

                CoroutineScope(Dispatchers.IO).launch {
                    operator.saveLink(newUrl)
                }
            },
            title = title,
            crypto = crypto
        )

        binding.web.loadUrl(url)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Const.INPUT_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data == null) {
                webChromeClient.mFilePathCallback?.onReceiveValue(null)
                return
            }
            if (data.data != null) {
                val selectedImageUri = data.data
                if (selectedImageUri != null) {
                    webChromeClient.mFilePathCallback?.onReceiveValue(arrayOf(selectedImageUri))
                }
            } else {
                val bitmap = data.extras?.get("data") as Bitmap
                CoroutineScope(Dispatchers.Default).launch {
                    launch(Dispatchers.IO) {
                        myUri = operator.createFile(this@WebActivityImpl, bitmap, isVersionLvlHigh)
                    }.join()

                    webChromeClient.mFilePathCallback?.onReceiveValue(arrayOf(myUri))
                }
            }
        } else {
            webChromeClient.mFilePathCallback?.onReceiveValue(null)
        }
    }
}*/
