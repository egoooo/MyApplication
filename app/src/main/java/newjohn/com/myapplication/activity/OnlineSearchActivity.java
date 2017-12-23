package newjohn.com.myapplication.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import newjohn.com.myapplication.MyApplication;
import newjohn.com.myapplication.R;
import newjohn.com.myapplication.adapter.RecyclerAdapter;
import newjohn.com.myapplication.adapter.RefreshAdapter;
import newjohn.com.myapplication.bean.DeviceDb;
import newjohn.com.myapplication.bean.DeviceDbDao;
import newjohn.com.myapplication.bean.OnlineData;
import newjohn.com.myapplication.global.Global;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class OnlineSearchActivity extends BaseActivity {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.onlineo_spinner)
    Spinner spinner;
    private List<OnlineData> data_list;
    RefreshAdapter mRefreshAdapter;
    OkHttpClient okHttpClient;
    private ArrayAdapter<String> adapter;
    private DeviceDbDao deviceDbDao;
    private int page=1;
    private String area="null";

    List<String> list;
    private String TAG="OnlineSearchActivity";
    Gson gson;
    List<OnlineData> onlineDatas=new ArrayList<OnlineData>();//Gson解析使用
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {


            super.handleMessage(msg);

            String str= (String) msg.obj;
            Log.i(TAG, "handleMessage: "+str);
            onlineDatas=gson.fromJson(str,new TypeToken<ArrayList<OnlineData>>() {}.getType());
            Log.i(TAG, "onResponse: "+str);
            switch (msg.what){
                case 1:
                    data_list.clear();
                    data_list.addAll(onlineDatas);
                    mRefreshAdapter.notifyDataSetChanged();
                    break;
                case 2:
                    for (int i=0;i<onlineDatas.size();i++){
                        data_list.add(onlineDatas.get(i));
                    }
                    mRefreshAdapter.notifyDataSetChanged();
                    break;
            }



            Log.i(TAG, "run: "+data_list.size());
            mRefreshAdapter.notifyDataSetChanged();





            if (data_list.size()<20){

                mRefreshAdapter.changeMoreStatus(mRefreshAdapter.NO_LOAD_MORE);

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_search);
        ButterKnife.bind(this);
        okHttpClient= new OkHttpClient();
        deviceDbDao= MyApplication.getMyApplication().getDaoSession().getDeviceDbDao();
        data_list=new ArrayList<>();
        gson=new Gson();


        initLoadMoreListener();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRefreshAdapter=new RefreshAdapter(getApplicationContext(),data_list);
        recyclerView.setAdapter(mRefreshAdapter);
        recyclerView.addItemDecoration(new DefaultItemDecoration(Color.BLACK));
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                page=1;
                mRefreshAdapter.delete();
                getDataFromNet();

                swipeRefreshLayout.setRefreshing(false);

            }
        });

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,getSpinnerDataSource());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position==0){
                    area="null";
                    page=1;
                  getDataFromNet();
                }
                if (position>0){
                    area=list.get(position);
                    page=1;
                   getDataFromNet();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        getDataFromNet();


    }

//    public List<OnlineData> getData(){
//        //cion和iconName的长度是相同的，这里任选其一都可以
//        for(int i=0;i<20;i++){
//            OnlineData onlineData=new OnlineData();
//            onlineData.setArea("桥梁：90"+i+"区");
//            onlineData.setDeviceNum("08989"+i);
//            onlineData.setStatus("1");
//           data_list.add(onlineData);
//        }
//
//        return data_list;
//    }




    private void initLoadMoreListener() {

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                //判断RecyclerView的状态 是空闲时，同时，是最后一个可见的ITEM时才加载
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == mRefreshAdapter.getItemCount()) {


                    page++;

                    RequestBody requestBody=new FormBody.Builder()
                            .add("userName", Global.user.getUserName())
                            .add("page",page+"")
                            .add("pageSize",20+"")
                            .add("area",area).build();
                    Log.i(TAG, "getDataFromNet: "+page+Global.user.getUserName()+area);
                    final Request request=new Request.Builder()
                            .url(Constant.URL+"WebProject/statusSearch")
                            .post(requestBody)
                            .build();
                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    mRefreshAdapter.changeMoreStatus(mRefreshAdapter.NET_ERROR);


                                }
                            });



                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final String str=response.body().string();
                            Message msg = handler.obtainMessage();
                            msg.what = 2;
                            msg.obj =str;
                            handler.sendMessage(msg);








                        }
                    });
                    //设置正在加载更多
                    mRefreshAdapter.changeMoreStatus(mRefreshAdapter.LOADING_MORE);









                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                //最后一个可见的ITEM
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
            }
        });

    }



    public  void getDataFromNet(){

        RequestBody requestBody=new FormBody.Builder()
                .add("userName", Global.user.getUserName())
                .add("page",page+"")
                .add("pageSize",20+"")
                .add("area",area).build();
        Log.i(TAG, "getDataFromNet: "+page+Global.user.getUserName()+area);
        final Request request=new Request.Builder()
                .url(Constant.URL+"WebProject/statusSearch")
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        mRefreshAdapter.changeMoreStatus(mRefreshAdapter.NET_ERROR);


                    }
                });



            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String str=response.body().string();
                Message msg = handler.obtainMessage();
                msg.what = 1;
                msg.obj =str;
                handler.sendMessage(msg);








            }
        });


    }



    public List<String> getSpinnerDataSource(){
        List<DeviceDb> dls=deviceDbDao.loadAll();


         list=new ArrayList<>();
        list.add("all");
        for (int i=0;i<dls.size();i++){
            list.add(dls.get(i).getArea());

        }

        return list;
    }






}
