package cn.yj.easycomic.shuhui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import cn.yj.easycomic.R;
import cn.yj.easycomic.shuhui.model.ShuHuiModel;
import cn.yj.easycomic.shuhui.transformation.CircleTransformation;

/**
 * Created by yj on 2016/11/4.
 */
public class CatalogAdapter extends BaseAdapter implements View.OnClickListener{
    private Context context;
    private List<ShuHuiModel> catalogList;
    private Holder holder;
    public CatalogAdapter(Context context, List<ShuHuiModel> catalogList) {
        this.context = context;
        this.catalogList = catalogList;

    }
    @Override
    public int getCount() {
        return catalogList.size();
    }

    @Override
    public Object getItem(int position) {
        return catalogList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.catalog_item, parent, false);
            holder = new Holder();
            holder.icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            holder.title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.extra = (TextView) convertView.findViewById(R.id.tv_extra);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        Picasso.with(context).load(catalogList.get(position).getIconUrl()).placeholder(R.mipmap.loading).into(holder.icon);
        holder.title.setText(catalogList.get(position).getTitle());
        holder.extra.setText(catalogList.get(position).getExtra());
        return convertView;
    }

    @Override
    public void onClick(View v) {

    }

    class Holder {
        ImageView icon;
        TextView title;
        TextView extra;
    }


}
