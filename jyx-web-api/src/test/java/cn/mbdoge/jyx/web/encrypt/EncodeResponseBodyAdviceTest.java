package cn.mbdoge.jyx.web.encrypt;


import cn.mbdoge.jyx.encrypt.AesEncrypt;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        properties = {
                "logging.level.cn.mbdoge.jyx.web=trace",
                "mbdoge.api.encrypt.enabled=true"
        }
)
@AutoConfigureMockMvc
class EncodeResponseBodyAdviceTest {
    @Autowired
    private MockMvc mockMvc; //只需 autowire
    @Autowired
    private ApiEncrypt apiEncrypt;

    @BeforeEach
    void setUp() {
    }

    @Test
    void name() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder =
                MockMvcRequestBuilders
                        .get("/input2");

        mockHttpServletRequestBuilder.param("test", "xxx");
//        mockHttpServletRequestBuilder.param("aa[]", "xxx");
        String contentAsString = mockMvc.perform(mockHttpServletRequestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        // input2 resp
        String s = "aaa";
        Map m = new HashMap();
        m.put("a", "a");
        m.put("s", s);
        m.put("d", "xxx");
        ObjectMapper mapper = new ObjectMapper();
        String s1 = mapper.writeValueAsString(apiEncrypt.encryptObj(m));

        Assertions.assertNotNull(contentAsString);
        Assertions.assertEquals(s1, contentAsString);
    }

    @Test
    void name2() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String aaa = mapper.writeValueAsString("aaa");
        System.out.println("aaa = " + aaa);

        String s = mapper.readValue(aaa, String.class);

        System.out.println("s = " + s);

    }
}