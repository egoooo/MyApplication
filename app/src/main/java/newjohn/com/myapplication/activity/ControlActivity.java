package newjohn.com.myapplication.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import newjohn.com.myapplication.R;
import newjohn.com.myapplication.bean.DeviceDb;
import newjohn.com.myapplication.bean.DeviceInfo;
import newjohn.com.myapplication.global.Global;
import newjohn.com.myapplication.ui.SelfDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ControlActivity extends BaseActivity{

    private static final String TAG ="ControlActivity " ;
    RelativeLayout relativeLayout_alert;
    RelativeLayout relativeLayout_clear;
    private SelfDialog selfDialog;

    @BindView(R.id.kaiguan)
    Switch aSwitch;
//    @BindView(R.id.clear)
//    Switch cSwitch;
    OkHttpClient okHttpClient;
    String max;
    String min;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        ButterKnife.bind(this);
        okHttpClient=new OkHttpClient();
        relativeLayout_alert= (RelativeLayout) findViewById(R.id.alert);
        relativeLayout_clear= (RelativeLayout) findViewById(R.id.clearlayout);
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        editor=sharedPreferences.edit();

        relativeLayout_alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selfDialog = new SelfDialog(ControlActivity.this);
                selfDialog.setTitle("报警值");
                selfDialog.setMessage("请设置报警值：");
                selfDialog.setYesOnclickListener("确定",new SelfDialog.onYesOnclickListener() {
                    @Override
                    public void onYesClick() {
//                        Toast.makeText(ControlActivity.this,selfDialog.getValueMax()+selfDialog.getValueMin(),Toast.LENGTH_LONG).show();
                        try {
                            float max=Float.parseFloat(selfDialog.getValueMax());
                            float min=Float.parseFloat(selfDialog.getValueMin());
                            editor.putFloat("max",max);
                            editor.putFloat("min",min);
                            editor.commit();
                            settingAlertValue(selfDialog.getValueMax(),selfDialog.getValueMin());
                            selfDialog.dismiss();
                        }
                        catch (NumberFormatException e){
                            Log.i(TAG, "onYesClick: "+e.toString());
                            Toast.makeText(ControlActivity.this,"请正确输入float型数！",Toast.LENGTH_LONG).show();


                        }



                    }
                });
                selfDialog.setNoOnclickListener("取消", new SelfDialog.onNoOnclickListener() {
                    @Override
                    public void onNoClick() {
                        Toast.makeText(ControlActivity.this,"点击了--取消--按钮",Toast.LENGTH_LONG).show();
                        selfDialog.dismiss();
                    }
                });
                selfDialog.show();
            }
        });



        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    Toast.makeText(ControlActivity.this,"open",Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(ControlActivity.this,"close",Toast.LENGTH_LONG).show();
                }

            }
        });

        relativeLayout_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ControlActivity.this,ClearActivity.class));
            }
        });

//        cSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
//                if (isChecked){
//                    Toast.makeText(ControlActivity.this,"open",Toast.LENGTH_LONG).show();
//                }
//                else {
//                    Toast.makeText(ControlActivity.this,"close",Toast.LENGTH_LONG).show();
//                }
//
//            }
//        });
   }



    public void settingAlertValue(String max,String min ){


        RequestBody requestBody=new FormBody.Builder()
                .add("userName", Global.user.getUserName())
                .add("upAlarmValue", max)
                .add("lowAlarmValue",min)
                .build();
        final Request request=new Request.Builder()
                .url(Constant.URL+"WebProject/setAlarmValue")
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "onFailure: "+e.toString());
                Toast.makeText(ControlActivity.this,"检查网络！",Toast.LENGTH_SHORT).show();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {







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

                        Toast.makeText(ControlActivity.this,"成功上传至服务器！",Toast.LENGTH_SHORT).show();




                    }
                });

            }
        });

    }

    public void control_close(){

    }

    public void control_clear(){

    }
}
