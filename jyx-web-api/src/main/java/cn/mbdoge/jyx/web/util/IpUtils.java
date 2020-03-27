package cn.mbdoge.jyx.web.util;

import cn.mbdoge.jyx.web.AnalyzeIpGeoException;
import cn.mbdoge.jyx.web.model.IpGeoVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.URL;
import java.util.Map;

@Slf4j
public final class IpUtils {
    private IpUtils() {
    }

    private static final String LOCAL_IP = "127.0.0.1";
    private static final String GEO_URL = "http://ip-api.com/json/";
    private static final String QUERY_API_SUCCESS_FLAG = "success";

    /**
     * 获取请求中的真实 ip 地址
     * @param request
     * @return
     */
    public static String getRequestRealAddress(HttpServletRequest request) {
        String unknown = "unknown";
        if (request == null) {
            return unknown;
        }
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }

        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return "0:0:0:0:0:0:0:1".equals(ip) ? LOCAL_IP : ip;
    }

    public static IpGeoVO queryIpGeoByApi(InetAddress ipAddress) throws AnalyzeIpGeoException {
        return queryIpGeoByApi(ipAddress, "zh-CN");
    }

    public static IpGeoVO queryIpGeoByApi(InetAddress ipAddress, String lang) throws AnalyzeIpGeoException {
        try {
            URL url = new URL(GEO_URL + ipAddress.getHostAddress() + "?lang=" + lang);
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> geo = objectMapper.readValue(url, new TypeReference<Map<String, String>>() {});
            String status = geo.get("status");
            if (QUERY_API_SUCCESS_FLAG.equalsIgnoreCase(status)) {
                IpGeoVO ipGeoVO = new IpGeoVO();
                ipGeoVO.setCity(geo.get("city"));
                ipGeoVO.setCountry(geo.get("country"));
                ipGeoVO.setCountryCode(geo.get("countryCode"));
                ipGeoVO.setRegion(geo.get("region"));
                ipGeoVO.setRegionName(geo.get("regionName"));

                return ipGeoVO;
            } else {
                throw new Exception("query fail! " + geo.get("message"));
            }

        } catch (Exception e) {
            log.trace("查询 ip = {} 地址信息失败请求api失败", ipAddress);
            throw new AnalyzeIpGeoException(e);
        }
    }

}
