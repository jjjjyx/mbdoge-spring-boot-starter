package cn.mbdoge.jyx.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.EnumDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class EventTypeTest {

    @Test
    void name() throws JsonProcessingException {
        // 在反序列化到EventType 时有问题
        ObjectMapper objectMapper = new ObjectMapper();

        String ret = "{\n" +
                "  \"code\": 1,\n" +
                "  \"type\": \"init\"" +
                "}";


        C c = objectMapper.readValue(ret, C.class);

        assertEquals(c.getType(), A.init);

        ret = "{\n" +
                "  \"code\": 1,\n" +
                "  \"type\": \"event\"" +
                "}";

        c = objectMapper.readValue(ret, C.class);

        assertEquals(c.getType(), A.event);


        assertThrows(InvalidFormatException.class, () -> {
            objectMapper.readValue("{\n" +
                    "  \"code\": 1,\n" +
                    "  \"type\": \"xx\"" +
                    "}", C.class);
        });


//        assertEquals(c.getType(), A.event);
    }


    enum A implements EventType {
        event {
            @Override
            public String[] getNames() {
                return new String[0];
            }
        },

        init {
            @Override
            public String[] getNames() {
                return new String[0];
            }
        }
    }

    public static class C {
        public C() {
        }

        private int code;
        private A type;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }
//

        public A getType() {
            return type;
        }

        public void setType(A type) {
            this.type = type;
        }
    }

}
