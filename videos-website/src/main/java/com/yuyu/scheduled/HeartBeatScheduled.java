package com.yuyu.scheduled;

import com.alibaba.fastjson.JSON;
import com.yuyu.ws.ChatEndPoint;
import com.yuyu.ws.pojo.ResultMessage;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class HeartBeatScheduled {

    /**
     * 服务器发送心跳
     */
    @Scheduled(cron = " 10/60 * * * * ?   ")
    public void sendHeartBeat(){
        ResultMessage resultMessage = new ResultMessage(null,true,null,"HeartBeat");
        String jsonString = JSON.toJSONString(resultMessage);
        ChatEndPoint.sendHeartBeat(jsonString);
    }
}
