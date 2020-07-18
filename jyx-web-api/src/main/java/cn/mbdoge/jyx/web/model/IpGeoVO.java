package cn.mbdoge.jyx.web.model;

import lombok.Data;

/**
 * @author jyx
 */
@Data
public class IpGeoVO {
    private static final String CN = "CN";
    String status;
    String message;
    String query;
    String country;
    String countryCode;
    String region;
    String regionName;
    String city;
    Float lat;
    Float lon;
    String timezone;
    Integer offset;

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
