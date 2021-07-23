package com.lpp.service;


import com.lpp.beans.RuleParam;

/**
 * @desc 用户画像数据查询服务接口
 */
public interface UserProfileQueryService {

    public boolean judgeProfileCondition(String deviceId, RuleParam ruleParam);

}
