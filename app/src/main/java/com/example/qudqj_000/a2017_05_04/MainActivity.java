package com.example.qudqj_000.a2017_05_04;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    WebView wv1;
    EditText et1;
    ProgressDialog dialog;
    Animation animTop;
    LinearLayout linear1;
    ListView lv1;
    ArrayList<String> name = new ArrayList<>();
    ArrayList<Data> data = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        program();
    }

    void program(){
        lv1 = (ListView)findViewById(R.id.list1);
        wv1 = (WebView)findViewById(R.id.webView1);
        et1 = (EditText)findViewById(R.id.url1);
        dialog = new ProgressDialog(this);

        wv1.addJavascriptInterface(new JavaScriptMethods(), "MyApp");
        WebSettings webSettings = wv1.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, name);
        lv1.setAdapter(adapter);

        wv1.setWebChromeClient(new WebChromeClient(){

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if(newProgress>=100) dialog.dismiss();
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                result.confirm();
                return super.onJsAlert(view, url, message, result);
            }
        });

        wv1.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                dialog.setMessage("Loading...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.show();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                et1.setText(url);
            }
        });
        wv1.loadUrl("http://www.naver.com");

        animTop = AnimationUtils.loadAnimation(this, R.anim.translate_top);
        animTop.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                linear1.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        linear1 = (LinearLayout)findViewById(R.id.linear1);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "즐겨찾기추가");
        menu.add(0,2,1,"즐겨찾기목록");

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==1){
            wv1.loadUrl("file:///android_asset/www/urladd.html");
            linear1.setAnimation(animTop);
//            animTop.start();
        }
        else if(item.getItemId()==2){

        }
        return super.onOptionsItemSelected(item);
    }

    public void onClick(View v){
        if(v.getId() == R.id.button1){

        }
        else if(v.getId() == R.id.button2){
            wv1.loadUrl("javascript:changeImage()");
        }
    }

    Handler myhandler = new Handler();

    class JavaScriptMethods{
        JavaScriptMethods(){}

        @JavascriptInterface
        public void displayToast(){
            myhandler.post(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                    dlg.setTitle("그림변경")
                            .setMessage("그림을 변경하시겠습니까?")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    wv1.loadUrl("javascript:changeImage()");
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                }
            });
        }

        @JavascriptInterface
        public void addToList(){
            myhandler.post(new Runnable() {
                @Override
                public void run() {

                }
            });
        }
    }
}
