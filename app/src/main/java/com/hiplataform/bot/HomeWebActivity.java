package com.hiplataform.bot;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class HomeWebActivity extends AppCompatActivity {

    private Activity oActivity = this;
    private WebView webViewHome;

    private String webSiteUrl = "https://wsquare.com.br/buser.html";

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_web);

        webViewHome = (WebView) findViewById(R.id.webViewHome);

        webViewHome.setWebViewClient(new WebViewClient());
        webViewHome.addJavascriptInterface(new WebAppInterface(this), "Android");

        WebSettings settings = webViewHome.getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setDefaultTextEncodingName("utf-8");

        String fullURL = this.webSiteUrl;

        webViewHome.loadUrl(fullURL);
    }


}

class WebAppInterface {

    Activity mContext;

    /** Instantiate the interface and set the context */
    WebAppInterface(Activity c) {
        mContext = c;
    }

    /** Show a toast from the web page */
    @JavascriptInterface
    public void abrirChatHi(){
        //Move to Next screen
        Intent newintent = new Intent(mContext, BotIniciarPermissoesActivity.class);
        mContext.startActivity(newintent);
    }
}