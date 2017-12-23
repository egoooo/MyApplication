package newjohn.com.myapplication.activity;

import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import newjohn.com.myapplication.MyApplication;
import newjohn.com.myapplication.R;
import newjohn.com.myapplication.adapter.MyGridAdapter;
import newjohn.com.myapplication.bean.DeviceDb;
import newjohn.com.myapplication.bean.DeviceDbDao;
import newjohn.com.myapplication.bean.DeviceInfo;
import newjohn.com.myapplication.dynamicLineChart.ChartTestActivity;
import newjohn.com.myapplication.global.Global;
import newjohn.com.myapplication.serv.PersistentConnectionService;
import newjohn.com.myapplication.serv.WebSocketService;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends BaseActivity {
    @BindView(R.id.gridview)
    GridView gridView;
    MyGridAdapter myGridAdapter;
    OkHttpClient okHttpClient;
    List<DeviceInfo> deviceinfos;
    DeviceDbDao deviceDbDao;

    private int[] icon = { R.drawable.yuancheng, R.drawable.lvbo,R.drawable.zaixian,
            R.drawable.lishi, R.drawable.qushixian, R.drawable.baojing,
            R.drawable.xiazai, R.drawable.shezhi };
    private String[] iconName = { "远程控制","滤波" ,"在线查询", "历史记录", "趋势线", "预警报警", "数据下载", "设置",
            };
    private List<Map<String, Object>> data_list;
    private String TAG="MainActivity";
    Gson gson=new Gson();
    private Intent mIntent;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate: is first"+Global.isfirst);
        toolbar=findViewById(R.id.main_toorbar);
        setSupportActionBar(toolbar);
        if (Global.isfirst){
            //启动服务
            mIntent = new Intent(this, WebSocketService.class);
            startService(mIntent);
            Global.isfirst=false;

        }
        deviceDbDao= MyApplication.getMyApplication().getDaoSession().getDeviceDbDao();

        okHttpClient=new OkHttpClient();
        ButterKnife.bind(this);
        //新建List
        data_list = new ArrayList<Map<String, Object>>();
        //获取数据
        getData();
        getInfo();
        myGridAdapter=new MyGridAdapter(MainActivity.this,data_list);
        gridView.setAdapter(myGridAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        Toast.makeText(getApplicationContext(),i+"",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this,ControlActivity.class));
                        break;
                    case 1:
                        Toast.makeText(getApplicationContext(),i+"",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this,ChartTestActivity.class));
                        break;
                    case 2:
                        Toast.makeText(getApplicationContext(),i+"",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this,OnlineSearchActivity.class));
                        break;
                    case 3:
                        Toast.makeText(getApplicationContext(),i+"",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this,HistoryActivity.class));
                        break;
                    case 4:
                        Toast.makeText(getApplicationContext(),i+"",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this,ChartActivity.class));
                        break;
                    case 5:
                        Toast.makeText(getApplicationContext(),i+"",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this,AlertActivity .class));

                        break;
                    case 6:
                        Toast.makeText(getApplicationContext(),i+"",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this,DownloadActivity.class));
                        break;
                    case 7:
                        Toast.makeText(getApplicationContext(),i+"",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this,SetActivity.class));
                        break;
                }
            }
        });





    }

    public List<Map<String, Object>> getData(){
        //cion和iconName的长度是相同的，这里任选其一都可以
        for(int i=0;i<icon.length;i++){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image", icon[i]);
            map.put("text", iconName[i]);
            data_list.add(map);
        }

        return data_list;
    }



    public void getInfo(){

        RequestBody requestBody=new FormBody.Builder()
                .add("userName", Global.user.getUserName()).build();
        final Request request=new Request.Builder()
                .url(Constant.URL+"WebProject/getLocation")
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "onFailure: "+e.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getInfo();



                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String str=response.body().string();

                Log.i(TAG, "onResponse: "+str);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        deviceinfos=gson.fromJson(str,new TypeToken<ArrayList<DeviceInfo>>() {}.getType());
                       Global.deviceInfos= (ArrayList<DeviceInfo>) deviceinfos;
                      //  Log.i(TAG, "run: "+Global.deviceInfos.get(0).getDeviceNum());
                        deviceDbDao.deleteAll();
                        for (int i=0;i<deviceinfos.size();i++){
                            deviceDbDao.insert(new DeviceDb(null,deviceinfos.get(i).getArea(),deviceinfos.get(i).getDeviceNum()));
                        }



                    }
                });

            }
        });

    }


    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy: Mian"+"kkk");
        //停止服务
        stopService(mIntent);

        super.onDestroy();
    }


    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("退出");
        builder.setMessage("是否要退出程序？");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();


            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                //移除标记为id的通知 (只是针对当前Context下的所有Notification)
//                notificationManager.cancel(1);
//                //移除所有通知
                notificationManager.cancelAll();
                Global.isfirst=true;
                AtyContainer.getInstance().finishAllActivity();

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();



    }

    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG,"onCreateOptionsMenu");
        //创建Menu菜单
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.restartService:
                mIntent = new Intent(this, WebSocketService.class);
                startService(mIntent);
                Global.isfirst=false;


        }
        return super.onOptionsItemSelected(item);
    }
}
