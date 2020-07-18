package cn.mbdoge.jyx.web.util;

import cn.mbdoge.jyx.web.model.IpGeoVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author jyx
 */
@Slf4j
public final class IpUtils {


    private static final String LOCAL_IP = "127.0.0.1";
    private static final String GEO_URL = "http://ip-api.com/json/";
    private static final String GEO_BATCH_URL = "http://ip-api.com/batch";
    private static final String QUERY_API_SUCCESS_FLAG = "success";
    private static final String UNKNOWN = "unknown";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    static {
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private IpUtils() {}
    /**
     * 获取请求中的真实 ip 地址
     * @param request req
     * @return ip
     */
    public static String getRequestRealAddress(HttpServletRequest request) {

        if (request == null) {
            return UNKNOWN;
        }
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return "0:0:0:0:0:0:0:1".equals(ip) ? LOCAL_IP : ip;
    }

    /**
     * 10.x.x.x/8
     */
    private static final byte SECTION_1 = 0x0A;

    /**
     * 172.16.x.x/12
     */
    private static final byte SECTION_2 = (byte) 172;
    private static final byte SECTION_3 = 16;
    private static final byte SECTION_4 = (byte) 0x1F;
    /**
     * 192.168.x.x/16
     */
    private static final byte SECTION_5 = (byte) 0xC0;
    private static final byte SECTION_6 = (byte) 0xA8;
    // 127.0.0.1
    private static final int LOCAL_1 = 2130706433;
    // 0:0:0:0:0:0:0:1
    private static final int LOCAL_2 = 1;

    /**
     * 判断ip 是否是内网ip地址
     * @param ipAddress ip
     * @return true 是内网地址
     */
    public static boolean internalIp (InetAddress ipAddress) {
        int code = ipAddress.hashCode();
        if (code == LOCAL_1 || LOCAL_2 == code) {
            return true;
        }
        byte[] address = ipAddress.getAddress();
        final byte b0 = address[0];
        final byte b1 = address[1];

        switch (b0) {
            case SECTION_1:
                return true;
            case SECTION_2:
                if (b1 >= SECTION_3 && b1 <= SECTION_4) {
                    return true;
                }
            case SECTION_5:
                if (b1 == SECTION_6) {
                    return true;
                }
            default:
                return false;

        }

    }

    /**
     * 查询ip的地理位置 ，默认返回中文
     * @param ipAddress ip
     * @return 地理位置
     * @throws AnalyzeIpGeoException 查询失败
     */
    public static IpGeoVO queryIpGeoByApi(InetAddress ipAddress) throws AnalyzeIpGeoException {

        return queryIpGeoByApi(ipAddress, "zh-CN");
    }

    /**
     * 查询ip的地理位置 指定使用的语言
     * @param ipAddress ip
     * @param lang 语言
     * @return 地理位置
     * @throws AnalyzeIpGeoException 查询失败
     */
    public static IpGeoVO queryIpGeoByApi(InetAddress ipAddress, String lang) throws AnalyzeIpGeoException {

        if (internalIp(ipAddress)) {
            throw new AnalyzeIpGeoException("private range");
        }

        try {
            URL url = new URL(GEO_URL + ipAddress.getHostAddress() + "?lang=" + lang);
            IpGeoVO geo = OBJECT_MAPPER.readValue(url, new TypeReference<IpGeoVO>() {});

            if (QUERY_API_SUCCESS_FLAG.equals(geo.getStatus())) {
                return geo;
            }
        } catch (Exception e) {
            log.trace("查询 ip = {} 地址信息失败请求api失败", ipAddress);
            throw new AnalyzeIpGeoException(e);
        }
        throw new AnalyzeIpGeoException("private range");
    }

    /**
     * 批量查询ip地址 自动过滤所有内网地址
     * @param ipAddress ip 列表
     * @return 地理位置列表
     * @throws AnalyzeIpGeoException api 请求失败
     */
    public static List<IpGeoVO> queryIpGeoByApi(InetAddress[] ipAddress) throws AnalyzeIpGeoException {
        Objects.requireNonNull(ipAddress);

        List<String> ips = Stream.of(ipAddress).filter(i -> !internalIp(i)).map(InetAddress::getHostAddress).collect(Collectors.toList());
        if (ips.isEmpty()) {
            return new ArrayList<>();
        }
        byte[] body = ("[\"" + String.join("\",\"", ips) + "\"]").getBytes(StandardCharsets.UTF_8);
        try {
            URL realUrl = new URL(GEO_BATCH_URL);
            URLConnection connection = realUrl.openConnection();
            HttpURLConnection http = (HttpURLConnection) connection;
            http.setRequestMethod("POST");
            http.setDoOutput(true);

            http.setFixedLengthStreamingMode(body.length);
            http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            try(OutputStream os = http.getOutputStream()) {
                os.write(body);
            }

            http.connect();

            List<IpGeoVO> geos = OBJECT_MAPPER.readValue(http.getInputStream(), new TypeReference<List<IpGeoVO>>() {
            });
            return geos.stream().filter((vo) -> QUERY_API_SUCCESS_FLAG.equals(vo.getStatus())).collect(Collectors.toList());

        } catch (Exception e) {
            log.trace("查询 ip = {} 地址信息失败请求api失败", Arrays.toString(ipAddress));
            throw new AnalyzeIpGeoException(e);
        }
    }


    // public static void main(String[] args) throws JsonProcessingException {
    //     String o = "{\n" +
    //             "        \"status\": \"success\",\n" +
    //             "        \"country\": \"Canada\",\n" +
    //             "        \"countryCode\": \"CA\",\n" +
    //             "        \"region\": \"QC\",\n" +
    //             "        \"regionName\": \"Quebec\",\n" +
    //             "        \"city\": \"Montreal\",\n" +
    //             "        \"zip\": \"H1S\",\n" +
    //             "        \"lat\": 45.5808,\n" +
    //             "        \"lon\": -73.5825,\n" +
    //             "        \"timezone\": \"America/Toronto\",\n" +
    //             "        \"isp\": \"Le Groupe Videotron Ltee\",\n" +
    //             "        \"org\": \"Videotron Ltee\",\n" +
    //             "        \"as\": \"AS5769 Videotron Telecom Ltee\",\n" +
    //             "        \"query\": \"24.48.0.1\"\n" +
    //             "    },";
    //
    //     IpGeoVO ipGeoVO = OBJECT_MAPPER.readValue(o, new TypeReference<IpGeoVO>() {
    //     });
    //     System.out.println("ipGeoVO = " + ipGeoVO.getStatus());
    //     System.out.println("ipGeoVO = " + ipGeoVO);
    //     System.out.println("ipGeoVO = " + ipGeoVO.getLat());
    // }

}
