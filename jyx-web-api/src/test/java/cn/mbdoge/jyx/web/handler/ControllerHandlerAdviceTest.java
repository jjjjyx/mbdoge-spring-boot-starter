package cn.mbdoge.jyx.web.handler;


import cn.mbdoge.jyx.exception.LocalServiceException;
import cn.mbdoge.jyx.web.Application;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.result.ModelResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.annotation.Resource;
import javax.validation.ConstraintViolationException;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        properties = {
                "logging.level.cn.mbdoge.jyx.web.handler=trace",
                "mbdoge.api.encrypt.enabled=false"
        }
)
@AutoConfigureMockMvc
public class ControllerHandlerAdviceTest {
    @Autowired
    private MockMvc mockMvc; //只需 autowire

    @Autowired
    private MessageSourceAccessor messageSourceAccessor;

    @BeforeEach
    void setUp() {
    }

    /**
     * 测试 404 错误
     * @throws Exception
     */
    @Test
    @DisplayName("测试 404 错误")
    public void noHandlerFoundException() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/aaa"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.level").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());

    }

    /**
     * 测试 i18n
     * @throws Exception
     */
    @Test
    @DisplayName("测试 404 错误 i18n")
    public void languageTest() throws Exception {

        String message = messageSourceAccessor.getMessage("controller.404", Locale.SIMPLIFIED_CHINESE);

        mockMvc.perform(MockMvcRequestBuilders.post("/aaa")
                .header("Accept-Language", "zh-CN"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value(message));

        message = messageSourceAccessor.getMessage("controller.404", Locale.US);

        mockMvc.perform(MockMvcRequestBuilders.post("/aaa")
                .header("Accept-Language", "en-US"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value(message));
    }

    /**
     * 测试缺少参数错误
     * @throws Exception
     * @see cn.mbdoge.jyx.web.Application#input2(String)
     */
    @Test
    @DisplayName("缺少参数错误")
    public void handleMissingServletRequestParameterException() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder =
                MockMvcRequestBuilders
                .get("/input2");

        MvcResult mvcResult = mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.level").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").isString())
                .andReturn();

        Exception resolvedException = mvcResult.getResolvedException();
        Assertions.assertTrue(resolvedException instanceof MissingServletRequestParameterException, "缺少参数");


        mockHttpServletRequestBuilder.param("test", "xxx");
        MvcResult xxx = mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.d").value("xxx"))
                .andReturn();
        Assertions.assertNull(xxx.getResolvedException());
    }

    /**
     *
     * @throws Exception
     * @see cn.mbdoge.jyx.web.Application#input3(cn.mbdoge.jyx.web.Application.A)
     */
    @Test
    @DisplayName("参数验证失败 HttpMessageNotReadable")
    public void handleHttpMessageNotReadableException() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder =
                MockMvcRequestBuilders
                        .post("/input3")
                        .param("value", "10");
//        MappingJackson2HttpMessageConverter
        ResultActions perform = mockMvc.perform(mockHttpServletRequestBuilder);

        MvcResult mvcResult = encode(perform)
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.level").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").isString())
                .andReturn();

        Exception resolvedException = mvcResult.getResolvedException();
        Assertions.assertTrue(resolvedException instanceof HttpMessageNotReadableException, "参数验证失败");


        mockHttpServletRequestBuilder =
                MockMvcRequestBuilders
                        .post("/input3")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"c\": \"dd\"}");

        perform = mockMvc.perform(mockHttpServletRequestBuilder);

        MvcResult dd = encode(perform)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.d.c").value("dd"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.d.a").doesNotExist())
                .andReturn();
        Assertions.assertNull(dd.getResolvedException());
    }

    /**
     * @see cn.mbdoge.jyx.web.Application#input4(cn.mbdoge.jyx.web.Application.B)
     * @throws Exception
     */
    @Test
    @DisplayName("参数验证失败 MethodArgumentNotValid")
    public void handleMethodArgumentNotValidException() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder =
                MockMvcRequestBuilders
                        .post("/input4")
                        .header("Accept-Language", "zh-CN")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"value\": \"0\"}");

        ResultActions perform = mockMvc.perform(mockHttpServletRequestBuilder);

        MvcResult mvcResult = encode(perform)
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.level").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").isString())
                .andReturn();

        Assertions.assertTrue(mvcResult.getResolvedException() instanceof MethodArgumentNotValidException, "参数验证失败");


        mockHttpServletRequestBuilder =
                MockMvcRequestBuilders
                        .post("/input4")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"value\": \"6\", \"vv\": \"6\"}");

        perform = mockMvc.perform(mockHttpServletRequestBuilder);

        MvcResult mvcResult1 = encode(perform)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.d.value").value(6))
                .andReturn();

        Assertions.assertNull(mvcResult1.getResolvedException());
    }

    /**
     * @see cn.mbdoge.jyx.web.Application#input5(cn.mbdoge.jyx.web.Application.B)
     * @throws Exception
     */
    @Test
    @DisplayName("参数验证失败 BindException")
    public void handleBindException() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder =
                MockMvcRequestBuilders
                        .post("/input5")
                        .param("value", "9")
                        .param("vv", "1.5");

        ResultActions perform = mockMvc.perform(mockHttpServletRequestBuilder);

        MvcResult mvcResult = encode(perform)
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.level").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").isString())
                .andReturn();

        Assertions.assertTrue(mvcResult.getResolvedException() instanceof BindException, "参数验证失败");


        mockHttpServletRequestBuilder =
                MockMvcRequestBuilders
                        .post("/input5")
                        .param("value", "6")
                        .param("vv", "3.2");

        perform = mockMvc.perform(mockHttpServletRequestBuilder);

        MvcResult mvcResult1 = encode(perform)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.d.value").value(6))
                .andExpect(MockMvcResultMatchers.jsonPath("$.d.vv").value(3.2))
                .andReturn();

        Assertions.assertNull(mvcResult1.getResolvedException());
    }

    /**
     * @see cn.mbdoge.jyx.web.Application#input6(int, boolean, Application.M)
     * @throws Exception
     */
    @Test
    @DisplayName("参数类型错误 MethodArgumentTypeMismatch")
    public void handleMethodArgumentTypeMismatchException() throws Exception {

        MockHttpServletRequestBuilder mockHttpServletRequestBuilder =
                MockMvcRequestBuilders
                        .post("/input6")
//                        .contentType(MediaType.APPLICATION_JSON).content("{\"value\": \"aaa\"}");
                        .param("a", "a")
                        .param("vv", "4.0")
                        .param("m", "aa");

        ResultActions perform = mockMvc.perform(mockHttpServletRequestBuilder);

        MvcResult mvcResult = encode(perform).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.level").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").isString())
                .andReturn();

        Assertions.assertTrue(mvcResult.getResolvedException() instanceof MethodArgumentTypeMismatchException, "参数类型不正确");


        mockHttpServletRequestBuilder =
                MockMvcRequestBuilders
                        .post("/input6")
//                        .contentType(MediaType.APPLICATION_JSON).content("{\"value\": \"aaa\"}");
                        .param("a", "1")
                        .param("c", "4.0")
                        .param("m", "aa");

        perform = mockMvc.perform(mockHttpServletRequestBuilder);

        mvcResult = encode(perform).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.level").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").isString())
                .andReturn();

        Assertions.assertTrue(mvcResult.getResolvedException() instanceof MethodArgumentTypeMismatchException, "参数类型不正确");

        mockHttpServletRequestBuilder =
                MockMvcRequestBuilders
                        .post("/input6")
//                        .contentType(MediaType.APPLICATION_JSON).content("{\"value\": \"aaa\"}");
                        .param("a", "1")
                        .param("c", "false")
                        .param("m", "aa");

        perform = mockMvc.perform(mockHttpServletRequestBuilder);

        mvcResult = encode(perform).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.level").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").isString())
                .andReturn();

        Assertions.assertTrue(mvcResult.getResolvedException() instanceof MethodArgumentTypeMismatchException, "参数类型不正确");
    }



    /**
     * @see Application.CC#input10
     * @throws Exception
     */
    @Test
    @DisplayName("参数验证失败 ConstraintViolation")
    public void handleConstraintViolationException() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder =
                MockMvcRequestBuilders
                        .post("/input10")
                        .param("a", "1");
//                        .contentType(MediaType.APPLICATION_JSON).content("{\"value\": \"5\"}");


        ResultActions perform = mockMvc.perform(mockHttpServletRequestBuilder);

        MvcResult mvcResult = encode(perform).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.level").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").isString())
                .andReturn();

        Assertions.assertTrue(mvcResult.getResolvedException() instanceof ConstraintViolationException, "参数验证失败");

        mockHttpServletRequestBuilder =
                MockMvcRequestBuilders
                        .post("/input10")
                        .param("a", "8");
//                        .contentType(MediaType.APPLICATION_JSON).content("{\"value\": \"5\"}");


        perform = mockMvc.perform(mockHttpServletRequestBuilder);

        mvcResult = encode(perform).andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.d").value(8))
                .andReturn();

        Assertions.assertNull(mvcResult.getResolvedException(), "参数验证失败");
    }

    /**
     * 请求方式 错误
     * @throws Exception
     */
    @Test
    @DisplayName("请求方式 错误")
    public void handleHttpRequestMethodNotSupportedException() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder =
                MockMvcRequestBuilders
                        .get("/input11");
//                        .contentType(MediaType.APPLICATION_JSON).content("{\"value\": \"aaa\"}");

        ResultActions perform = mockMvc.perform(mockHttpServletRequestBuilder);

        MvcResult mvcResult = encode(perform).andDo(print())
                .andExpect(status().isMethodNotAllowed())
                .andExpect(MockMvcResultMatchers.jsonPath("$.level").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").isString())
                .andReturn();

        Assertions.assertTrue(mvcResult.getResolvedException() instanceof HttpRequestMethodNotSupportedException, "请求方式不正确");
    }

    @Test
    @DisplayName("请求格式错误 HttpMediaTypeNotSupported")
    public void handleHttpMediaTypeNotSupportedException() throws Exception {

        MockHttpServletRequestBuilder mockHttpServletRequestBuilder =
                MockMvcRequestBuilders
                        .post("/input7")
                        .contentType(MediaType.APPLICATION_CBOR_VALUE);

        ResultActions perform = mockMvc.perform(mockHttpServletRequestBuilder);

        MvcResult mvcResult = encode(perform).andDo(print())
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(MockMvcResultMatchers.jsonPath("$.level").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").isString())
                .andReturn();

        Assertions.assertTrue(mvcResult.getResolvedException() instanceof HttpMediaTypeNotSupportedException, "请求体格式不正确");

        mockHttpServletRequestBuilder =
                MockMvcRequestBuilders
                        .post("/input7")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        perform = mockMvc.perform(mockHttpServletRequestBuilder);

        mvcResult = encode(perform).andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertNull(mvcResult.getResolvedException(), "请求体格式正确");
    }

    @Test
    @DisplayName("上传文件")
    public void handleMultipartException() throws Exception {
        MockMultipartFile firstFile = new MockMultipartFile("data", "filename.txt", "text/plain", "some xml".getBytes());
        MockMultipartFile secondFile = new MockMultipartFile("data", "other-file-name.data", "text/plain", "some other type".getBytes());



        mockMvc.perform(MockMvcRequestBuilders.multipart("/input8")
                .file(firstFile)
                .file(secondFile))
                .andExpect(status().is(200))
                .andExpect(MockMvcResultMatchers.content().string("success"));

        byte[] bytes = new byte[1048576 * 2000];

        // 300MB
        Arrays.fill(bytes, (byte) 1);

        MockMultipartFile bigFile = new MockMultipartFile("data", "big.data", "text/plain", bytes);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/input8")
                .file(firstFile)
                .file(secondFile)
                .file(bigFile)
        .header("Content-Length", bytes.length))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(MockMvcResultMatchers.content().string("success"));
    }

    @Test
    @DisplayName("localService 自定义业务异常")
    public void handleServiceException() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder =
                MockMvcRequestBuilders
                        .post("/input11");
//                        .contentType(MediaType.APPLICATION_JSON).content("{\"value\": \"aaa\"}");

        ResultActions perform = mockMvc.perform(mockHttpServletRequestBuilder);

        MvcResult mvcResult = encode(perform).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.level").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").isString())
                .andReturn();

        Assertions.assertTrue(mvcResult.getResolvedException() instanceof LocalServiceException, "业务异常");
    }

    @Test
    @DisplayName("其他异常 500")
    public void handleException() throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder =
                MockMvcRequestBuilders
                        .post("/input12");
//                        .contentType(MediaType.APPLICATION_JSON).content("{\"value\": \"aaa\"}");

        ResultActions perform = mockMvc.perform(mockHttpServletRequestBuilder);

        MvcResult mvcResult = encode(perform).andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.level").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").isString())
                .andReturn();

        Assertions.assertTrue(mvcResult.getResolvedException() != null, "参数类型不正确");
    }


    private ResultActions encode (ResultActions perform) {
        perform.andReturn().getResponse().setCharacterEncoding("UTF-8");
        return perform;
    }
}