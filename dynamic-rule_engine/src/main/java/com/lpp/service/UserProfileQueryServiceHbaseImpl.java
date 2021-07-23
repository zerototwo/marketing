package com.lpp.service;

import com.lpp.beans.RuleParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
@Slf4j
public class UserProfileQueryServiceHbaseImpl implements UserProfileQueryService{


    static Connection conn;
    static Table table;

    static {
        Configuration conf = new Configuration();
        conf.set("hbase.zookeeper.quorum", "10.12.0.168:2181,10.12.0.166:2181,10.12.0.164:2181");

        log.debug("hbase连接准备创建");
        try {
            conn = ConnectionFactory.createConnection(conf);
            table = conn.getTable(TableName.valueOf("user_profile"));
            log.debug("hbase连接创建完毕");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 构造函数
     */
    public UserProfileQueryServiceHbaseImpl() throws IOException {
        /*Configuration conf = new Configuration();
        conf.set("hbase.zookeeper.quorum", "hdp01:2181,hdp02:2181,hdp03:2181");

        log.debug("hbase连接准备创建");
        conn = ConnectionFactory.createConnection(conf);
        table = conn.getTable(TableName.valueOf("yinew_profile"));
        log.debug("hbase连接创建完毕");*/
    }

    /**
     * 传入一个用户号，以及要查询的条件
     * 返回这些条件是否满足
     * TODO 本查询只返回了成立与否，而查询到的画像数据值并没有返回 可能为将来的缓存模块带来不便，有待改造
     * @param deviceId 设备id
     * @param ruleParam 规则参数对象
     * @return 是否成立
     */
    @Override
    public boolean judgeProfileCondition(String deviceId, RuleParam ruleParam){

        // 从规则参数中取出画像标签属性条件
        HashMap<String, String> userProfileParams = ruleParam.getUserProfileParams();

        // 取出条件中所要求的所有待查询标签名
        Set<String> tagNames = userProfileParams.keySet();

        // 构造一个hbase的查询参数对象
        Get get = new Get(deviceId.getBytes());
        // 把要查询的标签（hbase表中的列）逐一添加到get参数中
        for (String tagName : tagNames) {
            get.addColumn("f".getBytes(),tagName.getBytes());
        }



        // 调用hbase的api执行查询
        String valueStr = "";
        long ts =0;
        long te =0;
        try {
            ts = System.currentTimeMillis();
            Result result = table.get(get);
            // 判断结果和条件中的要求是否一致
            for (String tagName : tagNames) {
                // 从查询结果中取出该标签的值
                byte[] valueBytes = result.getValue("f".getBytes(), tagName.getBytes());
                // 判断查询到的value和条件中要求的value是否一致，如果不一致，方法直接返回：false
                te = System.currentTimeMillis();
                if(valueBytes == null){
                    log.info("规则:{},用户:{},查询Hbase,要求的条件是:{},{},查询结果为:{},匹配失败,耗费时长:{}",ruleParam.getRuleName(),
                            deviceId,tagName,userProfileParams.get(tagName),"null",te-ts);
                    return false;
                }
                valueStr = new String(valueBytes);
                if(!valueStr.equals(userProfileParams.get(tagName))){
                    log.debug("规则:{},用户:{},查询Hbase,要求的条件是:{},{},查询结果为:{},匹配失败,耗费时长:{}",ruleParam.getRuleName(),
                            deviceId,tagName,userProfileParams.get(tagName),new String(valueBytes),te-ts);
                    return false;
                }
            }

            log.info("规则:{},用户:{},查询Hbase,要求的条件是:{},查询结果为:{},匹配成功,耗费时长:{}",ruleParam.getRuleName(),
                    deviceId,userProfileParams,valueStr,te-ts);
            // 如果上面的for循环走完了，那说明每个标签的查询值都等于条件中要求的值，则可以返回true
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 如果到了这，说明前面的查询出异常了，返回false即可
        return false;
    }
}
