package cn.mbdoge.jyx.web.language;

import org.springframework.util.StringUtils;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * @author jyx
 */
public class SmartLocaleResolver extends AcceptHeaderLocaleResolver {
    private static final String languageHeaderKey = "Accept-Language";
    private final List<Locale> locales;

    public SmartLocaleResolver() {
        this(Arrays.asList(
                Locale.US,
                Locale.SIMPLIFIED_CHINESE
//                Locale.ENGLISH,
//                Locale.CHINESE
//            Locale.TRADITIONAL_CHINESE,
//            Locale.JAPANESE
        ));
    }

    public SmartLocaleResolver(List<Locale> locales) {
        this.locales = locales;
    }

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        if (StringUtils.isEmpty(request.getHeader(languageHeaderKey))) {
            return super.resolveLocale(request);
        }
        // 转换一下其他语言到仅提供的语言
        // @see https://stackoverflow.com/questions/36655104/spring-boot-localization-issue-accept-language-header
        List<Locale.LanguageRange> list = Locale.LanguageRange.parse(request.getHeader(languageHeaderKey));

        return Locale.lookup(list, locales);
    }
}
