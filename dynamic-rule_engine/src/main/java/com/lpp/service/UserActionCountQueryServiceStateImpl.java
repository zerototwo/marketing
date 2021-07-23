package com.lpp.service;

import com.lpp.beans.LogBean;
import com.lpp.beans.RuleAtomicParam;
import com.lpp.beans.RuleParam;
import org.apache.flink.api.common.state.ListState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 用户行为次数类条件查询服务实现：在flink的state中统计行为次数
 */
public class UserActionCountQueryServiceStateImpl implements UserActionCountQueryService{

    /**
     * 查询规则参数对象中，要求的用户行为次数类条件是否满足
     * 同时，将查询到的真实次数，set回 规则参数对象中
     *
     * @param ruleParam  规则整体参数对象
     * @return 条件是否满足
     */
    @Override
    public boolean queryActionCounts(ListState<LogBean> eventState,RuleParam ruleParam) throws Exception {
        //取出用户行为次数条件
        List<RuleAtomicParam> userActionCountParams = ruleParam.getUserActionCountParams();
        //迭代历史明细时间
        Iterable<LogBean> logBeanIterable = eventState.get();

        queryActionCountsHelper(logBeanIterable, userActionCountParams);

        for (RuleAtomicParam userActionCountParam : userActionCountParams) {
            if (userActionCountParam.getRealCnt() < userActionCountParam.getCnt()) {
                return false;
            }
        }
        return true;
    }

    public void queryActionCountsHelper(Iterable<LogBean> logBeanIterable,List<RuleAtomicParam> userActionCountParams){
        for (LogBean logBean : logBeanIterable) {

            for (RuleAtomicParam userActionCountParam : userActionCountParams) {

                boolean isMatch = eventBeanMatchEventParam(logBean, userActionCountParam);

                if (isMatch) {
                    userActionCountParam.setRealCnt(userActionCountParam.getRealCnt()+1);
                }
            }

        }

    }

    /**
     *
     * @param eventBean
     * @param eventParam
     * @return
     */
    private boolean eventBeanMatchEventParam(LogBean eventBean, RuleAtomicParam eventParam) {
        if (eventBean.getEventId().equals(eventParam.getEventId())) {
            Map<String,String> eventProperties = eventBean.getProperties();
            HashMap<String, String> eventParamProperties = eventParam.getProperties();
            Set<Map.Entry<String, String>> entries = eventParamProperties.entrySet();

            for (Map.Entry<String, String> entry : entries) {
                if (!entry.getValue().equals(eventProperties.get(entry.getKey()))) {
                    return false;
                }

                return true;
            }
        }

        return false;
    }

}
