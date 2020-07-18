package cn.mbdoge.jyx.web.util;

import cn.mbdoge.jyx.web.model.IpGeoVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IpUtilsTest {

    @Test
    void internalIp() throws UnknownHostException {

        // InetAddress inetAddress = InetAddress.getByName("0:0:0:0:0:0:0:1");
        // System.out.println("inetAddress = " + Arrays.toString(inetAddress.getAddress()));
        // System.out.println("inetAddress.hashCode() = " + inetAddress.hashCode());
        //
        // inetAddress = InetAddress.getByName("127.0.0.1");
        // System.out.println("inetAddress = " + Arrays.toString(inetAddress.getAddress()));
        // System.out.println("inetAddress.hashCode() = " + inetAddress.hashCode());

        assertTrue(IpUtils.internalIp(InetAddress.getByName("0:0:0:0:0:0:0:1")));

        assertTrue(IpUtils.internalIp(InetAddress.getByName("127.0.0.1")));
        assertTrue(IpUtils.internalIp(InetAddress.getByName("192.168.2.1")));
        assertTrue(IpUtils.internalIp(InetAddress.getByName("192.168.55.12")));

        assertFalse(IpUtils.internalIp(InetAddress.getByName("192.167.55.12")));

        assertTrue(IpUtils.internalIp(InetAddress.getByName("10.167.55.12")));
        assertFalse(IpUtils.internalIp(InetAddress.getByName("11.167.55.12")));

    }

    @Test
    @DisplayName("测试单个获取 异常情况")
    void testApiGeoException() throws UnknownHostException {

        assertThrows(AnalyzeIpGeoException.class, () -> {
            IpUtils.queryIpGeoByApi(InetAddress.getByName("127.0.0.1"));
        });

        assertThrows(AnalyzeIpGeoException.class, () -> {
            IpUtils.queryIpGeoByApi(InetAddress.getByName("0:0:0:0:0:0:0:1"));
        });

        assertThrows(AnalyzeIpGeoException.class, () -> {
            IpUtils.queryIpGeoByApi(InetAddress.getByName("192.168.55.12"));
        });

        assertThrows(AnalyzeIpGeoException.class, () -> {
            IpUtils.queryIpGeoByApi(InetAddress.getByName("192.168.2.1"));
        });

        assertThrows(AnalyzeIpGeoException.class, () -> {
            IpUtils.queryIpGeoByApi(InetAddress.getByName("10.167.55.12"));
        });


    }
    @Test
    @DisplayName("测试单个获取")
    void testApiGeo() throws UnknownHostException, AnalyzeIpGeoException {

        IpGeoVO ipGeoVO = IpUtils.queryIpGeoByApi(InetAddress.getByName("11.167.55.12"));
        assertNotNull(ipGeoVO);
        assertNotNull(ipGeoVO.getCity());

        ipGeoVO = IpUtils.queryIpGeoByApi(InetAddress.getByName("8.8.8.8"));
        assertNotNull(ipGeoVO);
        assertNotNull(ipGeoVO.getCity());
    }

    @Test
    @DisplayName("测试批量获取")
    void testApiGeoBatch() throws UnknownHostException, AnalyzeIpGeoException {
        InetAddress[] addresses = {
                InetAddress.getByName("11.167.55.12"),
                InetAddress.getByName("8.8.8.8")
        };

        List<IpGeoVO> ipGeoVOS = IpUtils.queryIpGeoByApi(addresses);
        assertEquals(2, ipGeoVOS.size());

        System.out.println("ipGeoVOS = " + ipGeoVOS);

        addresses = new InetAddress[] {
                InetAddress.getByName("11.167.55.12"),
                InetAddress.getByName("8.8.8.8"),
                InetAddress.getByName("10.167.55.12"),
        };

        ipGeoVOS = IpUtils.queryIpGeoByApi(addresses);
        assertEquals(2, ipGeoVOS.size());
        System.out.println("ipGeoVOS = " + ipGeoVOS);
    }
}