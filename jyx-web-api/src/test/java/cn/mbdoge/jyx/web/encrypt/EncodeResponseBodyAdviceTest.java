package cn.mbdoge.jyx.web.encrypt;


import cn.mbdoge.jyx.encrypt.AesEncrypt;
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

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        properties = {
                "logging.level.cn.mbdoge.jyx.web.handler=trace",
                "mbdoge.api.encrypt.enabled=true"
        }
)
@AutoConfigureMockMvc
class EncodeResponseBodyAdviceTest {
    @Autowired
    private MockMvc mockMvc; //只需 autowire
    @Autowired
    private ApiEncryptProperties properties;

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
//        String decrypt = AesEncrypt.decrypt(contentAsString, properties.getSecret());

        Assertions.assertNotNull(contentAsString);
        Assertions.assertTrue(contentAsString.startsWith("\""));
        Assertions.assertTrue(contentAsString.endsWith("\""));
//        System.out.println("decrypt = " + decrypt);

    }
}