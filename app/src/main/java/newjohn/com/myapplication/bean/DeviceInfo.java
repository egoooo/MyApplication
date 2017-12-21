package newjohn.com.myapplication.bean;

/**
 * Created by Administrator on 2017/11/27.
 * 用于json解析，存放区域和区域下的设备编号
 */

public class DeviceInfo {
    String area;
    String deviceNum;

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
