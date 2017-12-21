package newjohn.com.myapplication;

import android.app.Application;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

import newjohn.com.myapplication.bean.DaoMaster;
import newjohn.com.myapplication.bean.DaoSession;

/**
 * Created by Administrator on 2017/11/19.
 */

public class MyApplication extends Application {

    private static MyApplication myApplication;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private SQLiteDatabase db;
    private DaoMaster.DevOpenHelper devOpenHelper;
    public SharedPreferences sharedPreferences;


    @Override
    public void onCreate() {
        super.onCreate();
        myApplication=this;
        setDatabase();
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);

    }

    public static MyApplication getMyApplication(){
        return myApplication;
    }

    private void setDatabase(){
        devOpenHelper=new DaoMaster.DevOpenHelper(this,"sensor_db",null);
        db=devOpenHelper.getWritableDatabase();
        daoMaster=new DaoMaster(db);
        daoSession=daoMaster.newSession();

    }

    public DaoSession getDaoSession(){
        return daoSession;
    }

    public SQLiteDatabase getDb(){
        return db;
    }

    public String getBaseUrl(){
        String url=sharedPreferences.getString("url","");

        return url;
    }
}
