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
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import newjohn.com.myapplication.MyApplication;
import newjohn.com.myapplication.R;
import newjohn.com.myapplication.activity.AlertActivity;
import newjohn.com.myapplication.bean.AlertData;
import newjohn.com.myapplication.bean.AlertDataDao;
import newjohn.com.myapplication.global.Global;

import static android.R.attr.handle;

/**
 * Created by Administrator on 2017/12/3.
 */

public class PersistentConnectionService extends Service {


    private static final String TAG ="PCService" ;
    private static final String SOCKET_HOST ="183.66.64.47";

    public static final int SOCKET_PORT = 2334;
    private Intent intent = new Intent("com.example.communication.RECEIVER");
    private Socket mSocket;
    private DataOutputStream mDataOutputStream;
    int i=0;//测试用
    AlertDataDao alertDataDao;
    Timer timer = new Timer();
    TimerTask task;
    // 心跳机制
    private SocketReadThread mReadThread;
    private static final long HEART_BEAT_RATE = 4 * 1000;
    private long sendTime = 0L;

    private Handler mHandler=new Handler(){

        @Override
        public void handleMessage(Message msg) {

            Log.i(TAG, "handleMessage: "+i);
            super.handleMessage(msg);
            //发送Action为com.example.communication.RECEIVER的广播
            intent.putExtra("progress",(String) msg.obj);
            String result=(String) msg.obj;





                String[] info=result.split(",");
                Log.i(TAG, "handleMessage: "+ Arrays.toString(info));
                i++;
                alertDataDao.insert(new AlertData(null,info[0],info[1],info[2],info[3],info[4]));

                //获取NotificationManager实例
                NotificationManager notifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                PendingIntent contentIntent = PendingIntent.getActivity(
                        getApplicationContext(), 0, new Intent(PersistentConnectionService.this, AlertActivity.class), 0);
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
        alertDataDao= MyApplication.getMyApplication().getDaoSession().getAlertDataDao();

        connectToServer();



        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 连接服务端
     */
    private void connectToServer() {
        Thread connectThread = new Thread(new Runnable() {

            public void run() {
                try {
                    mSocket = new Socket();
                    mSocket.connect(new InetSocketAddress(SOCKET_HOST, SOCKET_PORT));

                    Log.i(TAG, "连接成功  " + SOCKET_HOST);
                    mDataOutputStream = new DataOutputStream(
                            mSocket.getOutputStream());

                   mDataOutputStream.writeUTF(Global.user.getUserName());
                   mDataOutputStream.flush();



//                    SocketWriteThread socketWriteThread=new SocketWriteThread(Global.user.getUserName());
//                    socketWriteThread.start();

                    // 开启线程负责读取服务端数据
                    mReadThread = new SocketReadThread();
                    mReadThread.start();
//
//                    // 心跳检测，检测socket是否连接
//                    mHandler.postDelayed(mHeartBeatRunnable, HEART_BEAT_RATE);
                    startHeartBeat();
                    sendNotification();


                } catch (UnknownHostException e) {
                    Log.e(TAG, "连接失败  ");
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.e(TAG, "连接失败  ");
                    e.printStackTrace();
                }
            }
        });
        connectThread.start();
    }


    /**
     * 断开连接
     *
     */
    private void releaseLastSocket() {
        try {
            if (null != mSocket) {
                if (!mSocket.isClosed()) {
                    mSocket.close();
                }
            }
            mSocket = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





    public void startHeartBeat(){
        task=new TimerTask() {
            @Override
            public void run() {
                if (System.currentTimeMillis() - sendTime >= HEART_BEAT_RATE) {//每隔4秒检测一次
                    boolean isSuccess = sendHeartBeatMsg("bbb");
                    Log.i(TAG, "run: "+isSuccess);
                    if (!isSuccess) {
                        Log.i(TAG, "连接已断开，正在重连……");
                        Thread.currentThread().interrupt();  // 移除线程，重连时保证该线程已停止上次调用时的工作
                        mReadThread.release();//释放SocketReadThread线程资源
                        releaseLastSocket();
                        connectToServer();// 再次调用connectToServer方法，连接服务端
                    }
                }

            }
        };
        timer.schedule(task,0,HEART_BEAT_RATE );

    }
//    private Runnable mHeartBeatRunnable = new Runnable() {
//
//        @Override
//        public void run() {
//            Log.i(TAG, "run: "+"xintiao");
//            if (System.currentTimeMillis() - sendTime >= HEART_BEAT_RATE) {//每隔4秒检测一次
//                boolean isSuccess = sendHeartBeatMsg("bbb");
//                Log.i(TAG, "run: "+isSuccess);
//                if (!isSuccess) {
//                    Log.i(TAG, "连接已断开，正在重连……");
//                    mHandler.removeCallbacks(mHeartBeatRunnable);// 移除线程，重连时保证该线程已停止上次调用时的工作
//                    mReadThread.release();//释放SocketReadThread线程资源
//                    releaseLastSocket();
//                    connectToServer();// 再次调用connectToServer方法，连接服务端
//                }
//            }
//            mHandler.postDelayed(this, HEART_BEAT_RATE);
//        }
//    };
    /**
     * 发送心跳包
     *
     * @param msg
     * @return
     */
    public boolean sendHeartBeatMsg(String msg) {
        if (null == mSocket) {
            return false;
        }
        try {
            if (!mSocket.isClosed() && !mSocket.isOutputShutdown()) {
                String message = msg + "\r\n";
                Log.i(TAG, "sendHeartBeatMsg: "+msg);

                mDataOutputStream.write(message.getBytes());
                mDataOutputStream.flush();
                sendTime = System.currentTimeMillis();
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public void handleStringMsg(String resultStr){
        Message message=Message.obtain();
        message.obj=resultStr;
        mHandler.sendMessage(message);

    }


    private void sendNotification() {


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
                .setContentTitle("异常信息！")
                //设置通知内容
                .setContentText("监测异常信息服务已开启！")
                .setContentIntent(contentIntent)
                .setVibrate(vibrate)
                .setPriority(Notification.PRIORITY_MAX);
        //设置通知时间，默认为系统发出通知的时间，通常不用设置
        //.setWhen(System.currentTimeMillis());
        //通过builder.build()方法生成Notification对象,并发送通知,id=1
        notifyManager.notify(1, builder.build());
    }


    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy: ");
        releaseLastSocket();
        task.cancel();
        timer.cancel();
        mReadThread.release();//释放SocketReadThread线程资源

        super.onDestroy();
    }






    public class SocketReadThread extends Thread {

        private static final String TAG = "SocketThread";
        private volatile boolean mStopThread = false;

        public void release() {
            mStopThread = true;
            releaseLastSocket();
        }

        @Override
        public void run() {
            DataInputStream mInputStream = null;
            try {
                mInputStream = new DataInputStream(mSocket.getInputStream());
                Log.d(TAG, "SocketThread running!");
                while (!mStopThread) {
                    String resultStr = mInputStream.readUTF();
                    handleStringMsg(resultStr);
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (SocketException e){
                e.toString();


            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (null != mSocket){
                    mSocket.close();
                    mSocket=null;
                }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (mInputStream != null) {
                    try {

                        mInputStream.close();
                        mInputStream = null;

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }


    }









}
