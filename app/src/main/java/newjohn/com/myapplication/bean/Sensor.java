package newjohn.com.myapplication.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Administrator on 2017/11/19.
 * 数据库下存放历史数据；
 */
@Entity
public class Sensor {
    @Id(autoincrement = true)
    Long id;

    @Generated(hash = 1502300579)
    public Sensor(Long id, String area, String deviceNum, String upLimit, String lowLimit, String value, String dateTime) {
        this.id = id;
        this.area = area;
        this.deviceNum = deviceNum;
        this.upLimit = upLimit;
        this.lowLimit = lowLimit;
        this.value = value;
        this.dateTime = dateTime;
    }

    @Generated(hash = 786345970)
    public Sensor() {
    }

    String area;
    String deviceNum;


    String upLimit;

    String lowLimit;
    String value;
    String dateTime;

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

    public String getUpLimit() {
        return upLimit;
    }

    public void setUpLimit(String upLimit) {
        this.upLimit = upLimit;
    }

    public String getLowLimit() {
        return lowLimit;
    }

    public void setLowLimit(String lowLimit) {
        this.lowLimit = lowLimit;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
