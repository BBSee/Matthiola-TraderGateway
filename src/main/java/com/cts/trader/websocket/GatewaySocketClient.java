package com.cts.trader.websocket;

import com.cts.trader.utils.SpringUtil;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.server.standard.SpringConfigurator;

import javax.annotation.Resource;
import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ClientEndpoint
@Component
public class GatewaySocketClient {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private Session session;
    private static Map<Session, String> brokerMapping = new ConcurrentHashMap<>();

    public void setBrokerMapping(String brokerName) {
        brokerMapping.put(this.session, brokerName);
    }

    private Map getBrokerMapping() {
        return brokerMapping;
    }

    private RedisTemplate redisTemplate;

    public static GatewaySocketClient connect(String url) throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        GatewaySocketClient client = new GatewaySocketClient();
        container.connectToServer(client, new URI(url));
        return client;
    }

    public void connect2Url(String url) throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        //this.brokerName = brokerName;
        this.session = container.connectToServer(GatewaySocketClient.class, new URI(url));
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        logger.info("Client on open");
    }

    @OnError
    public void onError(Throwable throwable) {
        logger.info("client on close");
    }

    @OnClose
    public void onClose() throws IOException {
        this.session.close();
    }

    @OnMessage
    public void onMessage(String message) throws Exception {
        logger.info("client on message" + message);

        redisTemplate = (RedisTemplate)SpringUtil.getBean("myRedisTemplate");

        JSONObject jsonObject = JSONObject.fromObject(message);
        Set<String> keySet = jsonObject.keySet();
        //String[] jsonKeys = (String[])keySet.toArray();
        List<String> jsonKeys = new ArrayList<String>();
        for (String key : keySet) {
            jsonKeys.add(key);
        }

        System.out.println(jsonKeys);
        String type = jsonObject.getString(jsonKeys.get(0));
        String futuresID = jsonObject.getString(jsonKeys.get(1));
        JSONObject data = jsonObject.getJSONObject(jsonKeys.get(2));
        String redisKey = "";
        switch (type) {
            case "orderBook":
                redisKey = "orderBook," + brokerMapping.get(session) + "," + futuresID;
                redisTemplate.opsForValue().set(redisKey, data.toString());
                break;

            case "trade":
                redisKey = "trade," + brokerMapping.get(session) + "," + futuresID;
                redisTemplate.opsForValue().set(redisKey, data.toString());
                break;
        }
        /*
        JSONObject marketData = jsonObject.getJSONObject("data");
        redisTemplate.opsForValue().set(marketKey, marketData.toString());
        */

        Thread.sleep(2000);
        sendMessage("heartbeat");
    }

    public void sendMessage(String message) throws IOException {
        session.getBasicRemote().sendText(message);
    }
}