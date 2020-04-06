package cn.mbdoge.jyx.web.encrypt;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        properties = {
                "logging.level.cn.mbdoge.jyx.web=trace",
                "mbdoge.web.security.api.encrypt.enabled=true"
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
    @DisplayName("测试api 加密")
    void name() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder =
                MockMvcRequestBuilders
                        .get("/a/input2");

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
    void name2() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder =
                MockMvcRequestBuilders
                        .get("/a/login");

        mockHttpServletRequestBuilder.param("test", "xxx");

        String contentAsString = mockMvc.perform(mockHttpServletRequestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();


        Assertions.assertTrue(contentAsString.startsWith("\""));
        Assertions.assertTrue(contentAsString.endsWith("\""));
    }
}