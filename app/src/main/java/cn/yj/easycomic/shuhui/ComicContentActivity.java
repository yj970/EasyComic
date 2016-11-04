package cn.yj.easycomic.shuhui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import cn.yj.easycomic.R;

/**
 * Created by yj on 2016/11/4.
 */
public class ComicContentActivity extends Activity{
    private String id ;
    private final String EXTRA_ID = "extra_id";
    private WebView wv;
    private StringBuilder url;
    private ProgressBar pb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_chapter);

        id = getIntent().getStringExtra(EXTRA_ID);
        url = new StringBuilder("http://www.ishuhui.net/ComicBooks/ReadComicBooksToIsoV1/");
        url.append(id).append(".html");
        wv = (WebView) findViewById(R.id.wv);
        pb = (ProgressBar) findViewById(R.id.pb);

        //声明WebSettings子类
        WebSettings webSettings = wv.getSettings();
        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.setJavaScriptEnabled(true);
        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        //缩放操作
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件
        //其他细节操作
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
        wv.setWebViewClient(new WebViewClient());
        wv.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                pb.setProgress(newProgress);
                if(newProgress == 100) {
                    pb.setVisibility(View.INVISIBLE);
                }
            }
        });

        wv.loadUrl(url.toString());

    }

    @Override
    protected void onDestroy() {
        // 清理垃圾
        if (wv != null) {
            wv.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            wv.clearHistory();
            ((ViewGroup) wv.getParent()).removeView(wv);
            wv.destroy();
            wv = null;
        }
        super.onDestroy();
    }

    public void into(Context context, String id) {
        Intent intent = new Intent(context, ComicContentActivity.class);
        intent.putExtra(EXTRA_ID, id);
        context.startActivity(intent);
    }
}
