package cn.yj.easycomic.shuhui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.yj.easycomic.R;
import cn.yj.easycomic.shuhui.adapter.CatalogAdapter;
import cn.yj.easycomic.shuhui.model.CatalogModel;
import cn.yj.easycomic.shuhui.model.ShuHuiModel;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private ListView lvCatalog;
    private CatalogAdapter adapter;
    private List<ShuHuiModel> catalogList;
    private String hearCatalogUrl = "http://www.ishuhui.net/ComicBooks/GetChapterList";
    private int id = 1;
    private int max = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EventBus.getDefault().register(this);

        lvCatalog = (ListView) findViewById(R.id.lv_catalog);
        catalogList = new ArrayList<ShuHuiModel>();
        adapter = new CatalogAdapter(this, catalogList);
        lvCatalog.setAdapter(adapter);
        lvCatalog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                new ComicChapterActivity().into(MainActivity.this, catalogList.get(position).getId());
            }
        });

        // 请求目录
        requestCatalog(id);
    }


    private void requestCatalog(final int id) {
        OkHttpClient client = new OkHttpClient();
        Request.Builder builder = new Request.Builder().url(hearCatalogUrl + "?id=" + id + "&PageIndex=20");
        Request request = builder.build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                EventBus.getDefault().post(new FailCatalogMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String rsp = response.body().string();
                CatalogModel catalogModel = new CatalogModel();
                try {
                    String title = new JSONObject(new JSONObject(new JSONObject(rsp).get("Return").toString()).get("ParentItem").toString()).get("Title").toString();
                    String iconUrl = new JSONObject(new JSONObject(new JSONObject(rsp).get("Return").toString()).get("ParentItem").toString()).get("FrontCover").toString();
                    String explain = new JSONObject(new JSONObject(new JSONObject(rsp).get("Return").toString()).get("ParentItem").toString()).get("Explain").toString();
                    catalogModel.setTitle(title);
                    catalogModel.setExtra(explain);
                    catalogModel.setIconUrl(iconUrl);
                    catalogModel.setId(id + "");
                    EventBus.getDefault().post(catalogModel);
                } catch (JSONException e) {
                    Log.d("MyTAG", "error--> id = " + id + "message : " + e.getMessage());
                    catalogModel.setId("-1");
                    EventBus.getDefault().post(catalogModel);
                    e.printStackTrace();
                }

            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(CatalogModel model) {
        if (!model.getId().equals("-1")) {
            catalogList.add(model);
            adapter.notifyDataSetChanged();
        }
        if (id < max) {
            id++;
            requestCatalog(id);
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FailCatalogMessage model) {
        Toast.makeText(MainActivity.this, "请求目录失败", Toast.LENGTH_SHORT).show();
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    class FailCatalogMessage{}
}
