package newjohn.com.myapplication.activity;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import newjohn.com.myapplication.R;
import newjohn.com.myapplication.ui.SelfDialog;
import newjohn.com.myapplication.ui.SelfDialogOne;

public class SetActivity  extends BaseActivity {
    private static final String TAG ="SetActivity" ;
    public RelativeLayout heartLayout;
    public RelativeLayout addressLayout;
    public Toolbar toolbar;
    public SelfDialogOne selfDialog_heart;
    public SelfDialogOne selfDialog_url;
    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        toolbar=findViewById(R.id.toolbar_s);
        heartLayout=findViewById(R.id.heart);
        addressLayout=findViewById(R.id.address);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        editor=sharedPreferences.edit();
        heartLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selfDialog_heart = new SelfDialogOne(SetActivity.this);
                selfDialog_heart.setTitle("心跳周期");
                selfDialog_heart.setMessage("请设置心跳周期：");
                selfDialog_heart.setHint("心跳值，单位秒");
                selfDialog_heart.setYesOnclickListener("确定",new SelfDialogOne.onYesOnclickListener() {
                    @Override
                    public void onYesClick() {
                        try {
                            int value=Integer.parseInt(selfDialog_heart.getValue());
                            editor.putInt("heartBeat",value);
                            editor.commit();
                            selfDialog_heart.dismiss();
                            Toast.makeText(SetActivity.this,"设定为"+value+"秒",Toast.LENGTH_LONG).show();
                        }
                        catch (NumberFormatException e){
                            Log.i(TAG, "onYesClick: "+e.toString());
                            Toast.makeText(SetActivity.this,"请正确输入整型数！",Toast.LENGTH_LONG).show();


                        }


                    }
                });
                selfDialog_heart.setNoOnclickListener("取消", new SelfDialogOne.onNoOnclickListener() {
                    @Override
                    public void onNoClick() {
                        Toast.makeText(SetActivity.this,"点击了--取消--按钮",Toast.LENGTH_LONG).show();
                        selfDialog_heart.dismiss();
                    }
                });
                selfDialog_heart.show();

            }
        });
        addressLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selfDialog_url=new SelfDialogOne(SetActivity.this);
                selfDialog_url.setTitle("服务器地址");
                selfDialog_url.setMessage("请设置服务器地址：");
                selfDialog_url.setHint("请输入地址");
                selfDialog_url.setYesOnclickListener("确定",new SelfDialogOne.onYesOnclickListener() {
                    @Override
                    public void onYesClick() {
                        Toast.makeText(SetActivity.this,selfDialog_url.getValue(),Toast.LENGTH_LONG).show();
                        editor.putString("url",selfDialog_url.getValue());
                        editor.commit();
                        selfDialog_url.dismiss();
                    }
                });
                selfDialog_url.setNoOnclickListener("取消", new SelfDialogOne.onNoOnclickListener() {
                    @Override
                    public void onNoClick() {
                        Toast.makeText(SetActivity.this,"点击了--取消--按钮",Toast.LENGTH_LONG).show();
                        selfDialog_url.dismiss();
                    }
                });
                selfDialog_url.show();


            }
        });

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
}
