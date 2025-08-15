package org.jeecgframework.minidao.util;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * sql注入校验工具类
 * @date 2025-08-15
 * @for: DaoFormat.in存在SQL注入风险
 * @author scott
 */
public class SqlInjectionUtil {
    private static final Log log = LogFactory.getLog(SqlInjectionUtil.class);

    /**
     * 默认—sql注入关键词
     */
    private final static String XSS_STR = "and |exec |peformance_schema|information_schema|extractvalue|updatexml|geohash|gtid_subset|gtid_subtract|insert |select |delete |update |drop |count |chr |mid |master |truncate |char |declare |;|or |+|--";

    /**
     * sql注入风险的 正则关键字
     * <p>
     * 函数匹配，需要用正则模式
     */
    private final static String[] XSS_REGULAR_STR_ARRAY = new String[]{
            "chr\\s*\\(",
            "mid\\s*\\(",
            " char\\s*\\(",
            "sleep\\s*\\(",
            "user\\s*\\(",
            "show\\s+tables",
            "user[\\s]*\\([\\s]*\\)",
            "show\\s+databases",
            "sleep\\(\\d*\\)",
            "sleep\\(.*\\)",
    };
    /**
     * sql注释的正则
     */
    private final static Pattern SQL_ANNOTATION = Pattern.compile("/\\*[\\s\\S]*\\*/");
    private final static String SQL_ANNOTATION2 = "--";

    /**
     * sql注入提示语
     */
    private final static String SQL_INJECTION_KEYWORD_TIP = "请注意，存在SQL注入关键词---> ";
    private final static String SQL_INJECTION_TIP = "请注意，值可能存在SQL注入风险!--->";
    private final static String SQL_INJECTION_TIP_VARIABLE = "请注意，值可能存在SQL注入风险!---> ";


    /**
     * 校验比较严格
     * <p>
     * sql注入过滤处理，遇到注入关键字抛异常
     *
     * @param value
     * @return
     */
    public static void check(String value, String customXssString) {
        if (value == null || "".equals(value)) {
            return;
        }
        // 一、校验sql注释 不允许有sql注释
        checkSqlAnnotation(value);
        // 转为小写进行后续比较
        value = value.toLowerCase().trim();

        // 二、SQL注入检测存在绕过风险 (普通文本校验)
        //https://gitee.com/jeecg/jeecg-boot/issues/I4NZGE
        String[] xssArr = XSS_STR.split("\\|");
        for (int i = 0; i < xssArr.length; i++) {
            if (value.indexOf(xssArr[i]) > -1) {
                log.error(SqlInjectionUtil.SQL_INJECTION_KEYWORD_TIP + xssArr[i]);
                log.error(SqlInjectionUtil.SQL_INJECTION_TIP_VARIABLE + value);
                throw new RuntimeException(SqlInjectionUtil.SQL_INJECTION_TIP + value);
            }
        }
        // 三、SQL注入检测存在绕过风险 (自定义传入普通文本校验)
        if (customXssString != null) {
            String[] xssArr2 = customXssString.split("\\|");
            for (int i = 0; i < xssArr2.length; i++) {
                if (value.indexOf(xssArr2[i]) > -1) {
                    log.error(SqlInjectionUtil.SQL_INJECTION_KEYWORD_TIP + xssArr2[i]);
                    log.error(SqlInjectionUtil.SQL_INJECTION_TIP_VARIABLE + value);
                    throw new RuntimeException(SqlInjectionUtil.SQL_INJECTION_TIP + value);
                }
            }
        }

        // 四、SQL注入检测存在绕过风险 (正则校验)
        for (String regularOriginal : XSS_REGULAR_STR_ARRAY) {
            String regular = ".*" + regularOriginal + ".*";
            if (Pattern.matches(regular, value)) {
                log.error(SqlInjectionUtil.SQL_INJECTION_KEYWORD_TIP + regularOriginal);
                log.error(SqlInjectionUtil.SQL_INJECTION_TIP_VARIABLE + value);
                throw new RuntimeException(SqlInjectionUtil.SQL_INJECTION_TIP + value);
            }
        }
        return;
    }

    /**
     * 校验是否有sql注释
     *
     * @return
     */
    public static void checkSqlAnnotation(String str) {
        if (str.contains(SQL_ANNOTATION2)) {
            String error = "请注意，SQL中不允许含注释，有安全风险---> "+ SQL_ANNOTATION2;
            log.error(error);
            throw new RuntimeException(error);
        }

        Matcher matcher = SQL_ANNOTATION.matcher(str);
        if (matcher.find()) {
            String error = "请注意，值可能存在SQL注入风险---> \\*.*\\";
            log.error(error);
            throw new RuntimeException(error);
        }
    }

}
