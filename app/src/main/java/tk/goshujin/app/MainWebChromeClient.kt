package tk.goshujin.app

import android.webkit.WebChromeClient
import android.webkit.WebView

class MainWebChromeClient(
    private val onProgressChangedCallback: (Int) -> Unit
) : WebChromeClient() {
    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        super.onProgressChanged(view, newProgress)
        onProgressChangedCallback(newProgress)
    }

//    override fun onCreateWindow(
//        view: WebView?,
//        isDialog: Boolean,
//        isUserGesture: Boolean,
//        resultMsg: Message?
//    ): Boolean {
//
//        return true
//    }
}
