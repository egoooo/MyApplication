package newjohn.com.myapplication.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import newjohn.com.myapplication.GraphBean.ContentData;
import newjohn.com.myapplication.GraphBean.GraphData;
import newjohn.com.myapplication.MyApplication;
import newjohn.com.myapplication.R;
import newjohn.com.myapplication.bean.DeviceDb;
import newjohn.com.myapplication.bean.DeviceDbDao;
import newjohn.com.myapplication.bean.DeviceInfo;
import newjohn.com.myapplication.global.Global;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GraphActivity extends AppCompatActivity {

    private static final String TAG ="GraphActivity " ;
    private XYMultipleSeriesDataset dataset;
    private XYSeries series;
    private XYMultipleSeriesRenderer render;
    private int xLenght=20;
    private int addY;
    private String addX;
    LinearLayout linearLayout;
    private GraphicalView chart;
    private Handler handle;
    private TimerTask task;
    private Timer timer=new Timer();
    private XYSeries series1;
    private XYSeries series2;
    private int addY1;
    private String addX1;


    OkHttpClient okHttpClient;
    Gson gson;
    List<GraphData> graphDatas=new ArrayList<>();
    GraphData graphData1=new GraphData();
    GraphData graphData2=new GraphData();
    GraphData graphData3=new GraphData();

    @BindView(R.id.spinner1)
    Spinner spinner1;
    @BindView(R.id.spinner2)
    Spinner spinner2;
    @BindView(R.id.spinner3)
    Spinner spinner3;
    @BindView(R.id.search1)
    Button button;
    private ArrayList<String> list;
    private DeviceDbDao deviceDbDao;
    private ArrayAdapter<String> adapter;
    private String deviceNum1="null";
    private String deviceNum2="null";
    private String deviceNum3="null";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        ButterKnife.bind(this);
        deviceDbDao= MyApplication.getMyApplication().getDaoSession().getDeviceDbDao();
        okHttpClient=new OkHttpClient();
        gson=new Gson();

        linearLayout=findViewById(R.id.fullScreen);


        chart = ChartFactory.getLineChartView(this, getdemodataset(),
                getdemorenderer());
        // 先remove再add可以实现统计图更新
        linearLayout.removeAllViews();
        linearLayout.addView(chart, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

     setSpinner();

button.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        getdata();
    }
});

    }


    private XYMultipleSeriesDataset getdemodataset() {
        dataset = new XYMultipleSeriesDataset();// xy轴数据源
        series = new XYSeries("设备1");
        series1=new XYSeries("设备2");
        series2=new XYSeries("设备3");
        dataset.addSeries(series);
        dataset.addSeries(series1);
        dataset.addSeries(series2);
        return dataset;
    }

    XYMultipleSeriesRenderer getdemorenderer() {
        // 定义每条线的点的样式
        render = new XYMultipleSeriesRenderer();
        XYSeriesRenderer r = new XYSeriesRenderer();
        r.setColor(Color.parseColor("#04f604"));// 折线的颜色
        r.setPointStyle(PointStyle.CIRCLE);// 折线点的样式
        r.setDisplayChartValues(true);// 设置显示折线的点对应的值
        r.setFillPoints(true);
        r.setChartValuesSpacing(3);

       // r.setFillBelowLine(false);// 设置折线下方是否填充
       // r.setFillBelowLineColor(Color.parseColor("#04f604"));// 设置填充色
        r.setLineWidth(3f);// 设置折线的宽度


        XYSeriesRenderer r1 = new XYSeriesRenderer();
        r1.setColor(Color.parseColor("#FFD700"));// 折线的颜色
        r1.setPointStyle(PointStyle.SQUARE);// 折线点的样式
        r1.setDisplayChartValues(true);// 设置显示折线的点对应的值
        r1.setFillPoints(true);
        r1.setChartValuesSpacing(3);

        // r.setFillBelowLine(false);// 设置折线下方是否填充
        // r.setFillBelowLineColor(Color.parseColor("#04f604"));// 设置填充色
        r1.setLineWidth(3f);// 设置折线的宽度


        XYSeriesRenderer r2 = new XYSeriesRenderer();
        r2.setColor(Color.parseColor("#FF0000"));// 折线的颜色
        r2.setPointStyle(PointStyle.DIAMOND);// 折线点的样式
        r2.setDisplayChartValues(true);// 设置显示折线的点对应的值
        r2.setFillPoints(true);
        r2.setChartValuesSpacing(3);

        // r.setFillBelowLine(false);// 设置折线下方是否填充
        // r.setFillBelowLineColor(Color.parseColor("#04f604"));// 设置填充色
        r2.setLineWidth(3f);// 设置折线的宽度


        render.addSeriesRenderer(r);
        render.addSeriesRenderer(r1);
        render.addSeriesRenderer(r2);
        render.setBackgroundColor(Color.BLUE);
        render.setApplyBackgroundColor(true);

        render.setChartTitle("地质沉降监控");// 图表的标题
        render.setChartTitleTextSize(20);// 设置整个图表标题文字的大小
        render.setAxisTitleTextSize(20);// 设置轴标题文字的大小
        render.setAxesColor(Color.BLACK);
        render.setXTitle("当前时间");
        render.setYTitle("压力大小");
        render.setLabelsTextSize(20);// 设置轴刻度文字的大小
        render.setLabelsColor(Color.WHITE);
        render.setXLabelsColor(Color.RED);
        render.setYLabelsColor(0, Color.RED);
        render.setLegendTextSize(20);// 设置图例文字大小
       // render.setShowLegend(false);//显示不显示在这里设置，非常完美
        render.setYLabelsAlign(Paint.Align.RIGHT);// 刻度值相对于刻度的位置
        render.setShowGrid(true);// 显示网格
        render.setShowCustomTextGrid(true);// 设置是否显示X轴网格
        render.setGridColor(Color.BLACK);
        render.setYAxisMax(150);// 设置y轴的范围
        render.setYAxisMin(80);
        render.setXAxisMax(10);
        render.setYLabels(10);// 分几等份
        render.setInScroll(true);
        render.setLabelsTextSize(16);
        render.setLabelsColor(Color.WHITE);
       // render.setPanEnabled(false, false);// 禁止报表的拖动
        render.setPointSize(10f);// 设置点的大小(图上显示的点的大小和图例中点的大小都会被设置)

        render.setMargins(new int[] { 20, 30, 90, 10 }); // 设置图形四周的留白
        render.setXLabels(0);// 取消X坐标的数字,只有自己定义横坐标是才设为此值


        return render;
    }


//    private int addXx = -1;
    /*
    * 更新折线图
//     */
//    private void updatechart()  {
////        /**
////         *Date（or String）转化为时间戳，java中Date类中的getTime()是获取时间戳的，
////         * java中生成的时间戳精确到毫秒级别，而unix中精确到秒级别，所以通过java生成的时间戳需要除以1000。
////         */
////        SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
////        String time="1970-01-06 11:45:55";
////        String time1="1970-01-06 11:45:56";
////        Date date1 = null;Date date2=null;
////        try {
////            date1 = format.parse(time);
////            date2= format.parse(time1);
////            Log.i(TAG, "Format To times:"+date1.getTime()/1000);
////            Log.i(TAG, "Format To times:"+date2.getTime()/1000);
////        } catch (ParseException e) {
////            e.printStackTrace();
////        }
//
//
//
//        // 获取当前时间
//        String nowTime="";
//        Date date=new Date();
//        long datelong=date.getTime()/1000;
//
//
//        addX = date.getHours()+":"+date.getMinutes()+":"+date.getSeconds();
//        java.util.Random random=new java.util.Random();// 定义随机类
//        int result=random.nextInt(5)+1;// 返回[0,10)集合中的整数，注意不包括10
//        addX1 = date.getHours()+":"+date.getMinutes()+":"+date.getSeconds();
//        // 用随机数模拟数据
//        addY = (int) (Math.random() * 10);
//        addY1=(int) (Math.random() * 10);
//
//        addXx++;
//        //每一个新点坐标都后移一位
//        series.add(addXx ,addY);
//        series.addAnnotation("hahah",addXx ,addY);
//        series1.add(addXx,addY1);
//        series1.addAnnotation("khhhhh",addXx,addY1);
//
//        //最重要的一句话，以xy对的方式往里放值
//        render.addXTextLabel(addXx,addX);
//        render.setLabelsTextSize(20);
//
//        if(addXx>20){//如果超出了屏幕边界,实现坐标轴自动移动的方法
//            render.setXAxisMin(addXx-20);//显示范围为20
//            render.setXAxisMax(addXx);
//        }
//
//
//
//        // 移除数据集中旧的点集
//        dataset.removeSeries(series);
//        dataset.removeSeries(series1);
//
//        dataset.addSeries(series);
//        dataset.addSeries(series1);
//        // 视图更新，没有这一步，曲线不会呈现动态
//        chart.invalidate();
//    }
//
//
//    /**
//     * 通过发送消息结合计时器实现更新
//     */
//    private void sendMessage() {
//        handle = new Handler() {
//            public void handleMessage(Message msg) {
//                updatechart();
//                Log.i(TAG, "handleMessage:");
//
//
//            }
//
//        };
//        task = new TimerTask() {
//            public void run() {
//                Message msg = new Message();
//                msg.what = 200;
//                handle.sendMessage(msg);
//            }
//        };
//        timer.schedule(task, 0, 1000);
//    }
//


    public void getdata() {

        RequestBody requestBody = new FormBody.Builder()
                .add("userName", Global.user.getUserName())
                .add("deviceNum1", deviceNum1)
                .add("deviceNum2", deviceNum2)
                .add("deviceNum3", deviceNum3)
                .build();
        Log.i(TAG, "getdata: "+Global.user.getUserName()+deviceNum1+deviceNum2+deviceNum3);
        final Request request = new Request.Builder()
                .url("http://120.78.209.11:8080/WebProject/getThrend")
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "onFailure: " + e.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(GraphActivity.this,"刷新失败！检查网络",Toast.LENGTH_SHORT).show();



                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String str = response.body().string();

                Log.i(TAG, "onResponse: " + str);

                graphDatas = gson.fromJson(str, new TypeToken<ArrayList<GraphData>>() {
                }.getType());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        graphData1 = graphDatas.get(0);
                        //graphData2=graphDatas.get(1);
                        //graphData3=graphDatas.get(2);

//                        SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//      Date date1 = null;Date date2=null;Date date3=null;
//      try {
//          Log.i(TAG, "run: size "+graphData1.getContent().size());
//          date1 = format.parse(graphData1.getContent().get(graphData1.getContent().size()-1).getDateTime());
//         // date2= format.parse(graphData2.getContent().get(0).getTime());
//         // date3= format.parse(graphData3.getContent().get(0).getTime());
//          long a=(long) date1.getTime()/1000/60;
//         // long b=date1.getTime()/1000;
//         // long c=date1.getTime()/1000;
//          Log.i(TAG, "Format To times:"+date1.getTime()/1000/60);
//          // Log.i(TAG, "Format To times:"+date2.getTime()/1000);
//          //Log.i(TAG, "Format To times:"+date3.getTime()/1000);
//          //long e=Math.min(c, Math.min(a, b));
//          //graphData1.getContent().get(graphData1.getContent().size()-1).getDateTime();


                        //List<ContentData> contents = new ArrayList<>();
                        List<String> times = new ArrayList<String>();
                        for (int i = 0; i < graphDatas.size(); i++) {
                            for (int j = 0; j < graphDatas.get(i).getContent().size(); j++) {
                                String time = graphDatas.get(i).getContent().get(j).getDateTime();
                                if (!times.contains(time)) {
                                    times.add(time);
                                }

                            }
                        }
                        Collections.sort(times);
                        Log.i(TAG, "run: times "+times.toString());
                        Log.i(TAG, "run: graphDatas.size() "+graphDatas.size());



                        for (int k=0;k<graphDatas.size();k++){
                            Log.i(TAG, "run: area"+k+":"+graphDatas.get(k).getDeviceNum());
                           List<ContentData> contents=new ArrayList<ContentData>();
                                   contents= graphDatas.get(k).getContent();
                            Collections.reverse(contents);
                           //XYSeries series=new XYSeries(graphDatas.get(k).getDeviceNum());
                            for (int i = 0; i < contents.size(); i++) {

                                if (k==0){
                                    int j=times.indexOf(contents.get(i).getDateTime());
                                    Log.i(TAG, "run: sort k==0" +j+"-");

                                    series.setTitle(graphDatas.get(k).getDeviceNum());
                                    series.add(j, Double.parseDouble(contents.get(i).getValue()));
                                    //series.add(j, 90);

                                    render.addXTextLabel(j, contents.get(i).getDateTime());
                                    dataset.removeSeries(series);
                                    dataset.removeSeries(series1);
                                    dataset.removeSeries(series2);
                                    dataset.addSeries(series);
                                }
                                if (k==1){
                                    int j=times.indexOf(contents.get(i).getDateTime());
                                    Log.i(TAG, "run: sort k==1" +j+"-");

                                    series1.setTitle(graphDatas.get(k).getDeviceNum());
                                    series1.add(j, Double.parseDouble(contents.get(i).getValue()));
                                    //series.add(j, 90);

                                    render.addXTextLabel(j, contents.get(i).getDateTime());

                                    dataset.removeSeries(series1);
                                    dataset.removeSeries(series2);
                                    dataset.addSeries(series1);

                                }
                                if (k==2){
                                    int j=times.indexOf(contents.get(i).getDateTime());
                                    Log.i(TAG, "run: sort k==2" +j+"-");

                                    series2.setTitle(graphDatas.get(k).getDeviceNum());
                                    series2.add(j, Double.parseDouble(contents.get(i).getValue()));
                                    //series.add(j, 90);

                                    render.addXTextLabel(j, contents.get(i).getDateTime());
                                    dataset.removeSeries(series2);
                                    dataset.addSeries(series2);

                                }

                            }

                        }







//                        dataset.removeSeries(series1);
//
//
//
//                        dataset.addSeries(series1);
                        chart.invalidate();
                        Toast.makeText(GraphActivity.this,"刷新成功！",Toast.LENGTH_SHORT).show();

                    }


                });
            }


        });
    }



    public long StringTimeToLong(String time){

        SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long datelong=0;
        Date date=null;
        try {

            date = format.parse(time);
            datelong=date.getTime()/1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        finally {
            return datelong;
        }

    }


    public List<String> getSpinnerDataSource(){
        List<DeviceDb> dls=deviceDbDao.loadAll();


        list=new ArrayList<>();
        list.add("无");
        for (int i=0;i<dls.size();i++){
            String dvs=dls.get(i).getDeviceNum();
            String[] devicesList=dvs.split(",");
            Log.i(TAG, "getSpinnerDataSource: "+devicesList.length+dvs);
            for (int k=0;k<devicesList.length;k++){
                if (!list.contains(devicesList[k])){
                    list.add(devicesList[k]);
                }
            }
        }
        Log.i(TAG, "getSpinnerDataSource:list "+list.toString());

        return list;
    }

    public void setSpinner(){
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,getSpinnerDataSource());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position==0){
                    deviceNum1="null";

                }
                if (position>0){
                    deviceNum1=list.get(position);


                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinner2.setAdapter(adapter);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position==0){
                    deviceNum2="null";

                }
                if (position>0){
                    deviceNum2=list.get(position);


                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinner3.setAdapter(adapter);
        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position==0){
                    deviceNum3="null";

                }
                if (position>0){
                    deviceNum3=list.get(position);


                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
