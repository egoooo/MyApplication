package newjohn.com.myapplication.serv;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import newjohn.com.myapplication.MyApplication;
import newjohn.com.myapplication.R;
import newjohn.com.myapplication.activity.AlertActivity;
import newjohn.com.myapplication.bean.AlertData;
import newjohn.com.myapplication.bean.AlertDataDao;
import newjohn.com.myapplication.global.Global;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;




/**
 * Created by Administrator on 2017/12/22.
 */

public class WebSocketService extends Service {

    private String TAG="WebSocketService";
    private  ExecutorService executorService = Executors.newSingleThreadExecutor();
    WebSocketClient webSocketClient;
    AlertDataDao alertDataDao;
    private boolean isCloseService=false;
    private Intent intent = new Intent("com.example.communication.RECEIVER");
    private Handler mHandler=new Handler(){

        @Override
        public void handleMessage(Message msg) {

            Log.i(TAG, "handleMessage: "+msg);
            super.handleMessage(msg);
            //发送Action为com.example.communication.RECEIVER的广播
            intent.putExtra("progress",(String) msg.obj);
            String result=(String) msg.obj;





            String[] info=result.split(",");
            Log.i(TAG, "handleMessage: "+ Arrays.toString(info));
            alertDataDao.insert(new AlertData(null,info[0],info[1],info[2],info[3],info[4]));

            //获取NotificationManager实例
            NotificationManager notifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            PendingIntent contentIntent = PendingIntent.getActivity(
                    getApplicationContext(), 0, new Intent(WebSocketService.this, AlertActivity.class), 0);
            //震动设置
            long[] vibrate = new long[]{0, 500, 1000, 1500};
            //实例化NotificationCompat.Builde并设置相关属性
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                    //设置小图标
                    .setSmallIcon(R.drawable.a)
                    //设置通知标题
                    .setContentTitle("异常信息！")
                    //设置通知内容
                    .setContentText(info[0]+"的"+info[1]+"设备压力值为："+info[2]+"，"+info[3])
                    .setContentIntent(contentIntent)
                    .setVibrate(vibrate)
                    .setPriority(Notification.PRIORITY_MAX);
            //设置通知时间，默认为系统发出通知的时间，通常不用设置
            //.setWhen(System.currentTimeMillis());
            //通过builder.build()方法生成Notification对象,并发送通知,id=1
            notifyManager.notify(1, builder.build());




            sendBroadcast(intent);

        }


    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: "+"kaishi");
        alertDataDao= MyApplication.getMyApplication().getDaoSession().getAlertDataDao();

        connect();
        return super.onStartCommand(intent, flags, startId);
    }


   public void connect(){


        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Map<String, String> headers = new HashMap();
                webSocketClient = new WebSocketClient(URI.create("ws://183.66.64.47:6667/WebProject/websocket"), new Draft_6455(), headers, 5*1000) {

                    @Override
                    public void onOpen(ServerHandshake serverHandshake) {
                        System.out.println("client onOpen");
                        webSocketClient.send(Global.user.getUserName());
                        sendNotification("提示","报警服务已开启");
                    }

                    @Override
                    public void onMessage(String s) {
                        System.out.println("client onMessage:" + s);
                        Message message=Message.obtain();
                        message.obj=s;
                        mHandler.sendMessage(message);
                    }

                    @Override
                    public void onClose(int i, String s, boolean b) {
                        System.out.println("client onClose:" + i + " " + s + " " + b);

                        if (!isCloseService){
                            sendNotification("提示","网络关闭，报警服务被迫关闭,请在主页面手动重启");

                        }
                        stopSelf();

                    }

                    @Override
                    public void onError(Exception e) {
                        System.out.println("client onError:" + e);
                        sendNotification("提示","网络错误，报警服务被迫关闭，请在主页面手动重启");
                        stopSelf();



                    }

                    @Override
                    public void onClosing(int code, String reason, boolean remote) {
                        System.out.println("client onClosing:" + reason+code);
                        super.onClosing(code, reason, remote);

                    }
                };

                webSocketClient.connect();
            }
        });
    }


    @Override
    public void onDestroy() {
        isCloseService=true;

        webSocketClient.close();


        Log.i(TAG, "onDestroy: "+"dddd");
        super.onDestroy();
    }


    private void sendNotification(String title,String text) {


        //获取NotificationManager实例
        NotificationManager notifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, AlertActivity.class), 0);
        //震动设置
        long[] vibrate = new long[]{0, 500, 1000, 1500};
        //实例化NotificationCompat.Builde并设置相关属性
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                //设置小图标
                .setSmallIcon(R.drawable.a)
                //设置通知标题
                .setContentTitle(title)
                //设置通知内容
                .setContentText(text)
                .setContentIntent(contentIntent)
                .setVibrate(vibrate)
                .setPriority(Notification.PRIORITY_MAX);
        //设置通知时间，默认为系统发出通知的时间，通常不用设置
        //.setWhen(System.currentTimeMillis());
        //通过builder.build()方法生成Notification对象,并发送通知,id=1
        notifyManager.notify(1, builder.build());
    }
}
