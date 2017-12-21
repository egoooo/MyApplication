package newjohn.com.myapplication.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/11/19.
 */

public class History {


    public List<HistoryData> getHistoryDataList() {
        return historyDataList;
    }

    public void setHistoryDataList(List<HistoryData> historyDataList) {
        this.historyDataList = historyDataList;
    }

    public List<HistoryData> historyDataList;

}
