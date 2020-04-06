package cn.mbdoge.jyx.web;

import cn.mbdoge.jyx.exception.LocalServiceException;
import cn.mbdoge.jyx.web.handler.ControllerHandlerAdviceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.MessageSourceAccessor;

import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication()
@Controller
@Configuration
public class Application implements CommandLineRunner {


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    //    @Bean
//    public ConfigurableServletWebServerFactory webServerFactory() {
//        return new WebServerFactory();
//    }
//
    @Override
    public void run(String... args) throws Exception {
    }


    @Autowired
    private MessageSourceAccessor messageSourceAccessor;

    @GetMapping("/language")
    @ResponseBody
    public Map language(HttpServletRequest request, @RequestParam(name = "code", required = false, defaultValue = "upload.fail") String code){
        String header = request.getHeader("Accept-Language");
        String message = messageSourceAccessor.getMessage(code, "多语言测试");
        Map m = new HashMap();
        m.put("lang", header);
        m.put("msg", message);
        return m;
    }

    @GetMapping("/")
    public String index(){
        System.out.println("hello world");
        return "index.html";
    }

    @GetMapping("/json")
    @ResponseBody
    public Map input(String word){
        String s = "aaa";
        Map m = new HashMap();
        m.put("a", "a");
        m.put("s", s);
        m.put("d", "d");
        return m;
    }

    /**
     *
     * @see cn.mbdoge.jyx.web.handler.ControllerHandlerAdviceTest#handleMissingServletRequestParameterException()
     * @return
     */
    @GetMapping("/input2")
    @ResponseBody
    public Map input2(@RequestParam(name = "test") String test){
        String s = "aaa";
        Map m = new HashMap();
        m.put("a", "a");
        m.put("s", s);
        m.put("d", test);
        return m;
    }

    /**
     *
     * @see cn.mbdoge.jyx.web.handler.ControllerHandlerAdviceTest#handleHttpMessageNotReadableException()
     * @see cn.mbdoge.jyx.web.handler.ControllerHandlerAdvice#handleHttpMessageNotReadableException(HttpMessageNotReadableException)
     * @return
     */
    @PostMapping("/input3")
    @ResponseBody
    public Map input3(@RequestBody A a) throws IOException {
        String s = "aaa";
        Map m = new HashMap();
        m.put("d", a);
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.writeValueAsString(null);
//        throw new IOException("xx");
        return m;
    }




    /**
     * 测试ResponseBody
     */
    public static class A {
        private String c;

        public String getC() {
            return c;
        }

        public void setC(String c) {
            this.c = c;
        }
    }

    /**
     * 验证参数错误
     * @param b
     * @return
     * @see cn.mbdoge.jyx.web.handler.ControllerHandlerAdvice#handleMethodArgumentNotValidException
     * @see ControllerHandlerAdviceTest#handleMethodArgumentNotValidException()
     */
    @PostMapping("/input4")
    @ResponseBody
    public Map input4(@Validated @RequestBody B b)  {
        Map m = new HashMap();
        m.put("d", b);
        return m;
    }

    /**
     * 验证参数错误
     * @param b
     * @return
     * @see cn.mbdoge.jyx.web.handler.ControllerHandlerAdvice#handleBindException(BindException)
     * @see ControllerHandlerAdviceTest#handleBindException()
     */
    @PostMapping("/input5")
    @ResponseBody
    public Map input5(@Validated B b)  {
        Map m = new HashMap();
        m.put("d", b);
        return m;
    }


    @PostMapping("/input6")
    @ResponseBody
    public Map input6(@RequestParam("a") int a, boolean c, M m)  {
        Map ma = new HashMap();
        ma.put("d", a);
        ma.put("m", m);
//        m.put("b", b);
        return ma;
    }

    @PostMapping(value = "/input7", consumes = {"application/x-www-form-urlencoded"})
    @ResponseBody
    public Map input7() throws IOException {
        Map ma = new HashMap();
        return ma;
    }

    @RequestMapping(value = "/input8", method = RequestMethod.POST)
    @ResponseBody
    public String input8(
            HttpServletRequest request,
            @RequestParam(value = "data", required = false) List<MultipartFile> files) {
//        System.out.println("language = " + language);
        System.out.println("request.getHeader(\"content-length\") = " + request.getHeader("Content-Length"));
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()){
            String s = headerNames.nextElement();
            System.out.println("headerNames = " + s);
            System.out.println("request = " + request.getHeader(s));
        }
        for (MultipartFile file : files) {
            System.out.println(file.getOriginalFilename());
            System.out.println("file.getSize() = " + file.getSize());
        }
        return "success";
    }

    @PostMapping("/input11")
    @ResponseBody
    public Map input11() throws IOException {
        throw new LocalServiceException("upload.fail");
    }

    @PostMapping("/input12")
    @ResponseBody
    public Map input12() throws IOException {

        throw new IOException("xxx");
    }

    /**
     * 测试参数验证
     */
    public static class B {
        @Min(value = 5)
        private int value;
        private boolean flag;
        private String msg;
        @DecimalMin(value = "2.0", message = "")
        private double vv;
        private M m;
        private List<M> ms;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public boolean isFlag() {
            return flag;
        }

        public void setFlag(boolean flag) {
            this.flag = flag;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public double getVv() {
            return vv;
        }

        public void setVv(double vv) {
            this.vv = vv;
        }

        public M getM() {
            return m;
        }

        public void setM(M m) {
            this.m = m;
        }

        public List<M> getMs() {
            return ms;
        }

        public void setMs(List<M> ms) {
            this.ms = ms;
        }
    }

    @Controller
    @Validated
    public static class CC {

        @PostMapping("/input10")
        @ResponseBody
        public Map input10(@RequestParam("a") @Min(5) int a) throws IOException {
            Map m = new HashMap();
            m.put("d", a);
            return m;
        }
    }

    public enum M {
        open,
        close
    }
}