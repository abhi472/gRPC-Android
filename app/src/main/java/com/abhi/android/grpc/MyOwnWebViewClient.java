package com.abhi.android.grpc;

import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by abhi on 4/3/18.
 */

class MyOwnWebViewClient extends WebViewClient {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }
}
