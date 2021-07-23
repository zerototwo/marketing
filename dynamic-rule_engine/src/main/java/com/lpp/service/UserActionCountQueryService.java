package com.lpp.service;


import com.lpp.beans.LogBean;
import com.lpp.beans.RuleAtomicParam;
import com.lpp.beans.RuleParam;
import org.apache.flink.api.common.state.ListState;

/**
 * @desc 用户行为次数类条件查询服务接口
 */
public interface UserActionCountQueryService {

    public boolean queryActionCounts(ListState<LogBean> eventState, RuleParam ruleParam) throws Exception;



}
