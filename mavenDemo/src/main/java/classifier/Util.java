package classifier;

import com.google.common.io.Resources;
import org.apache.maven.model.Resource;
import org.mortbay.log.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Util {

    public static String name[] = {"PET塑料瓶", "一次性塑料手套", "一次性筷子", "一次性纸杯", "中性笔", "作业本", "信封",
            "充电器", "充电宝", "充电电池", "充电线", "剃须刀", "剪刀", "化妆品瓶", "医用棉签", "口服液瓶", "吹风机", "土豆",
            "塑料包装", "塑料桶", "塑料玩具", "塑料盆", "塑料盖子", "塑料袋", "外卖餐盒", "头饰", "奶粉", "姜", "干电池", "废弃衣服",
            "废弃食用油", "快递盒", "手表", "打火机", "扫把", "护手霜", "护肤品玻璃罐", "抱枕", "抹布", "拖把", "指甲油瓶子",
            "指甲钳", "插座", "无纺布手提袋", "旧帽子", "旧玩偶", "旧镜子", "暖宝宝贴", "杀虫剂", "杏核", "杯子", "果皮",
            "棉签", "椅子", "毛毯", "水彩笔", "水龙头", "泡沫盒子", "洗面奶瓶", "海绵", "消毒液瓶", "烟盒", "牙刷", "牙签",
            "牙膏皮", "牛奶盒", "瓶盖", "电视机", "电风扇", "白糖_盐", "空调机", "糖果", "红豆", "纸尿裤", "纸巾_卷纸_抽纸",
            "纸箱", "纽扣", "耳机", "胶带", "胶水", "自行车", "菜刀", "菜板", "葡萄干", "蒜头", "蒜皮", "蚊香", "蛋_蛋壳",
            "衣架", "袜子", "辣椒", "过期化妆品", "退热贴", "酸奶盒", "铅笔屑", "陶瓷碗碟", "青椒", "面膜", "香烟", "鼠标"};

//    public static Map<Integer, String> readClassName() throws Exception {
//        Map<Integer, String> className = new HashMap<>();
//        System.out.println("name size: " + String.valueOf(name.length));
//        for(int i = 0; i < name.length; i++)
//            className.put(i, name[i]);
////        String filePath = Resources.getResource("class_index.txt").getPath();
////        System.out.println("file path: " + filePath);
////        File file = new File(filePath);
////        InputStreamReader inputReader = new InputStreamReader(new FileInputStream(file));
////        BufferedReader buffer = new BufferedReader(inputReader);
////        String str;
////        while((str = buffer.readLine()) != null) {
////            String[] split = str.split(" ");
////            className.put(Integer.valueOf(split[1]), split[0]);
////        }
////        buffer.close();
////        inputReader.close();
//        return className;
//    }

    public static int indexOfMax(float[] array) {
        int maxIndex = -1;
        float maxValue = -1.0f;
        for(int i = 0; i < array.length; i++) {
            if(array[i] > maxValue) {
                maxValue = array[i];
                maxIndex = i;
            }
        }
        return maxIndex;
    }
}
