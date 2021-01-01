package cn.mbdoge.jyx.web.util;

import com.fasterxml.jackson.databind.util.StdConverter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @author jyx
 */
public class LocalDateTimeDeserialize  extends StdConverter<Long, LocalDateTime> {


    @Override
    public LocalDateTime convert(Long value) {
        if (value == null) {
            return null;
        }
        return Instant.ofEpochMilli(value).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
