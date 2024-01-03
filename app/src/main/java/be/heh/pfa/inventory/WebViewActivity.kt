package be.heh.pfa.inventory

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.WebViewClient
import be.heh.pfa.R
import kotlinx.android.synthetic.main.activity_web_view.wv_ManufacturerWebsite_WebViewActivity

class WebViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        val url = intent.getStringExtra("url")
        if (url != null) {
            wv_ManufacturerWebsite_WebViewActivity.loadUrl(url)
            wv_ManufacturerWebsite_WebViewActivity.settings.javaScriptEnabled = true
            wv_ManufacturerWebsite_WebViewActivity.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: android.webkit.WebView?,
                    url: String?
                ): Boolean {
                    view?.loadUrl(url!!)
                    return true
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {

        if (keyCode == KeyEvent.KEYCODE_BACK && wv_ManufacturerWebsite_WebViewActivity.canGoBack()) {
            wv_ManufacturerWebsite_WebViewActivity.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}