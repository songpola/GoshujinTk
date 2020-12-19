package tk.goshujin.app

import android.content.Context
import android.content.Intent
import android.os.Build
import android.webkit.WebResourceRequest
import android.webkit.WebView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.webkit.SafeBrowsingResponseCompat
import androidx.webkit.WebViewClientCompat
import androidx.webkit.WebViewFeature

class MyWebViewClientCompat(private val context: Context) : WebViewClientCompat() {

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean =
        if (request.url.host == "goshujin.tk") {
            // This is my web site, so do not override; let my WebView load the page
            false
        } else {
            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
            Intent(Intent.ACTION_VIEW, request.url).apply {
                ContextCompat.startActivity(context, this, null)
            }
            true // super.shouldOverrideUrlLoading(view, request) => always false
        }

    // Automatically go "back to safety" when attempting to load a website that
    // Google has identified as a known threat. An instance of WebView calls
    // this method only after Safe Browsing is initialized, so there's no
    // conditional logic needed here.
    override fun onSafeBrowsingHit(
        view: WebView,
        request: WebResourceRequest,
        threatType: Int,
        callback: SafeBrowsingResponseCompat
    ) {
        // The "true" argument indicates that your app reports incidents like
        // this one to Safe Browsing.
        if (WebViewFeature.isFeatureSupported(WebViewFeature.SAFE_BROWSING_RESPONSE_BACK_TO_SAFETY)) {
            callback.backToSafety(true)
        }
    }
}
