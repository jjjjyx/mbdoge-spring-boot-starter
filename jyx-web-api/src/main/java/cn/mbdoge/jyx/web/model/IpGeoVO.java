package cn.mbdoge.jyx.web.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author jyx
 */
@Data
public class IpGeoVO implements Serializable {
    private static final String CN = "CN";
    private String status;
    private String message;
    private String query;
    private String country;
    private String countryCode;
    private String region;
    private String regionName;
    private String city;
    private Float lat;
    private Float lon;
    private String timezone;
    private Integer offset;

    @Override
    public String toString () {
        // 国家是中国的不显示国家
        String ret = "";

        if (!CN.equalsIgnoreCase(countryCode)) {
            ret = country + " ";
        }

        return ret + regionName + " " + city;
    }
}
