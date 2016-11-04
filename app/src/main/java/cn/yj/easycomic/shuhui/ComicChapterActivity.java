package cn.yj.easycomic.shuhui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.yj.easycomic.R;
import cn.yj.easycomic.shuhui.adapter.CatalogAdapter;
import cn.yj.easycomic.shuhui.model.ChapterModel;
import cn.yj.easycomic.shuhui.model.ShuHuiModel;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by yj on 2016/11/4.
 */
public class ComicChapterActivity extends Activity{
    private String id ;
    private final String EXTRA_ID = "extra_id";
    private ListView lvCatalog;
    private CatalogAdapter adapter;
    private List<ShuHuiModel> catalogList;
    private String hearCatalogUrl = "http://www.ishuhui.net/ComicBooks/GetChapterList";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_catalog);

        EventBus.getDefault().register(this);
        id = getIntent().getStringExtra(EXTRA_ID);

        lvCatalog = (ListView) findViewById(R.id.lv_catalog);
        catalogList = new ArrayList<ShuHuiModel>();
        adapter = new CatalogAdapter(this, catalogList);
        lvCatalog.setAdapter(adapter);
        lvCatalog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                new ComicContentActivity().into(ComicChapterActivity.this, catalogList.get(position).getId());
            }
        });

        requestChapter();

    }

    private void requestChapter() {
        OkHttpClient client = new OkHttpClient();
        Request.Builder builder = new Request.Builder().url(hearCatalogUrl + "?id=" + id + "&PageIndex=0");
        Request request = builder.build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                EventBus.getDefault().post(new FailChapterMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String rsp = response.body().string();
                try {
                    String jsonArray = new JSONObject(new JSONObject(rsp).get("Return").toString()).get("List").toString();
                    JSONArray array = new JSONArray(jsonArray);
                    for (int i = 0; i < array.length(); i++) {
                        ChapterModel chapterModel = new ChapterModel();
                        String title = array.getJSONObject(i).get("Title").toString();
                        String id = array.getJSONObject(i).get("Id").toString();
                        String iconUrl = array.getJSONObject(i).get("FrontCover").toString();
                        String extra = array.getJSONObject(i).get("ChapterNo").toString();
                        chapterModel.setTitle(title);
                        chapterModel.setId(id);
                        chapterModel.setIconUrl(iconUrl);
                        chapterModel.setExtra(extra);
                        EventBus.getDefault().post(chapterModel);
                    }

                } catch (JSONException e) {
                    Log.d("MyTAG", "error--> id = " + id + "message : " + e.getMessage());
                    e.printStackTrace();
                }

            }
        });
    }

    public void into(Context context, String id) {
        Intent intent = new Intent(context, ComicChapterActivity.class);
        intent.putExtra(EXTRA_ID, id);
        context.startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ChapterModel model) {
        catalogList.add(model);
        adapter.notifyDataSetChanged();
    };
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FailChapterMessage model) {
        Toast.makeText(ComicChapterActivity.this, "请求章节失败", Toast.LENGTH_SHORT).show();

    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    class FailChapterMessage{}
}
