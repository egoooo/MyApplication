package newjohn.com.myapplication.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by Administrator on 2017/12/8.
 * 用于存放报警异常信息
 */
@Entity
public class AlertData {
    @Id(autoincrement = true)
    Long id ;
    String area;
    String deviceNum;
    String value;
    String info;
    String dateTime;

    @Generated(hash = 57587779)
    public AlertData(Long id, String area, String deviceNum, String value, String info, String dateTime) {
        this.id = id;
        this.area = area;
        this.deviceNum = deviceNum;
        this.value = value;
        this.info = info;
        this.dateTime = dateTime;
    }

    @Generated(hash = 2140717077)
    public AlertData() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
