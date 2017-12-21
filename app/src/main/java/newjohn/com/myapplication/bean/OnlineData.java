package newjohn.com.myapplication.bean;

/**
 * Created by Administrator on 2017/11/27.
 * 存放区域下设备编号；Gson
 */

public class OnlineData {

    String deviceNum;
    String area;

    String status;
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getDeviceNum() {
        return deviceNum;
    }

    public void setDeviceNum(String deviceNum) {
        this.deviceNum = deviceNum;
    }
}
