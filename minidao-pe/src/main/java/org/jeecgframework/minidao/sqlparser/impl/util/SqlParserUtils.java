package org.jeecgframework.minidao.sqlparser.impl.util;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description: sql解析公共方法
 * @Author: chenrui
 * @Date: 2025/8/15 09:49
 */
public class SqlParserUtils {

    /**
     * MyBatis占位符正则
     */
    public static final Pattern MB_PLACEHOLDER = Pattern.compile("(#\\{[^}]+})|($\\{[^}]+})");
    /**
     * sql替换占位符前缀
     */
    public static final String MB_PREFIX = "__MB_PARAM_";
    /**
     * sql替换占位符后缀
     */
    public static final String MB_SUFFIX = "__";

    /**
     * 将 #{..}/${..} 替换为占位
     * @param text
     * @param tokenToRaw
     * @return
     * @author chenrui
     * @date 2025/8/14 20:32
     */
    public static String maskMyBatisPlaceholders(String text, Map<String, String> tokenToRaw) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        Matcher m = MB_PLACEHOLDER.matcher(text);
        StringBuffer sb = new StringBuffer();
        int idx = 0;
        while (m.find()) {
            String raw = m.group();
            String token = MB_PREFIX + idx + MB_SUFFIX;
            tokenToRaw.put(token, raw);
            m.appendReplacement(sb, Matcher.quoteReplacement("'" + token + "'"));
            idx++;
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     * 占位符还原：将带引号的占位换回原始 #{..}/${..}
     * @param sql
     * @param tokenToRaw
     * @return
     * @author chenrui
     * @date 2025/8/14 20:31
     */
    public static String restoreMyBatisPlaceholders(String sql, Map<String, String> tokenToRaw) {
        if (sql == null || tokenToRaw == null || tokenToRaw.isEmpty()) {
            return sql;
        }
        String out = sql;
        for (Map.Entry<String, String> e : tokenToRaw.entrySet()) {
            out = out.replace("'" + e.getKey() + "'", e.getValue());
        }
        return out;
    }
}
