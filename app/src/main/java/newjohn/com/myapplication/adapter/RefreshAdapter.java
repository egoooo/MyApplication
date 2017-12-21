package newjohn.com.myapplication.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import newjohn.com.myapplication.R;
import newjohn.com.myapplication.bean.OnlineData;

import static android.content.ContentValues.TAG;


/**
 * Created by 刘楠 on 2016/9/10 0010.18:06
 */
public class RefreshAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener{

    private OnItemClickListener mOnItemClickListener = null;
    Context        mContext;
    LayoutInflater mInflater;
    List<OnlineData>   mDatas;
    private static final int TYPE_ITEM   = 0;
    private static final int TYPE_FOOTER = 1;

    //上拉加载更多
    public static final int PULLUP_LOAD_MORE = 0;
    //正在加载中
    public static final int LOADING_MORE     = 1;
    //没有加载更多 隐藏
    public static final int NO_LOAD_MORE     = 2;
    //网络错误！
    public static final int NET_ERROR     = 3;

    //上拉加载更多状态-默认为0
    private int mLoadMoreStatus = 0;


    public RefreshAdapter(Context context, List<OnlineData> datas) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mDatas = datas;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {
            View itemView = mInflater.inflate(R.layout.recycler_item, parent, false);

            itemView.setOnClickListener(this);
            return new ItemViewHolder(itemView);
        } else if (viewType == TYPE_FOOTER) {
            View itemView = mInflater.inflate(R.layout.load_more_footview_layout, parent, false);

            return new FooterViewHolder(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ItemViewHolder) {

            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
             OnlineData        data            = mDatas.get(position);
            itemViewHolder.ereaTv.setText(data.getArea());
            if (data.getStatus().equals("1")){
                itemViewHolder.statusIv.setImageResource(R.drawable.on);
                itemViewHolder.statusIv.setScaleType(ImageView.ScaleType.FIT_XY);
                itemViewHolder.deviceNum.setText(data.getDeviceNum()+"在线");

            }
            else{
                itemViewHolder.statusIv.setImageResource(R.drawable.off);
                itemViewHolder.statusIv.setScaleType(ImageView.ScaleType.FIT_XY);
                itemViewHolder.deviceNum.setText(data.getDeviceNum()+"离线");

            }



        } else if (holder instanceof FooterViewHolder) {


            FooterViewHolder footerViewHolder = (FooterViewHolder) holder;


            switch (mLoadMoreStatus) {
                case PULLUP_LOAD_MORE:
                    footerViewHolder.mPbLoad.setVisibility(View.VISIBLE);
                    footerViewHolder.mTvLoadText.setText("上拉加载更多...");
                    break;
                case LOADING_MORE:
                    footerViewHolder.mPbLoad.setVisibility(View.VISIBLE);
                    footerViewHolder.mTvLoadText.setText("正加载更多...");
                    break;
                case NO_LOAD_MORE:
                    //隐藏加载更多
                    footerViewHolder.mTvLoadText.setText("没有更多了！");
                    footerViewHolder.mPbLoad.setVisibility(View.GONE);
                    break;
                case NET_ERROR:
                    footerViewHolder.mTvLoadText.setText("请检查网络！");
                    footerViewHolder.mPbLoad.setVisibility(View.GONE);


            }
        }

    }

    @Override
    public int getItemCount() {
        //RecyclerView的count设置为数据总条数+ 1（footerView）
        return mDatas.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {

        if (position + 1 == getItemCount()) {
            //最后一个item设置为footerView
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取position
            mOnItemClickListener.onItemClick(v,(int)v.getTag());
        }
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.erea)
        TextView ereaTv;
        @BindView(R.id.status)
        ImageView statusIv;
        @BindView(R.id.device)
        TextView  deviceNum;


        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            //initListener(itemView);
        }

//        private void initListener(View itemView) {
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(mContext, "poistion " + getAdapterPosition(), Toast.LENGTH_SHORT).show();
//
//                }
//            });
//        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.pbLoad)
        ProgressBar  mPbLoad;
        @BindView(R.id.tvLoadText)
        TextView     mTvLoadText;
        @BindView(R.id.loadLayout)
        LinearLayout mLoadLayout;
        public FooterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

//
//    public void AddHeaderItem(List<OnlineData> items) {
//        mDatas.addAll(0, items);
//        notifyDataSetChanged();
//    }
//
//    public void AddFooterItem(List<OnlineData> items) {
//        mDatas.addAll(items);
//        notifyDataSetChanged();
//    }
//
//    public void AddItem(List<OnlineData> items) {
//
//        mDatas.addAll(0, items);
//        Log.i(TAG, "AddItem: "+ mDatas.size());
//
//        notifyDataSetChanged();
//    }

//
//
   // java.lang.OutOfMemoryError: Failed to allocate a 47834640 byte allocation with 16777120 free bytes and 41MB until OOM
    //at java.util.ArrayList.add(ArrayList.java:118)
   // at newjohn.com.myapplication.adapter.RefreshAdapter.AddItemOneByOne(RefreshAdapter.java:206)
// public void AddItemOneByOne(List<OnlineData> items) {
//        for (int i=0;i<items.size();i++){
//            mDatas.add(items.get(i));
//        }
//
//        notifyDataSetChanged();
//    }

    public void delete() {
        mDatas.clear();
        notifyDataSetChanged();
    }

    /**
     * 更新加载更多状态
     * @param status
     */
    public void changeMoreStatus(int status){
        mLoadMoreStatus=status;
        notifyDataSetChanged();
    }


    //define interface
    public static interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
