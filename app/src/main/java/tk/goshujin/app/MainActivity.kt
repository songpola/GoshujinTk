package tk.goshujin.app

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.TypedValue
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
import androidx.appcompat.app.AppCompatActivity
import androidx.webkit.*
import tk.goshujin.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var isSafeBrowsingInitialized: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.mainToolbar)

        binding.mainWebView.apply {
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
                    mixedContentMode = MIXED_CONTENT_ALWAYS_ALLOW
                }

                webViewClient = MyWebViewClientCompat(this@MainActivity)
                webChromeClient = MyWebChromeClient { newProgress ->
                    if (newProgress == 0) {
                        binding.mainProgressBar.show()
                        binding.mainSwipeRefreshLayout.isRefreshing = true
                    }
                    binding.mainProgressBar.progress = newProgress
                    if (newProgress == 100) {
                        binding.mainProgressBar.hide()
                        binding.mainSwipeRefreshLayout.isRefreshing = false
                    }
                }
            }

            // Dark theme support
            if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                WebSettingsCompat.setForceDark(settings, WebSettingsCompat.FORCE_DARK_AUTO)
            }

            // Safe Browsing
            if (WebViewFeature.isFeatureSupported(WebViewFeature.START_SAFE_BROWSING)) {
                WebViewCompat.startSafeBrowsing(this@MainActivity) { success ->
                    isSafeBrowsingInitialized = success
                    loadUrl("https://goshujin.tk/")
                }
            }
        }
        TypedValue().also {
            theme.resolveAttribute(R.attr.colorSecondary, it, true)
            binding.mainSwipeRefreshLayout.setColorSchemeResources(it.resourceId)
        }
        binding.mainSwipeRefreshLayout.setOnRefreshListener {
            binding.mainWebView.reload()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // Check if the key event was the Back button and if there's history
        if (keyCode == KeyEvent.KEYCODE_BACK && binding.mainWebView.canGoBack()) {
            binding.mainWebView.goBack()
            return true
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_webview, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_zoomIn -> {
                binding.mainWebView.zoomIn()
                true
            }
            R.id.menu_zoomOut -> {
                binding.mainWebView.zoomOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
