package cn.mbdoge.jyx.web.language;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Locale;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest(
        properties = {
                "logging.level.cn.mbdoge.jyx.web.handler=trace",
                "mbdoge.api.encrypt.enabled=false"
        }
)
@AutoConfigureMockMvc
public class LocalTest {

    @Autowired
    private MockMvc mockMvc;
    //只需 autowire

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private MessageSourceAccessor messageSourceAccessor;

    @Test
    @DisplayName("测试bean")
    void name() {
        String message1 = messageSource.getMessage("upload.fail", null, Locale.US);
        Assertions.assertEquals("upload failed", message1);

        String message = messageSourceAccessor.getMessage("upload.fail");
        Assertions.assertEquals("上传失败", message);

        Assertions.assertThrows(NoSuchMessageException.class, () -> {
            messageSourceAccessor.getMessage("upload.fail2222");
        });
    }

    @Test
    @DisplayName("请求中携带 Language")
    void name1() throws Exception {
        String message = messageSourceAccessor.getMessage("upload.fail", Locale.US);

        mockMvc.perform(MockMvcRequestBuilders.get("/language")
                .header("Accept-Language", "en-US")
        )
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value(message));
    }


    @Test
    @DisplayName("请求中未携带 Language")
    void name2() throws Exception {
        // 默认是中文
        String message = messageSourceAccessor.getMessage("upload.fail", Locale.CHINESE);

        mockMvc.perform(MockMvcRequestBuilders.get("/language"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value(message));
    }

    @Test
    @DisplayName("请求中未携带错误的Language")
    void name3() throws Exception {
        // 默认是中文
        String message = messageSourceAccessor.getMessage("upload.fail", Locale.CHINESE);

        mockMvc.perform(MockMvcRequestBuilders.get("/language")
                .header("Accept-Language", "en")
        )
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value(message));
    }
}
