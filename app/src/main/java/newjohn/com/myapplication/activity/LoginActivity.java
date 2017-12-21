package newjohn.com.myapplication.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import newjohn.com.myapplication.R;
import newjohn.com.myapplication.bean.User;
import newjohn.com.myapplication.global.Global;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class LoginActivity extends BaseActivity {

    private static final String TAG ="LoginActivity" ;
    @BindView(R.id.login)
    Button button;
    @BindView(R.id.userName)
    EditText editTextUser;
    @BindView(R.id.password)
    EditText editTextPassword;
    @BindView(R.id.remenber_pass)
    CheckBox rememberPass;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    LinearLayout linearLayoutWait;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    String reply= (String) msg.obj;
                    if (reply.equals("用户不存在！")){
                        linearLayoutWait.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this,"用户不存在！",Toast.LENGTH_SHORT).show();
                    }
                    if (reply.equals("密码输入错误！")){
                        linearLayoutWait.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this,"密码输入错误！",Toast.LENGTH_SHORT).show();
                    }
                    if (reply.equals("验证成功！")){
                        Toast.makeText(LoginActivity.this,"登录成功！",Toast.LENGTH_SHORT).show();
                        User user=new User();
                        user.setUserName(editTextUser.getText().toString());

                       Global.getGlobal().user=user;

                        editor=sharedPreferences.edit();
                        if (rememberPass.isChecked()){
                            editor.putBoolean("remember_password",true);
                            editor.putString("userName",editTextUser.getText().toString());
                            editor.putString("password",editTextPassword.getText().toString());

                        }
                        else {
                            editor.clear();
                        }
                        editor.commit();

                        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    break;

                case 2:
                    Toast.makeText(LoginActivity.this,"检查网络！",Toast.LENGTH_SHORT).show();
                    linearLayoutWait.setVisibility(View.GONE);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        linearLayoutWait=findViewById(R.id.loginwait);
        final OkHttpClient okHttpClient=new OkHttpClient();
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        boolean isRemember=sharedPreferences.getBoolean("remember_password",false);
        if (isRemember){
            String userName=sharedPreferences.getString("userName","");
            String password=sharedPreferences.getString("password","");
            editTextUser.setText(userName);
            editTextPassword.setText(password);
            rememberPass.setChecked(true);
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName=editTextUser.getText().toString();
                String password=editTextPassword.getText().toString();
                Log.i(TAG, "u: "+userName);
                Log.i(TAG, "p: "+password);
                if (userName.equals("")){
                    Toast.makeText(LoginActivity.this,"用户名不能为空",Toast.LENGTH_SHORT).show();
                }
                else if(password.equals("")){
                    Toast.makeText(LoginActivity.this,"密码不能为空",Toast.LENGTH_SHORT).show();
                }
                else {
                    linearLayoutWait.setVisibility(View.VISIBLE);

                    RequestBody requestBody=new FormBody.Builder()
                            .add("userName",userName)
                            .add("password",password)
                            .build();
                    Request request=new Request.Builder()
                            .url(Constant.URL+"WebProject/loginServer")
                            .post(requestBody)
                            .build();
                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.i(TAG, "onFailure: "+e.toString());


                            Message msg = handler.obtainMessage();
                            msg.what = 2;

                            handler.sendMessage(msg);


                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String str=response.body().string();

                            Log.i(TAG, "onResponse: "+str);

                            Message msg = handler.obtainMessage();
                            msg.what = 1;
                            msg.obj = str;
                            handler.sendMessage(msg);

                        }
                    });

                }





            }
        });
    }
}
