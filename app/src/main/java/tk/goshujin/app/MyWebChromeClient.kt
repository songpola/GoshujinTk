package tk.goshujin.app

import android.webkit.WebChromeClient
import android.webkit.WebView

class MyWebChromeClient(
    private val onProgressChangedCallback: (Int) -> Unit
) : WebChromeClient() {
    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        super.onProgressChanged(view, newProgress)
        onProgressChangedCallback(newProgress)
    }
}
