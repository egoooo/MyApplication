package newjohn.com.myapplication.GraphBean;

import java.util.List;

/**
 * Created by Administrator on 2017/12/4.
 */

public class GraphData {
    String deviceNum;
    List<ContentData> content;

    public String getDeviceNum() {
        return deviceNum;
    }

    public void setDeviceNum(String deviceNum) {
        this.deviceNum = deviceNum;
    }

    public List<ContentData> getContent() {
        return content;
    }

    public void setContent(List<ContentData> content) {
        this.content = content;
    }
}
