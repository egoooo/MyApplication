package newjohn.com.myapplication.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import newjohn.com.myapplication.R;


/**
 * Created by Administrator on 2017/11/7.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder >{
    private Context context;
    private List<Map<String, Object>> mDataList;
    public RecyclerAdapter(Context context,List<Map<String, Object>> dataList) {
        this.context=context;
        this.mDataList=dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_item,viewGroup,false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
//        holder.iv2.setBackgroundResource((int) mDataList.get(position).get("image"));
//        holder.iv2.setScaleType(ImageView.ScaleType.FIT_XY);
//        holder.tv.setText((String)mDataList.get(position).get("text"));

    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
//        @BindView(R.id.tv)
//        TextView tv;
//        @BindView(R.id.iv)
//        ImageView iv;
//        @BindView(R.id.iv2)
//        ImageView iv2;

        public ViewHolder(View view){
            super(view);
            ButterKnife.bind(this,view);

        }
    }
}
