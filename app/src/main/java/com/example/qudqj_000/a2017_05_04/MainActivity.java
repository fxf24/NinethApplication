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
import android.widget.AdapterView;
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
    LinearLayout linear1, linear2;
    ListView lv1;
    ArrayList<String> sitename = new ArrayList<>();
    ArrayList<Data> data = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("웹뷰");
        program();
    }

    void program(){
        lv1 = (ListView)findViewById(R.id.list1);
        linear1 = (LinearLayout)findViewById(R.id.linear1);
        linear2 = (LinearLayout)findViewById(R.id.web);
        wv1 = (WebView)findViewById(R.id.webView1);
        et1 = (EditText)findViewById(R.id.url1);
        dialog = new ProgressDialog(this);


        wv1.addJavascriptInterface(new JavaScriptMethods(), "MyApp");
        WebSettings webSettings = wv1.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, sitename);
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

        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                wv1.loadUrl(data.get(position).getUrl());
                linear2.setVisibility(View.VISIBLE);
                lv1.setVisibility(View.INVISIBLE);
                linear1.setVisibility(View.VISIBLE);

            }
        });

        lv1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                dlg.setTitle("삭제")
                        .setIcon(R.mipmap.ic_launcher)
                        .setMessage("삭제 하시겠습니까?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                data.remove(position);
                                sitename.remove(position);
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
                return true;
            }
        });

        wv1.loadUrl("http://www.naver.com");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "즐겨찾기추가");
        menu.add(0, 2, 1,"즐겨찾기목록");

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==1){
            wv1.loadUrl("file:///android_asset/www/urladd.html");
            linear2.setVisibility(View.VISIBLE);
            lv1.setVisibility(View.INVISIBLE);
            linear1.setAnimation(animTop);
        }
        else if(item.getItemId()==2){
            linear2.setVisibility(View.INVISIBLE);
            lv1.setVisibility(View.VISIBLE);
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClick(View v){
        if(v.getId() == R.id.button1){

        }
    }

    Handler myhandler = new Handler();

    class JavaScriptMethods{
        JavaScriptMethods(){}

        @JavascriptInterface
        public void addToList(final String name, final String url){
            myhandler.post(new Runnable() {
                @Override
                public void run() {
                    if(data.size()==0){
                        data.add(new Data(name, "http://"+url));
                        sitename.add(name);
                        adapter.notifyDataSetChanged();
                        wv1.loadUrl("javascript:setMsg('등록되었습니다.')");
                    }
                    else {
                        boolean flag = false;
                        for (int i = 0; i<data.size();i++) {
                            if(("http://"+url).equals(data.get(i).getUrl())){
                                flag = true;
                            }
                        }
                        if (flag) {
                            wv1.loadUrl("javascript:displayMsg()");
                            Toast.makeText(getApplicationContext(),"들어옴",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            data.add(new Data(name, "http://"+url));
                            sitename.add(name);
                            adapter.notifyDataSetChanged();
                            wv1.loadUrl("javascript:setMsg('등록되었습니다.')");
                        }
                    }
                }
            });
        }

        @JavascriptInterface
        public void layoutVisible(){
            myhandler.post(new Runnable() {
                @Override
                public void run() {
                    linear1.setVisibility(View.VISIBLE);
                }
            });
        }
    }
}
