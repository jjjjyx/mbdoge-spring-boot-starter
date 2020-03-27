package cn.mbdoge.jyx.web.model;

import lombok.Data;

/**
 * @author jyx
 */
@Data
public class IpGeoVO {
    private static final String CN = "CN";

    String country;
    String countryCode;
    String region;
    String regionName;
    String city;

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
