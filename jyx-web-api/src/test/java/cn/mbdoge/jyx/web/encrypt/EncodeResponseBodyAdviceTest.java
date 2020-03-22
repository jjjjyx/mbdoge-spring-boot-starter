package cn.mbdoge.jyx.web.encrypt;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
class EncodeResponseBodyAdviceTest {
    @Autowired
    private MockMvc mockMvc; //只需 autowire

    @Test
    void name() throws Exception {
        String contentAsString = mockMvc.perform(MockMvcRequestBuilders.post("/aaa"))
                .andDo(print())
                .andReturn().getResponse().getContentAsString();
//                .andExpect()

        System.out.println("contentAsString = " + contentAsString);
    }
}