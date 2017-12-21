package newjohn.com.myapplication.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by Administrator on 2017/11/30.
 * 用于存放当前用户所能查询的区域和区域下的设备编号；
 * 数据库使用
 */

@Entity
public class DeviceDb {
    @Id(autoincrement = true)
    Long id;

    String area;
    String deviceNum;

    @Generated(hash = 1196538442)
    public DeviceDb(Long id, String area, String deviceNum) {
        this.id = id;
        this.area = area;
        this.deviceNum = deviceNum;
    }

    @Generated(hash = 2118226980)
    public DeviceDb() {
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
}
