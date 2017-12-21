package newjohn.com.myapplication.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import newjohn.com.myapplication.MyApplication;
import newjohn.com.myapplication.R;
import newjohn.com.myapplication.bean.DeviceDb;
import newjohn.com.myapplication.bean.DeviceDbDao;
import newjohn.com.myapplication.global.Global;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ClearActivity extends BaseActivity {

    private static final String TAG ="ClearActivity" ;
    DeviceDbDao deviceDbDao;
    List<DeviceDb> data;
    ListView listview;
    Toolbar toolbar;
    OkHttpClient okHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clear);
        deviceDbDao= MyApplication.getMyApplication().getDaoSession().getDeviceDbDao();
        okHttpClient=new OkHttpClient();
        data=deviceDbDao.loadAll();
        listview=findViewById(R.id.listview_c);
        toolbar=findViewById(R.id.toolbar_c);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        MyAdapter adapter = new MyAdapter(this);
        listview.setAdapter(adapter);

    }


    //ViewHolder静态类
    static class ViewHolder
    {

        public TextView area;
        public Button button;
    }

    public class MyAdapter extends BaseAdapter
    {
        private LayoutInflater mInflater = null;
        private MyAdapter(Context context)
        {
            //根据context上下文加载布局，这里的是Demo17Activity本身，即this
            this.mInflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            //How many items are in the data set represented by this Adapter.
            //在此适配器中所代表的数据集中的条目数
            return data.size();
        }
        @Override
        public Object getItem(int position) {
            // Get the data item associated with the specified position in the data set.
            //获取数据集中与指定索引对应的数据项
            return data.get(position);
        }
        @Override
        public long getItemId(int position) {
            //Get the row id associated with the specified position in the list.
            //获取在列表中与指定索引对应的行id
            return position;
        }

        //Get a View that displays the data at the specified position in the data set.
        //获取一个在数据集中指定索引的视图来显示数据
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            //如果缓存convertView为空，则需要创建View
            if(convertView == null)
            {
                holder = new ViewHolder();
                //根据自定义的Item布局加载布局
                convertView = mInflater.inflate(R.layout.list_item_clear, null);

                holder.area = (TextView)convertView.findViewById(R.id.area);
                holder.button = convertView.findViewById(R.id.clear);
                //将设置好的布局保存到缓存中，并将其设置在Tag里，以便后面方便取出Tag
                convertView.setTag(holder);
            }else
            {
                holder = (ViewHolder)convertView.getTag();
            }

            holder.area.setText((String)data.get(position).getArea());
            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(ClearActivity.this, "清零"+data.get(position).getArea(), Toast.LENGTH_SHORT).show();
                    clear(data.get(position).getArea());
                }
            });

            return convertView;
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    public void clear(String area){


        RequestBody requestBody=new FormBody.Builder()
                .add("userName", Global.user.getUserName())
                .add("area",area)
                .add("control","clear")
                .build();
        final Request request=new Request.Builder()
                .url(Constant.URL+"WebProject/getControlServer")
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "onFailure: "+e.toString());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {



                        Toast.makeText(ClearActivity.this,"检查网络！",Toast.LENGTH_SHORT).show();



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

                        Toast.makeText(ClearActivity.this,str,Toast.LENGTH_SHORT).show();




                    }
                });

            }
        });

    }
}
