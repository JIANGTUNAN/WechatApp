package top.tolan.system.socket;

import com.alibaba.fastjson2.JSONObject;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/websocket/{token}")
@Component
@Slf4j
public class WebSocketServer {

    /**
     * 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
     */
    private static int onlineCount = 0;
    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     */
    private static final ConcurrentHashMap<String, WebSocketServer> webSocketMap = new ConcurrentHashMap<>();
    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;
    /**
     * 接收token
     */
    private String token = "";

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) {
        this.session = session;
        this.token = token;
        // 判断是否已经存在该用户
        if (webSocketMap.containsKey(token)) {
            // 存在则删除
            webSocketMap.remove(token);
        } else {
            // 不存在则加入
            webSocketMap.put(token, this);
            // 在线数加1
            addOnlineCount();
        }
        log.info("用户连接:{},当前在线人数为:{}", token, getOnlineCount());
        try {
            JSONObject msg = new JSONObject();
            msg.put("type", "linkSuccess");
            msg.put("msg", "连接成功");
            sendMessage(msg.toJSONString());
        } catch (IOException e) {
            log.error("用户:{},网络异常!!!!!!", token);
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        if (webSocketMap.containsKey(token)) {
            // 存在则删除
            webSocketMap.remove(token);
            // 在线数减1
            subOnlineCount();
        }
        log.info("token开头为{}的用户成功断开链接,当前在线人数为:{}", token.substring(0, 10), getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("{}用户消息 => 报文:{}", token.substring(0, 10), message);
        // 可以群发消息
        // 消息保存到数据库、redis
        if (StringUtils.isNotBlank(message)) {
            try {
                if (StringUtils.isNotBlank(message) && "ping".equals(message) && StringUtils.isNotBlank(this.token)) {
                    JSONObject msg = new JSONObject();
                    msg.put("type", "heartbeat");
                    msg.put("msg", "pong");
                    webSocketMap.get(this.token).sendMessage(msg.toJSONString());
                    return;
                }
                // 解析发送的报文
                JSONObject jsonObject = JSONObject.parseObject(message);
                // 追加发送人(防止串改)
                jsonObject.put("fromUserId", this.token);
                String fromUserId = jsonObject.getString("fromUserId");
                // 传送给对应toUserId用户的websocket
                if (StringUtils.isNotBlank(fromUserId) && webSocketMap.containsKey(fromUserId)) {
                    webSocketMap.get(fromUserId).sendMessage(jsonObject.toJSONString());
                } else {
                    // 否则不在这个服务器上，发送到mysql或者redis
                    log.error("请求的token:{}不存在或提前关闭了会话。", fromUserId.substring(0, 10));
                }
            } catch (Exception e) {
                log.error("处理客户端消息失败：{}", e.getMessage(), e);
            }
        }
    }

    /**
     * 发生错误时候
     *
     * @param session 会话
     * @param error   错误
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("用户错误:{},原因:{}", this.token, error.getMessage(), error);
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) throws IOException {
        // 加入线程锁
        synchronized (session) {
            try {
                // 同步发送信息
                this.session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                log.error("服务器推送失败:{}", e.getMessage());
            }
        }
    }

    /**
     * 发送自定义消息
     *
     * @param message  发送的信息
     * @param toUserId 如果为null默认发送所有
     * @throws IOException 异常
     */
    public static void sendInfo(String message, String toUserId) throws IOException {
        // 如果token为空，向所有群体发送
        if (StringUtils.isEmpty(toUserId)) {
            // 向所有用户发送信息
            for (String keys : webSocketMap.keySet()) {
                WebSocketServer item = webSocketMap.get(keys);
                item.sendMessage(message);
            }
        }
        // 如果不为空，则发送指定用户信息
        else if (webSocketMap.containsKey(toUserId)) {
            WebSocketServer item = webSocketMap.get(toUserId);
            item.sendMessage(message);
        } else {
            log.error("发送失败：请求的token:" + toUserId.substring(0, 10) + "不存在或提前关闭了会话");
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }

    public static synchronized ConcurrentHashMap<String, WebSocketServer> getWebSocketMap() {
        return WebSocketServer.webSocketMap;
    }
}
