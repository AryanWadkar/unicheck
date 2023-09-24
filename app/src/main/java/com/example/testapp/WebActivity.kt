package com.example.testapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient

class WebActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)

        val bundle:Bundle?=intent.extras
        val URL=bundle!!.getString("URL")

        webView = findViewById<WebView>(R.id.webView)

        webView.webViewClient = WebViewClient()

        if (URL != null) {
            webView.loadUrl(URL)
        }

        webView.settings.setSupportZoom(true)
    }

    override fun onBackPressed() {
        if (webView.canGoBack())
            webView.goBack()
        else
            super.onBackPressed()
    }
}