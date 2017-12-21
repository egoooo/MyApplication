package newjohn.com.myapplication.filter;

import java.util.List;

/**
 * Created by Administrator on 2017/11/20.
 */

public class FilterAlgorithm {
    List<Float> outputList;


    /**
     *限幅滤波法
     * @param inputList 输入序列
     * @param value    //参考值
     * @param FILTER_A//容许误差
     * @return
     */
    public  List<Float> LimitingFiltering(List<Float> inputList ,Float value,Float FILTER_A){
        for (int i = 0; i < inputList.size(); i++) {
            if (inputList.get(i)-value>FILTER_A||value-inputList.get(i)>FILTER_A){


            }
            else {
                outputList.add(inputList.get(i));

            }

        }

        System.out.println(outputList.toString());

     return outputList;
    }
}
