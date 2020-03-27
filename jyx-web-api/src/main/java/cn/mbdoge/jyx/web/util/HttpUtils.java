package cn.mbdoge.jyx.web.util;

import javax.servlet.http.HttpServletRequest;

public final class HttpUtils {
    private HttpUtils() {
    }

    /**
     * 判断是否是 websocket 请求
     * @return
     */
    public static boolean isWs (HttpServletRequest request) {
        String connectionHeader = request.getHeader("Connection");
        String upgradeHeader = request.getHeader("Upgrade");

        return "Upgrade".equalsIgnoreCase(connectionHeader) && "websocket".equalsIgnoreCase(upgradeHeader);
    }
}
