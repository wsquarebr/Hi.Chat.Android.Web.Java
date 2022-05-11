package com.hiplataform.bot;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Array;

public class BotWebActivity extends AppCompatActivity {

    private static String scriptRemoveReader = "document.getElementsByClassName(\"bot-header-button\")[0].style.display='none';";
    private boolean carregouScript = false;

    private Activity oActivity = this;
    private WebView webViewChat;

    private ValueCallback<Uri[]> uploadMessage = null;
    private ValueCallback mUploadMessage = null;

    private int REQUEST_SELECT_FILE = 100;
    private int FILECHOOSER_RESULT_CODE = 1;

    private boolean atendimentoChat = true;
    private String hiUrl = "";

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_web);

        configurarBarraSuperior();

        webViewChat = (WebView) findViewById(R.id.webViewChat);

        webViewChat.setWebViewClient(new WebViewClient());

        WebSettings settings = webViewChat.getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);

        settings.setDefaultTextEncodingName("utf-8");

        if(Build.VERSION.SDK_INT >=21)
        {
            settings.setMixedContentMode(0);
            webViewChat.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        else if(Build.VERSION.SDK_INT >=19)
        {
            webViewChat.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        else
        {
            webViewChat.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        webViewChat.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        webViewChat.setWebChromeClient(new WebChromeClient() {
            /**
             * File upload for Android HoneyComb (3.X) versions
             * This is a fallback signature method from some legacy HoneyComb versions
             */
            public void openFileChooser(ValueCallback uploadMsg) {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                oActivity.startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULT_CODE);
            }

            /**
             * File upload for Android HoneyComb (3.X) versions
             */
            // Some Android 3.0+ versions use a different method signature
            public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                oActivity.startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULT_CODE);
            }

            /**
             * File upload for Android JellyBean (4.1) versions
             */
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
            public void openFileChooser(ValueCallback uploadMsg, String acceptType, String capture) {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                oActivity.startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULT_CODE);
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            public boolean onShowFileChooser(WebView webViewChat , ValueCallback<Uri[]> filePathCallback , WebChromeClient.FileChooserParams fileChooserParams ) {
                if (uploadMessage != null) {
                    uploadMessage.onReceiveValue(null);
                    uploadMessage = null;
                }

                uploadMessage = filePathCallback;

                Intent contentSelection = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelection.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelection.setType("*/*");

                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelection);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Choose your file");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { contentSelection });

                try {
                    startActivityForResult(chooserIntent, REQUEST_SELECT_FILE);
                } catch (Exception e) {
                    uploadMessage = null;
                    Toast.makeText(oActivity, e.getMessage(), Toast.LENGTH_LONG).show();
                    return false;
                }

                return true;
            }

            /**
             * Neccessary for WebView audio capture permission
             * This REQUIRES Android Lollipop(5.0) version or higher
             * Lower versions of Android WebView doesn't support getUserMedia
             */
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            public void onPermissionRequest(PermissionRequest request) {
                request.grant(request.getResources());
            }

            @Override
            public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {

                if(message.toLowerCase().contains("changes you made") || message.toLowerCase().contains("suas alterações"))
                {
                    result.confirm();
                    return false;
                }

                return super.onJsBeforeUnload(view, url, message, result);
            }
        });

        webViewChat.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

                if (request.getUrl().toString().toLowerCase().contains("humanform") || request.getUrl().toString().toLowerCase().contains("chatid"))
                {
                    atendimentoChat = true;
                }

                final String urlRequest = request.getUrl().toString().toLowerCase();

                if (urlRequest.contains("http://exitme")) {
                    finish();
                }
                else
                {
                    hiUrl = request.getUrl().toString();
                    view.loadUrl(hiUrl);
                }

                return true;
            }
        });

        String fullURL = getString(R.string.BOT_URL);

        webViewChat.loadUrl(fullURL);
    }

    public void onActivityResult(int requestCode , int resultCode , Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == REQUEST_SELECT_FILE) {
                if (uploadMessage == null) {
                    return;
                }
                uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
                uploadMessage = null;
            }
        } else if (requestCode == FILECHOOSER_RESULT_CODE) {
            if (mUploadMessage == null) {
                return;
            }

            Uri result = null;
            if (intent != null || resultCode == RESULT_OK) {
                result = intent.getData();
            }

            mUploadMessage.onReceiveValue(result);

            mUploadMessage = null;
        } else {
            Toast.makeText(this, "Erro no upload de arquivo", Toast.LENGTH_LONG)
                    .show();
        }
    }

    public void configurarBarraSuperior()
    {
        ActionBar mActionBar = getSupportActionBar();

        if(mActionBar != null) {
            mActionBar.setIcon(null);

            LayoutInflater mInflater = LayoutInflater.from(this);

            View mCustomView = mInflater.inflate(R.layout.view_navigation, null);

            TextView lblTitulo = (TextView) mCustomView.findViewById(R.id.lblTitulo);
            lblTitulo.setText(this.getTitle());

            ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                    ActionBar.LayoutParams.MATCH_PARENT,
                    ActionBar.LayoutParams.MATCH_PARENT);

            mActionBar.setCustomView(mCustomView, params);
            mActionBar.setDisplayShowCustomEnabled(true);
        }
    }

    public void voltar(View view)
    {
        //Caso esteja em atendimento Bot
        new AlertDialog.Builder(oActivity)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Sair")
                .setMessage("Deseja realmente sair?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if(!atendimentoChat) {
                            webViewChat.loadUrl("javascript:(function(){ HiBot.reset(); document.location.href='http://exitme'; })()");
                        }
                        else{
                            webViewChat.loadUrl("javascript:(function(){ var status = Hi.Chat.status();  if(status.toLowerCase() == 'ondialog'){ Hi.Chat.quitDialog(); } else if(status.toLowerCase() == 'onqueue'){ Hi.Chat.quitQueue(); } else { document.location.href='http://exitme'; }     })()");
                        }


                    }
                })
                .setNegativeButton("Cancelar", null).show();
    }

    @Override
    public void onBackPressed() {
        voltar(null);
    }
}
