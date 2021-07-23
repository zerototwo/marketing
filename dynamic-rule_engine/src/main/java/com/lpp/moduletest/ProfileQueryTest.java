package com.lpp.moduletest;

import com.lpp.beans.RuleParam;
import com.lpp.service.UserProfileQueryServiceHbaseImpl;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;

public class ProfileQueryTest {

    @Test
    public void testQueryProfile() throws IOException {
        // 构造参数
        HashMap<String, String> userProfileParams = new HashMap<>();
        userProfileParams.put("k12","v92");
        userProfileParams.put("k22","v3");

        RuleParam ruleParam = new RuleParam();
        ruleParam.setUserProfileParams(userProfileParams);


        // 构造一个查询服务
        UserProfileQueryServiceHbaseImpl impl = new UserProfileQueryServiceHbaseImpl();
        boolean b = impl.judgeProfileCondition("000645", ruleParam);
        System.out.println(b);
    }
}
