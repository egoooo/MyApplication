package newjohn.com.myapplication.excel;

import android.os.Environment;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import newjohn.com.myapplication.bean.HistoryData;
import newjohn.com.myapplication.bean.Sensor;

/**
 * Created by Administrator on 2017/11/19.
 */

public class ExcelUtils {

    public  static  boolean writeExcel(List<Sensor> sensors) throws IOException {

//第一步,创建一个webbook文件,对应一个excel文件
        HSSFWorkbook wb = new HSSFWorkbook();
        //第二部,在excel中添加一个sheet工作簿,参数为该工作簿名字,不写为默认;
        HSSFSheet sheet = wb.createSheet("表1");
        //第三部,做sheet中添加表头第0行,注意老版本poi对excel的行数列数有限制short
        HSSFRow row = sheet.createRow((int)0);
        //第四部,创建单元格表头 设置表头居中
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);//创建一个居中格式

        //创建具体盛放数据的单元格,可以考虑把cell抽成共通对象去使用
        HSSFCell cell = row.createCell((int) 0);
        cell.setCellValue("区域");
        cell = row.createCell((int) 1);
        cell.setCellValue("设备编号");
        cell = row.createCell((int) 2);
        cell.setCellValue("压力上限值");
        cell = row.createCell((int) 3);
        cell.setCellValue("压力下限值");
        cell = row.createCell((int) 4);
        cell.setCellValue("当前值");
        cell = row.createCell((int) 5);
        cell.setCellValue("更新日期");

        for (int i=0;i<sensors.size();i++){
            //每次新建一行然后在新行中插入list中的数据对象,有点繁琐,也许有更好的封装方法,留待后看
            row = sheet.createRow((int)i+1);
            row.createCell((int)0).setCellValue(sensors.get(i).getArea());
            row.createCell((int)1).setCellValue(sensors.get(i).getDeviceNum());
            row.createCell((int)2).setCellValue(sensors.get(i).getUpLimit());
            row.createCell((int)3).setCellValue(sensors.get(i).getLowLimit());
            row.createCell((int)4).setCellValue(sensors.get(i).getValue());
            row.createCell((int)5).setCellValue(sensors.get(i).getDateTime());
        }



        //保存为xls文件
        File file = new File(Environment.getExternalStorageDirectory()+"/SensorData.xls");
        if(!file.exists()){
            file.createNewFile();
        }
        FileOutputStream outputStream = new FileOutputStream(file);
        wb.write(outputStream);//HSSFWorkbook自带写出文件的功能
        outputStream.close();
        System.out.println("写入成功");
        return true;


    }
}
