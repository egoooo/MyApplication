package newjohn.com.myapplication.global;

import java.util.ArrayList;

import newjohn.com.myapplication.bean.DeviceInfo;
import newjohn.com.myapplication.bean.User;

/**
 * Created by Administrator on 2017/11/27.
 */

public class Global {
    public static User user=new User();
    public static ArrayList<DeviceInfo> deviceInfos;
    public static Global global;
    public static boolean isfirst=true;
    private Global(){
    };

    public static Global getGlobal(){
        if (global==null){
            global=new Global();
        }

        return global;

    };


}
