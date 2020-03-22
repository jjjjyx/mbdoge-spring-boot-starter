package cn.mbdoge.jyx.web.tomcat;

import org.junit.jupiter.api.Assertions;
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
import org.springframework.web.bind.MissingServletRequestParameterException;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        properties = {
                "logging.level.cn.mbdoge.jyx.web.handler=trace",
                "mbdoge.api.encrypt.enabled=false"
        }
)
@AutoConfigureMockMvc
public class WebServerFactoryTest {
    @Autowired
    private MockMvc mockMvc; //只需 autowire

    /**
     * 这个 ?a[]=xx 这种参数形式测试不出来
     * 需要在启动的tomcat 上发起请求
     * @throws Exception
     */
    @Test
    void name() throws Exception {

        MockHttpServletRequestBuilder mockHttpServletRequestBuilder =
                MockMvcRequestBuilders
                        .get("/input2?a[]=xx");

        mockHttpServletRequestBuilder.param("test", "xxx");
//        mockHttpServletRequestBuilder.param("aa[]", "xxx");
        MvcResult xxx = mockMvc.perform(mockHttpServletRequestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.d").value("xxx"))
                .andReturn();
        Assertions.assertNull(xxx.getResolvedException());
    }
}