package cn.mbdoge.jyx.web.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author jyx
 */
@Data
public class IpGeoVO implements Serializable {

    private static final long serialVersionUID = -8729156811365170222L;

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

    private String continent;
    private String continentCode;
    private String district;
    private String zip;
    private String currency;
    private String isp;
    private String org;
    private String as;
    private String asname;
    private Boolean mobile;
    private Boolean proxy;
    private Boolean hosting;


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
