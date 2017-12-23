package newjohn.com.myapplication.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import newjohn.com.myapplication.GraphBean.ContentData;
import newjohn.com.myapplication.GraphBean.GraphData;
import newjohn.com.myapplication.MyApplication;
import newjohn.com.myapplication.R;
import newjohn.com.myapplication.bean.DeviceDb;
import newjohn.com.myapplication.bean.DeviceDbDao;
import newjohn.com.myapplication.global.Global;
import newjohn.com.myapplication.ui.CustomToast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChartActivity extends BaseActivity {
    private static final String TAG ="ChartActivity " ;
    @BindView(R.id.spinner4)
    Spinner spinner1;
    @BindView(R.id.spinner5)
    Spinner spinner2;
    @BindView(R.id.spinner6)
    Spinner spinner3;
    @BindView(R.id.search2)
    Button button;
    @BindView(R.id.linechart)
    LineChart lineChart;
    OkHttpClient okHttpClient;
    Gson gson;
    List<GraphData> graphDatas=new ArrayList<>();
    private ArrayList<String> list;
    private DeviceDbDao deviceDbDao;
    private ArrayAdapter<String> adapter;
    private String deviceNum1="null";
    private String deviceNum2="null";
    private String deviceNum3="null";
    float lowlimit=0;
    float highlimit=0;
    boolean isAlert=false;

    XAxis xAxis;
    YAxis yAxis;
    Legend l;
    Description description;

    ArrayList<Entry> values1 = new ArrayList<>();
    ArrayList<Entry> values2 = new ArrayList<>();

    ArrayList<Entry> values3 = new ArrayList<>();
    ArrayList<Entry> empty = new ArrayList<>();

    //LineDataSet每一个对象就是一条连接线
    LineDataSet set1;
    LineDataSet set2;
    LineDataSet set3;
    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
    LinearLayout waitL;
    SharedPreferences sharedPreferences;
    private boolean isFirst=false;
    LineData data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        waitL=findViewById(R.id.wait);
        ButterKnife.bind(this);
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        lowlimit=sharedPreferences.getFloat("min",10.0F);
        highlimit=sharedPreferences.getFloat("max",100.2F);
        deviceDbDao= MyApplication.getMyApplication().getDaoSession().getDeviceDbDao();
        okHttpClient=new OkHttpClient();
        gson=new Gson();
        setSpinner();
        setChart();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getdata();
                waitL.setVisibility(View.VISIBLE);
            }
        });

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
        adapter = new ArrayAdapter<String>(this,R.layout.simple_spinner_item,getSpinnerDataSource());
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




    public void getdata() {

        RequestBody requestBody = new FormBody.Builder()
                .add("userName", Global.user.getUserName())
                .add("deviceNum1", deviceNum1)
                .add("deviceNum2", deviceNum2)
                .add("deviceNum3", deviceNum3)
                .build();
        Log.i(TAG, "getdata: "+Global.user.getUserName()+deviceNum1+deviceNum2+deviceNum3);
        final Request request = new Request.Builder()
                .url(Constant.URL+"WebProject/getThrend")
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "onFailure: " + e.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        waitL.setVisibility(View.INVISIBLE);
                        Toast.makeText(ChartActivity.this,"刷新失败！检查网络",Toast.LENGTH_SHORT).show();



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


                        String[] t= (String[]) times.toArray(new String[0]);
                        Log.i(TAG, "run: t"+ t.length);
                        if (times.size()!=0){
                            //return mValues[(int) value % mValues.length];会报异常，除数为0；所以加判断
                            MyXFormatter formatter = new MyXFormatter(t);
                            xAxis.setValueFormatter(formatter);
                        }


                        if (data!=null){

                            if (data.contains(set1)){
                                data.removeDataSet(set1);
                                set1=null;


                            }
                            if (data.contains(set2)){
                                data.removeDataSet(set2);
                                set2=null;


                            }
                            if (data.contains(set3)){
                                data.removeDataSet(set3);
                                set3=null;


                            }

                        }




                        for (int k=0;k<graphDatas.size();k++){
                            Log.i(TAG, "run: area"+k+":"+graphDatas.get(k).getDeviceNum());
                            List<ContentData> contents=new ArrayList<ContentData>();
                            contents= graphDatas.get(k).getContent();
                            Collections.reverse(contents);
                            if (k==0) {
                                ArrayList<Entry> values1 = new ArrayList<>();


                                //XYSeries series=new XYSeries(graphDatas.get(k).getDeviceNum());
                                for (int i = 0; i < contents.size(); i++) {


                                    int j = times.indexOf(contents.get(i).getDateTime());
                                    float v=Float.parseFloat(contents.get(i).getValue());
                                    float v1=Float.valueOf(contents.get(i).getValue());
                                    Log.i(TAG, "run: " +v+v1+(0.0987F+i));
                                    Log.i(TAG, "run: sort "+k+":" +"排序号："+ j + "时间："+contents.get(i).getDateTime()+"值："+v);
                                    float v2=12.77777f;
                                    values1.add(new Entry( j,v));
                                    if(v>highlimit||v<lowlimit){
                                        isAlert=true;}


                                }
//                                if (lineChart.getData() != null &&
//                                        lineChart.getData().getDataSetCount() > 0&&set1!=null) {
//                                    set1 = (LineDataSet)lineChart.getData().getDataSetByIndex(0);
//                                    set1.setValues(values1);
//                                    set1.setLabel(graphDatas.get(k).getDeviceNum());
//
//
//                                   lineChart.getData().notifyDataChanged();
//                                    lineChart.notifyDataSetChanged();
//                                }
//                                else {
                                    set1=new LineDataSet(values1,graphDatas.get(k).getDeviceNum());
                                    set1.setColor(Color.YELLOW);
                                    set1.setCircleColor(Color.BLACK);
                                    set1.setLineWidth(5f);//设置线宽
                                    set1.setCircleRadius(5f);//设置焦点圆心的大小
                                    set1.enableDashedHighlightLine(10f, 5f, 0f);//点击后的高亮线的显示样式
                                    set1.setHighlightLineWidth(2f);//设置点击交点后显示高亮线宽
                                    set1.setHighlightEnabled(true);//是否禁用点击高亮线
                                    set1.setHighLightColor(Color.RED);//设置点击交点后显示交高亮线的颜色

                                    set1.setDrawFilled(false);//设置禁用范围背景填充
                               set1.setDrawValues(false);
//                                set1.setValueTextSize(9f);//设置显示值的文字大小
                                    dataSets.add(set1);
//                                }
                                if (isAlert){
                                    CustomToast.showToast(ChartActivity.this,graphDatas.get(k).getDeviceNum()+"超出限定值！");
                                isAlert=false;
                                }





                            }
                            if (k==1) {
                                ArrayList<Entry> values2 = new ArrayList<>();

                                //XYSeries series=new XYSeries(graphDatas.get(k).getDeviceNum());
                                for (int i = 0; i < contents.size(); i++) {


                                    int j = times.indexOf(contents.get(i).getDateTime());
                                    float v=Float.parseFloat(contents.get(i).getValue());
                                    Log.i(TAG, "run: sort "+k+":" +"排序号："+ j + "时间："+contents.get(i).getDateTime()+"值："+v);
                                    values2.add(new Entry(j,v));
                                    if(v>highlimit||v<lowlimit){
                                        isAlert=true;}


                                }

//                                if (lineChart.getData() != null &&
//                                        lineChart.getData().getDataSetCount() > 0&&set2!=null) {
//                                    Log.i(TAG, "run: set2 not new ");
//                                    set2 = (LineDataSet)lineChart.getData().getDataSetByIndex(1);
//                                    set2.setValues(values2);
//                                    set2.setLabel(graphDatas.get(k).getDeviceNum());
//
//
//                                    lineChart.getData().notifyDataChanged();
//                                    lineChart.notifyDataSetChanged();
//                                }
//                                else {
                                    Log.i(TAG, "run: set2 new ");
                                    set2=new LineDataSet(values2,graphDatas.get(k).getDeviceNum());
                                    set2.setColor(Color.RED);
                                    set2.setCircleColor(Color.GRAY);
                                    set2.setLineWidth(5f);
                                    set2.setCircleRadius(5f);
                                    set2.setDrawValues(false);
                                    set2.setValueTextSize(10f);
                                    dataSets.add(set2);

//                                }

                                if (isAlert){
                                    CustomToast.showToast(ChartActivity.this,graphDatas.get(k).getDeviceNum()+"超出限定值！");
                                    isAlert=false;
                                }



                            }
                            if (k==2) {
                                ArrayList<Entry> values3 = new ArrayList<>();

                                //XYSeries series=new XYSeries(graphDatas.get(k).getDeviceNum());
                                for (int i = 0; i < contents.size(); i++) {


                                    int j = times.indexOf(contents.get(i).getDateTime());
                                    float v=Float.parseFloat(contents.get(i).getValue());
                                    Log.i(TAG, "run: sort "+k+":" +"排序号："+ j + "时间："+contents.get(i).getDateTime()+"值："+v);
                                    values3.add(new Entry(j,v));
                                    if(v>highlimit||v<lowlimit){
                                        isAlert=true;}


                                }

//                                if (lineChart.getData() != null &&
//                                        lineChart.getData().getDataSetCount() > 0&&set3!=null) {
//                                    set3 = (LineDataSet)lineChart.getData().getDataSetByIndex(2);
//                                    set3.setValues(values3);
//                                    set3.setLabel(graphDatas.get(k).getDeviceNum());
//                                    lineChart.getData().notifyDataChanged();
//                                    lineChart.notifyDataSetChanged();
//                                }
//                                else {
                                    set3=new LineDataSet(values3,graphDatas.get(k).getDeviceNum());
                                    set3.setColor(Color.rgb(23,56,68));
                                    set3.setCircleColor(Color.CYAN);

                                    set3.setLineWidth(5f);
                                    set3.setCircleRadius(5f);
                                    set3.setDrawValues(false);
                                    set3.setValueTextSize(10f);
//

                                    dataSets.add(set3);
//                                }

                                if (isAlert){
                                    CustomToast.showToast(ChartActivity.this,graphDatas.get(k).getDeviceNum()+"超出限定值！");
                                    isAlert=false;
                                }



                            }




                        }
                        if (data==null){
                            data = new LineData(dataSets);


                        }
                        else {

                            lineChart.getData().notifyDataChanged();
                        }

                        IMarker marker = new MyMarkerView(getApplicationContext(),R.layout.mark_view);
                        lineChart.setMarker(marker);


                        lineChart.setData(data);
                        lineChart.notifyDataSetChanged();
                        lineChart.invalidate();









                    Toast.makeText(ChartActivity.this,"刷新成功！",Toast.LENGTH_SHORT).show();
                        waitL.setVisibility(View.INVISIBLE);

                    }


                });
            }


        });
    }


    public void setChart(){

        //创建描述信息
        description =new Description();
        description.setText("压力变化曲线（KPA）");
        description.setTextColor(Color.RED);
        description.setTextSize(20);
        lineChart.setDescription(description);//设置图表描述信息
        lineChart.setNoDataText("没有数据熬");//没有数据时显示的文字
        lineChart.setNoDataTextColor(Color.BLUE);//没有数据时显示文字的颜色
        lineChart.setDrawGridBackground(false);//chart 绘图区后面的背景矩形将绘制
        lineChart.setDrawBorders(false);//禁止绘制图表边框的线
        //lineChart.setBorderColor(); //设置 chart 边框线的颜色。
        //lineChart.setBorderWidth(); //设置 chart 边界线的宽度，单位 dp。
        //lineChart.setLogEnabled(true);//打印日志
        //lineChart.notifyDataSetChanged();//刷新数据
        //lineChart.invalidate();//重绘
        // enable touch gestures
        lineChart.setTouchEnabled(true); // 设置是否可以触摸

        lineChart.setTouchEnabled(true); // 设置是否可以触摸
        lineChart.setDragEnabled(true);// 是否可以拖拽
        lineChart.setScaleEnabled(true);// 是否可以缩放 x和y轴, 默认是true
        lineChart.setScaleXEnabled(true); //是否可以缩放 仅x轴
        lineChart.setScaleYEnabled(true); //是否可以缩放 仅y轴
        lineChart.setPinchZoom(true);  //设置x轴和y轴能否同时缩放。默认是否
        lineChart.setDoubleTapToZoomEnabled(true);//设置是否可以通过双击屏幕放大图表。默认是true
        lineChart.setHighlightPerDragEnabled(true);//能否拖拽高亮线(数据点与坐标的提示线)，默认是true
        lineChart.setDragDecelerationEnabled(true);//拖拽滚动时，手放开是否会持续滚动，默认是true（false是拖到哪是哪，true拖拽之后还会有缓冲）
        lineChart.setDragDecelerationFrictionCoef(0.99f);//与上面那个属性配合，持续滚动时的速度快慢，[0,1) 0代表立即停止。

        //获取此图表的x轴
        xAxis = lineChart.getXAxis();
        xAxis.setEnabled(true);//设置轴启用或禁用 如果禁用以下的设置全部不生效
        xAxis.setDrawAxisLine(true);//是否绘制轴线
        xAxis.setDrawGridLines(true);//设置x轴上每个点对应的线
        xAxis.setDrawLabels(true);//绘制标签  指x轴上的对应数值
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置x轴的显示位置
        xAxis.setLabelCount(10);
        //xAxis.setTextSize(20f);//设置字体
        //xAxis.setTextColor(Color.BLACK);//设置字体颜色
        //设置竖线的显示样式为虚线
        //lineLength控制虚线段的长度
        //spaceLength控制线之间的空间
      //  xAxis.enableGridDashedLine(10f, 10f, 0f);
//       xAxis.setAxisMaximum(20f);//设置最大值
//        xAxis.setAxisMinimum(10f);
        xAxis.setAvoidFirstLastClipping(true);//图表将避免第一个和最后一个标签条目被减掉在图表或屏幕的边缘
        xAxis.setLabelRotationAngle(10f);//设置x轴标签的旋转角度
//        设置x轴显示标签数量  还有一个重载方法第二个参数为布尔值强制设置数量 如果启用会导致绘制点出现偏差
      xAxis.setLabelCount(10);

//        xAxis.setTextColor(Color.BLUE);//设置轴标签的颜色
//        xAxis.setTextSize(24f);//设置轴标签的大小
//        xAxis.setGridLineWidth(10f);//设置竖线大小
//        xAxis.setGridColor(Color.RED);//设置竖线颜色
//        xAxis.setAxisLineColor(Color.GREEN);//设置x轴线颜色
//        xAxis.setAxisLineWidth(5f);//设置x轴线宽度
//        xAxis.setValueFormatter();//格式化x轴标签显示字符

        LimitLine ll1 = new LimitLine(highlimit, "Upper Limit");
        ll1.setLineWidth(2f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);


        LimitLine ll2 = new LimitLine(lowlimit, "Lower Limit");
        ll2.setLineWidth(2f);
        ll2.enableDashedLine(10f, 10f, 0f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll2.setTextSize(10f);

/**
 * Y轴默认显示左右两个轴线
 */
        //获取右边的轴线
        YAxis rightAxis=lineChart.getAxisRight();
        //设置图表右边的y轴禁用
        rightAxis.setEnabled(false);
        //获取左边的轴线
        YAxis leftAxis = lineChart.getAxisLeft();
        //设置网格线为虚线效果
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        //是否绘制0所在的网格线
        leftAxis.setDrawZeroLine(false);
        leftAxis.setAxisMaximum(150);
        leftAxis.setAxisMinimum(-100);
        leftAxis.setLabelCount(25);
        leftAxis.addLimitLine(ll1);
        leftAxis.addLimitLine(ll2);


        l = lineChart.getLegend();//图例
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_INSIDE);//设置图例的位置
        l.setTextSize(10f);//设置文字大小
        l.setForm(Legend.LegendForm.CIRCLE);//正方形，圆形或线
        l.setFormSize(10f); // 设置Form的大小
        l.setWordWrapEnabled(true);//是否支持自动换行 目前只支持BelowChartLeft, BelowChartRight, BelowChartCenter
        l.setFormLineWidth(10f);//设置Form的宽度


    }







    public class MyXFormatter  implements IAxisValueFormatter {

        private String[] mValues;

        public MyXFormatter(String[] values) {
            this.mValues = values;
        }
        private static final String TAG = "MyXFormatter";

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            // "value" represents the position of the label on the axis (x or y)
            Log.d(TAG, "----->getFormattedValue: "+value);
            return mValues[(int) value % mValues.length];
        }
    }




    public class MyMarkerView extends MarkerView {

        private TextView tvContent;

        public MyMarkerView(Context context, int layoutResource) {
            super(context, layoutResource);

            // find your layout components
            tvContent = (TextView) findViewById(R.id.tvContent);
        }

        // callbacks everytime the MarkerView is redrawn, can be used to update the
        // content (user-interface)
        @Override
        public void refreshContent(Entry e, Highlight highlight) {

            tvContent.setText("" + e.getY());

            // this will perform necessary layouting
            super.refreshContent(e, highlight);
        }

        private MPPointF mOffset;

        @Override
        public MPPointF getOffset() {

            if(mOffset == null) {
                // center the marker horizontally and vertically
                mOffset = new MPPointF(-(getWidth() / 2), -getHeight());
            }

            return mOffset;
        }
    }

}
