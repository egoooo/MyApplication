package newjohn.com.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import newjohn.com.myapplication.R;


/**
 * Created by Administrator on 2017/11/7.
 */

public class MyGridAdapter extends BaseAdapter {
    private Context context;
    private List<Map<String, Object>> mDataList;

    public MyGridAdapter(Context context,List<Map<String, Object>> dataList) {

        this.context=context;
        this.mDataList=dataList;
    }

    @Override
    public int getCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        Holder holder;
        if (convertView==null){
            holder=new Holder();
            convertView=LayoutInflater.from(context).inflate(R.layout.gridview_item,null);
            holder.iv = (ImageView) convertView.findViewById(R.id.iv_item);
            //设置显示图片
            holder.iv.setBackgroundResource((int) mDataList.get(position).get("image"));
            holder.iv.setScaleType(ImageView.ScaleType.FIT_XY);
            holder.tv = (TextView) convertView.findViewById(R.id.tv_item);
            //设置标题
            holder.tv.setText((String)mDataList.get(position).get("text"));
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        return convertView;
    }

    class Holder {
        ImageView iv;
        TextView tv;
     }
}
