package com.abhi.android.grpc.utils;

import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by abhi on 4/3/18.
 */

public class MyOwnWebViewClient extends WebViewClient {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }
}
