package tk.goshujin.app

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.activity.addCallback
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewCompat
import androidx.webkit.WebViewFeature
import com.google.android.material.progressindicator.LinearProgressIndicator
import tk.goshujin.app.databinding.FragmentHomeBinding

class HomeFragment : Fragment(R.layout.fragment_home) {

    companion object {
        const val BASE_URL = "https://goshujin.tk/"

        fun newInstance() = HomeFragment()
    }

    private var binding: FragmentHomeBinding? = null
    private var isSafeBrowsingInitialized: Boolean = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            // Check if the key event was the Back button and if there's history
            if (binding?.webView?.canGoBack() == true) {
                binding?.webView?.goBack()
            } else {
                // If it wasn't the Back key or there's no web page history, bubble up to the default
                // system behavior (probably exit the activity)
                isEnabled = false
                activity?.onBackPressed()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view).apply {
            // Toolbar
            initToolbar(toolbar, webView)
            // WebView and other Views
            initWebView(webView, pageLoadingProgress, toolbar, swipeRefreshLayout)
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun initToolbar(toolbar: Toolbar, webView: WebView) {
        toolbar.apply {
            inflateMenu(R.menu.menu_webview)
            setOnMenuItemClickListener {
                return@setOnMenuItemClickListener when (it.itemId) {
                    R.id.menu_zoomIn -> {
                        webView.zoomIn()
                        true
                    }
                    R.id.menu_zoomOut -> {
                        webView.zoomOut()
                        true
                    }
                    R.id.menu_share -> {
                        startActivity(
                            Intent.createChooser(
                                Intent(Intent.ACTION_SEND).apply {
                                    putExtra(Intent.EXTRA_TEXT, webView.url)
                                    type = "text/plain"
                                },
                                null
                            )
                        )
                        true
                    }
                    R.id.menu_openInBrowser -> {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(webView.url)))
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun initWebView(
        webView: WebView,
        pageLoadingProgress: LinearProgressIndicator,
        toolbar: Toolbar,
        swipeRefreshLayout: SwipeRefreshLayout
    ) {
        webView.apply {
            settings.apply {
                @SuppressLint("SetJavaScriptEnabled")
                javaScriptEnabled = true
                setSupportMultipleWindows(true)
                useWideViewPort = true
                loadWithOverviewMode = true
                setSupportZoom(true)
                builtInZoomControls = true
                displayZoomControls = false

                // To load image properly
                loadsImagesAutomatically = true
                domStorageEnabled = true
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                }

                // Set Page Title to Toolbar and stop SwipeRefreshLayout
                webViewClient = MainWebViewClientCompat(context, BASE_URL) { pageTitle ->
                    toolbar.title = pageTitle
                    swipeRefreshLayout.isRefreshing = false
                }
                webChromeClient = MainWebChromeClient { newProgress ->
                    pageLoadingProgress.apply {
                        when (newProgress) {
                            0 -> show()
                            100 -> hide()
                            else -> setProgressCompat(newProgress, true)
                        }
                    }
                }
            }

            // Dark theme support
            if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                WebSettingsCompat.setForceDark(settings, WebSettingsCompat.FORCE_DARK_AUTO)
            }

            // Safe Browsing
            if (WebViewFeature.isFeatureSupported(WebViewFeature.START_SAFE_BROWSING)) {
                WebViewCompat.startSafeBrowsing(context) { success ->
                    isSafeBrowsingInitialized = success
                    loadUrl(BASE_URL)
                }
            } else {
                loadUrl(BASE_URL)
            }

            swipeRefreshLayout.setOnRefreshListener {
                webView.reload()
            }
        }
    }
}