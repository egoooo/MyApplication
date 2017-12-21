package newjohn.com.myapplication.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import newjohn.com.myapplication.MyApplication;
import newjohn.com.myapplication.R;
import newjohn.com.myapplication.bean.DeviceDb;
import newjohn.com.myapplication.bean.DeviceDbDao;
import newjohn.com.myapplication.bean.History;
import newjohn.com.myapplication.bean.HistoryData;
import newjohn.com.myapplication.bean.Sensor;
import newjohn.com.myapplication.bean.SensorDao;
import newjohn.com.myapplication.excel.ExcelUtils;
import newjohn.com.myapplication.global.Global;
import newjohn.com.myapplication.listviewscroll.AutoListView;
import newjohn.com.myapplication.listviewscroll.CHScrollView;
import newjohn.com.myapplication.listviewscroll.ListViewScrollAdapter;
import newjohn.com.myapplication.ui.CustomDatePicker;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

import static android.support.design.R.styleable.MenuItem;


public class HistoryActivity extends BaseActivity implements  AutoListView.OnRefreshListener, AutoListView.OnLoadListener, AdapterView.OnItemClickListener {
    String TAG=HistoryActivity.class.getName();
    private AutoListView lstv;
    private CHScrollView headerScroll;
    List<Map<String, String>> list = new ArrayList<Map<String, String>>();//
    private ListViewScrollAdapter adapter; //表格的适配器
    private CustomDatePicker customDatePicker1, customDatePicker2;


    private OkHttpClient okHttpClient;
    private String start_time="2017-11-14 12:00";
    private String end_time="";
    private int page=1;
    private int pageSize=20;
    private String deviceNum="null";
    private  String area="null";

    private String[] devicesList=null;
    ArrayList<String> dl;

    private Gson gson=new Gson();
    List<HistoryData> historyDatas=new ArrayList<HistoryData>();//Gson解析使用
    SensorDao sensorDao;
    DeviceDbDao deviceDbDao;
    private Toolbar toolbar;
    @BindView(R.id.start)
    Button start;
    @BindView(R.id.start1)
    Button end;

    @BindView(R.id.spinner)
    Spinner spinner;

    @BindView(R.id.spinner1)
    Spinner areaSpinner;

    @BindView(R.id.search)
    Button searchButton;

    LinearLayout linearLayoutwait;


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressWarnings("unchecked")
        public void handleMessage(Message msg) {
            linearLayoutwait.setVisibility(View.GONE);
            List<Map<String, String>> result = (List<Map<String, String>>) msg.obj;
            switch (msg.what) {
                case AutoListView.REFRESH:
                    lstv.onRefreshComplete();
                    list.clear();
                    list.addAll(result);
                    break;
                case AutoListView.LOAD:
                    lstv.onLoadComplete();
                    list.addAll(result);
                    break;
            }
            lstv.setResultSize(result.size());
            adapter.notifyDataSetChanged();
        };
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        linearLayoutwait=findViewById(R.id.hwait);
        sensorDao= MyApplication.getMyApplication().getDaoSession().getSensorDao();
        deviceDbDao=MyApplication.getMyApplication().getDaoSession().getDeviceDbDao();
        okHttpClient=new OkHttpClient();

        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date=new Date();
        String str=sdf.format(date);
        Log.i(TAG, "onCreate: "+"time"+str);
        end_time=str;

        ButterKnife.bind(this);
        initView();
        initData();
        setTime();
        setSpinner();
        search();

    }


    private void initView() {
        toolbar=findViewById(R.id.toolbar_h);



        //设置导航图标要在setSupportActionBar方法之后
        setSupportActionBar(toolbar);


        headerScroll = (CHScrollView) findViewById(R.id.item_scroll_title);
        CHScrollView.CHScrollViewHelper.mHScrollViews.clear();

        CHScrollView.CHScrollViewHelper.mHScrollViews.add(headerScroll);
        lstv = (AutoListView) findViewById(R.id.scroll_list);
        adapter = new ListViewScrollAdapter(this, list, R.layout.auto_listview_item,
                new String[] { "area", "deviceNum", "upLimit", "lowLimit", "value", "dateTime"},
                new int[] { R.id.item_title, R.id.item_data1, R.id.item_data2, R.id.item_data3, R.id.item_data4,
                        R.id.item_data5},
                R.id.item_scroll, lstv);

        lstv.setAdapter(adapter);
        lstv.setOnRefreshListener(this);
        lstv.setOnLoadListener(this);
        lstv.setOnItemClickListener(this);
    }

    private void initData() {
        loadData(AutoListView.REFRESH);
    }

    private void loadData(final int what) {
        // 这里模拟从服务器获取数据
        new Thread(new Runnable() {

            @Override
            public void run() {

                switch (what){
                    case AutoListView.REFRESH:
                        page=1;
                        Log.i(TAG, "run: "+Global.user.getUserName());
                        RequestBody requestBody=new FormBody.Builder()
                                .add("userName", Global.user.getUserName())
                                .add("start_time",start_time)
                                .add("end_time",end_time)
                                .add("page",page+"")
                                .add("pageSize",pageSize+"")
                                .add("deviceNum",deviceNum)
                                .add("area",area).build();
                        Log.i(TAG, "run: "+Global.user.getUserName()+start_time+end_time+pageSize+page+deviceNum+area);
                        final Request request=new Request.Builder()
                                .url(Constant.URL+"WebProject/historySearch")
                                .post(requestBody)
                                .build();
                        okHttpClient.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.i(TAG, "onFailure: "+e.toString());

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        lstv.setResultSize(0);
                                        adapter.notifyDataSetChanged();
                                        Toast.makeText(HistoryActivity.this, "检查网络！", Toast.LENGTH_SHORT).show();


                                    }
                                });

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String str=response.body().string();

                                Log.i(TAG, "onResponse: "+str);


                                historyDatas=gson.fromJson(str,new TypeToken<ArrayList<HistoryData>>() {}.getType());

                                List<Map<String, String>> result = new ArrayList<Map<String, String>>();
                                Map<String, String> data = null;
                                sensorDao.deleteAll();
                                for (int i = 0; i < historyDatas.size(); i++) {
                                    data = new HashMap<String, String>();
                                    data.put("area", historyDatas.get(i).getArea());
                                    data.put("deviceNum", historyDatas.get(i).getDeviceNum());
                                    data.put("upLimit", historyDatas.get(i).getUpLimit());
                                    data.put("lowLimit" , historyDatas.get(i).getLowLimit());
                                    data.put("value" , historyDatas.get(i).getValue());
                                    data.put("dateTime" ,historyDatas.get(i).getDateTime());
                                    Sensor sensor=new Sensor(null,historyDatas.get(i).getArea(),historyDatas.get(i).getDeviceNum(),historyDatas.get(i).getUpLimit(),historyDatas.get(i).getLowLimit(),historyDatas.get(i).getValue(),historyDatas.get(i).getDateTime());

                                    sensorDao.insert(sensor);
                                    result.add(data);
                                }

                                Message msg = handler.obtainMessage();
                                msg.what = what;
                                msg.obj = result;
                                handler.sendMessage(msg);
                            }
                        });

                        break;
                    case AutoListView.LOAD:
                        page++;
                        RequestBody requestBody1=new FormBody.Builder()
                                .add("userName", Global.user.getUserName())
                                .add("start_time",start_time)
                                .add("end_time",end_time)
                                .add("page",page+"")
                                .add("pageSize",pageSize+"")
                                .add("deviceNum",deviceNum)
                                .add("area",area).build();
                        final Request request1=new Request.Builder()
                                .url("http://120.78.209.11:8080/WebProject/historySearch")
                                .post(requestBody1)
                                .build();
                        Log.i(TAG, "run: "+Global.user.getUserName()+start_time+end_time+pageSize+page+deviceNum+area);
                        okHttpClient.newCall(request1).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.i(TAG, "onFailure1: "+e.toString());
                                page--;
                               runOnUiThread(new Runnable() {
                                   @Override
                                   public void run() {
                                       lstv.setResultSize(0);
                                       adapter.notifyDataSetChanged();
                                       Toast.makeText(HistoryActivity.this, "检查网络！", Toast.LENGTH_SHORT).show();
                                   }
                               });

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {

                                String str=response.body().string();
                                Log.i(TAG, "onResponse1: "+str);

                                historyDatas=gson.fromJson(str,new TypeToken<ArrayList<HistoryData>>() {}.getType());

                                List<Map<String, String>> result = new ArrayList<Map<String, String>>();
                                Map<String, String> data = null;
                                for (int i = 0; i < historyDatas.size(); i++) {
                                    data = new HashMap<String, String>();
                                    data.put("area", historyDatas.get(i).getArea());
                                    data.put("deviceNum", historyDatas.get(i).getDeviceNum());
                                    data.put("upLimit", historyDatas.get(i).getUpLimit());
                                    data.put("lowLimit" , historyDatas.get(i).getLowLimit());
                                    data.put("value" , historyDatas.get(i).getValue());
                                    data.put("dateTime" ,historyDatas.get(i).getDateTime());

                                    result.add(data);
                                }

                                Message msg = handler.obtainMessage();
                                msg.what = what;
                                msg.obj =result;
                                handler.sendMessage(msg);


                            }
                        });
                        break;
                }





            }
        }).start();
    }

    /**
     * 重写AutoListView.OnRefreshListener, AutoListView.OnLoadListener, AdapterView.OnItemClickListener的
     *  onRefresh()、onLoad()、onItemClick()方法，实现下拉刷新，加载更多，表格item点击事件
     */

    @Override
    public void onRefresh() {
        loadData(AutoListView.REFRESH);
    }

    @Override
    public void onLoad() {
        loadData(AutoListView.LOAD);
    }




//    // 测试数据
//    public List<Map<String, String>> getData() {
//        List<Map<String, String>> result = new ArrayList<Map<String, String>>();
//        Map<String, String> data = null;
//        for (int i = 0; i < 10; i++) {
//            data = new HashMap<String, String>();
//            data.put("area", "area" + i);
//            data.put("deviceNum", "deviceNum" + 1 + "_" + i);
//            data.put("upLimit", "upLimit" + 2 + "_" + i);
//            data.put("lowLimit" , "lowLimit" + 3 + "_" + i);
//            data.put("value" , "value" + 4 + "_" + i);
//            data.put("dateTime" , "dateTime" + 5 + "_" + i);
//
//            result.add(data);
//        }
//        return result;
//    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        try {
            TextView textView = (TextView) adapterView.findViewById(R.id.item_data2);

            Toast.makeText(this, "你点击了：" + textView.getText(), Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {

        }
    }


    public void setTime(){
        Calendar cal= Calendar.getInstance();
        final int year = cal.get(Calendar.YEAR);       //获取年月日时分秒
        Log.i("wxy","year"+year);
        final int month = cal.get(Calendar.MONTH);   //获取到的月份是从0开始计数
        final int day = cal.get(Calendar.DAY_OF_MONTH);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
                String now = sdf.format(new Date());



                customDatePicker1 = new CustomDatePicker(HistoryActivity.this, new CustomDatePicker.ResultHandler() {
                    @Override
                    public void handle(String time) { // 回调接口，获得选中的时间
                        start.setText(time);
                        start_time=time;
                    }
                }, "2010-01-01 00:00", now); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
                customDatePicker1.showSpecificTime(true); // 不显示时和分
                customDatePicker1.setIsLoop(false); // 不允许循环滚动
                customDatePicker1.show(now);
            }
        });


        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
                String now = sdf.format(new Date());
                Toast.makeText(HistoryActivity.this, "lllllll", Toast.LENGTH_SHORT).show();


                customDatePicker2 = new CustomDatePicker(HistoryActivity.this, new CustomDatePicker.ResultHandler() {
                    @Override
                    public void handle(String time) { // 回调接口，获得选中的时间
                      end_time=time;
                        end.setText(time);
                    }
                }, "2010-01-01 00:00", now); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
                customDatePicker2.showSpecificTime(true); // 显示时和分
                customDatePicker2.setIsLoop(false); // 不允许循环滚动
                customDatePicker2.show(now);
            }
        });
    }



    public void setSpinner(){
        List<DeviceDb> deviceDbs=deviceDbDao.loadAll();
        final ArrayList<String> areas=new ArrayList<>();
        areas.add("all");
        final ArrayList<String> devices=new ArrayList<>();
        devices.add("all");

        for (int i=0;i<deviceDbs.size();i++){
            Log.i(TAG, "setSpinner: "+deviceDbs.get(i).getArea());
            Log.i(TAG, "setSpinner: "+deviceDbs.get(i).getDeviceNum());
            areas.add(deviceDbs.get(i).getArea());
            devices.add(deviceDbs.get(i).getDeviceNum());


        }
        // 建立数据源
        //final String[] mItems = {"a101","a102","a103","a104","a105","a106","a107","a108","a109"};
       // final String[] mItems1 = {"桥梁一区","桥梁二区","桥梁三区","隧道一区","隧道二区","隧道三区"};
// 建立Adapter并且绑定数据源

        ArrayAdapter<String> adapter1=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, areas);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//绑定 Adapter到控件

        areaSpinner.setAdapter(adapter1);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int postion, long id) {
                if (postion==0){
                    deviceNum="null";
                }
                else {
                    deviceNum=dl.get(postion);
                }




//                Toast.makeText(HistoryActivity.this, "你点击的是:"+dl.get(postion), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        areaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override

            public void onItemSelected(AdapterView<?> adapterView, View view, int postion, long id) {
                if (postion==0){
                    area="null";
                    String[] devicesList=devices.get(postion).split(",");

                    ArrayAdapter<String> adapter=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_item, devicesList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner .setAdapter(adapter);

                }
                if (postion>0){
                    area=areas.get(postion);
                    String[] devicesList=devices.get(postion).split(",");
                    dl=new ArrayList<String>();
                    dl.clear();
                    dl.add("all");
                    for (int i=0;i<devicesList.length;i++){
                        dl.add(devicesList[i]);
                    }
                    ArrayAdapter<String> adapter=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_item, dl);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner .setAdapter(adapter);


                }




                //Toast.makeText(HistoryActivity.this, "你点击的是:"+areas.get(postion), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });






    }




    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i("dayang","onCreateOptionsMenu");
        //创建Menu菜单
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.excel) {

            List<Sensor> sensors =sensorDao.loadAll();

            try {
                ExcelUtils.writeExcel(sensors);
                Toast.makeText(HistoryActivity.this, "写入成功！存至根目录下，名为SensorData.xls", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(HistoryActivity.this, "写入失败！", Toast.LENGTH_SHORT).show();

            }


        }

        if(id==R.id.clean){
            sensorDao.deleteAll();
            Toast.makeText(HistoryActivity.this, "清除成功！", Toast.LENGTH_SHORT).show();


        }
        return super.onOptionsItemSelected(item);

    }


    public void search(){

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLayoutwait.setVisibility(View.VISIBLE);



                page=1;
                Log.i(TAG, "run: "+Global.user.getUserName());
                RequestBody requestBody=new FormBody.Builder()
                        .add("userName", Global.user.getUserName())
                        .add("start_time",start_time)
                        .add("end_time",end_time)
                        .add("page",page+"")
                        .add("pageSize",pageSize+"")
                        .add("deviceNum",deviceNum)
                        .add("area",area).build();
                Log.i(TAG, "onClick: "+Global.user.getUserName()+start_time+end_time+page+pageSize+deviceNum+area);
                final Request request=new Request.Builder()
                        .url("http://120.78.209.11:8080/WebProject/historySearch")
                        .post(requestBody)
                        .build();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.i(TAG, "onFailure: "+e.toString());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                linearLayoutwait.setVisibility(View.GONE);
                                lstv.setResultSize(0);
                                adapter.notifyDataSetChanged();
                                Toast.makeText(HistoryActivity.this, "检查网络！", Toast.LENGTH_SHORT).show();


                            }
                        });

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String str=response.body().string();

                        Log.i(TAG, "onResponse: "+str);


                        historyDatas=gson.fromJson(str,new TypeToken<ArrayList<HistoryData>>() {}.getType());

                        List<Map<String, String>> result = new ArrayList<Map<String, String>>();
                        Map<String, String> data = null;
                        sensorDao.deleteAll();
                        for (int i = 0; i < historyDatas.size(); i++) {
                            data = new HashMap<String, String>();
                            data.put("area", historyDatas.get(i).getArea());
                            data.put("deviceNum", historyDatas.get(i).getDeviceNum());
                            data.put("upLimit", historyDatas.get(i).getUpLimit());
                            data.put("lowLimit" , historyDatas.get(i).getLowLimit());
                            data.put("value" , historyDatas.get(i).getValue());
                            data.put("dateTime" ,historyDatas.get(i).getDateTime());
                            Sensor sensor=new Sensor(null,historyDatas.get(i).getArea(),historyDatas.get(i).getDeviceNum(),historyDatas.get(i).getUpLimit(),historyDatas.get(i).getLowLimit(),historyDatas.get(i).getValue(),historyDatas.get(i).getDateTime());

                            sensorDao.insert(sensor);
                            result.add(data);
                        }

                        Message msg = handler.obtainMessage();
                        msg.what = 0;
                        msg.obj = result;
                        handler.sendMessage(msg);
                    }
                });

            }
        });



    }
}
