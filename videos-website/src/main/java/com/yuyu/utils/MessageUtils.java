package com.yuyu.utils;

import com.alibaba.fastjson.JSON;
import com.yuyu.ws.pojo.ResultMessage;

public class MessageUtils {

    public static String getMessage(boolean isSystemMessage,String fromName,Object message,String groupId){
        ResultMessage result = new ResultMessage();
        result.setSystem(isSystemMessage);
        result.setMessage(message);
        result.setGroupId(groupId);
        if(fromName!=null){
            result.setFromName(fromName);
        }
        return JSON.toJSONString(result);
    }
}
