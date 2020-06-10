package com.cj.ticketsys.svc.b2b.lvmm;

import com.alibaba.fastjson.JSON;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static com.cj.ticketsys.svc.b2b.lvmm.LvConstant.*;


/**
 * @author wangliwei
 * @date 2020/4/7
 *
 * 驴妈妈接口对接sign工具类
 */

public class LvSignUtil {

    /**
     * 获取签名——驴妈妈平台
     * @param json json格式参数
     * @return
     */
    public static String getSign(String json) throws Exception {

        // 1-首先去除contacts和travellerList
        // 1.1-将json转换为map
        Map<String, String> map = JSON.parseObject(json, Map.class);
        // 1.2-去除contacts和travellerList以及sign键值对
        map.remove(CONTACTS);
        map.remove(TRAVELLER_LIST);
        map.remove(SIGN);

        // 2-然后按照变量名的顺序升序排列
        // 2.1-提取所有的key
        Set<String> keySet = map.keySet();
        // 2.2-将所有的key按照升序排列
        Set<String> treeKey = new TreeSet<>(keySet);
        // 2.3-将所有的key对应的value拼接成字符串
        StringBuilder sb = new StringBuilder();
        for (String key : treeKey) {
            sb.append(map.get(key));
        }
        String res = sb.toString();

        // 3-使用md5进行加密
        res = Md5Util.encodeByMd5(res);

        // 4-加上signKey
        res = res + SIGN_KEY;

        // 5-再次使用md5进行加密
        res = Md5Util.encodeByMd5(res);

        return res;
    }

    /**
     * 校验签名是否正确
     *
     * @param json 参数
     * @return true-签名正确；false-签名错误
     */
    public static boolean verifySign(String json) throws Exception {
        //获取参数中的签名
        String signParam = JSON.parseObject(json).getString(SIGN);
        //根据参数生成签名
        String sign = getSign(json);
        //校验两个签名是否相同
        return sign.equals(signParam);
    }

    /**
     * 测试
     * @param args
     * @throws Exception
     */
//    public static void main(String[] args) throws Exception {
//
//        String json = "{\n" +
//                "\t\"msg\":\"\",\n" +
//                "\t\"serialNo\":\"5678\",\n" +
//                "\t\"uid\":\"lvmama1234\",\n" +
//                "\t\"password\":\"85a61c849\",\n" +
//                "\t\"status\":\"0\",\n" +
//                "\t\"sign\":\"test\",\n" +
//                "\t\"authCode\":\"test\",\n" +
//                "\t\"codeURL\":\"test\",\n" +
//                "\t\"orderId\":\"124818851606038118\"\n" +
//                "}";
//
//        System.out.println(getSign(json));
//    }

}
