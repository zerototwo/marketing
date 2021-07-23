package com.lpp.service;


import com.lpp.beans.RuleParam;

/**
 * @desc 用户行为次序列条件查询服务接口
 */
public interface UserActionSequenceQueryService {

    public boolean queryActionSequence(String deviceId, RuleParam ruleParam) throws Exception;
}
