package org.jeecgframework.minidao.sqlparser.impl.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description: sql解析公共方法
 * @Author: chenrui
 * @Date: 2025/8/15 09:49
 */
public class SqlParserUtils {
    private static final Log logger = LogFactory.getLog(SqlParserUtils.class);
    
    /**
     * MyBatis占位符正则
     */
    public static final Pattern MB_PLACEHOLDER = Pattern.compile("(#\\{[^}]+})|(\\$\\{[^}]+})");
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
    public static String maskMyBatisPlaceholders(String text, Map<String, String> tokenToRaw){
        return maskMyBatisPlaceholders(text, tokenToRaw, "");
    }

    /**
     * 将 #{..}/${..} 替换为占位
     * @param text
     * @param tokenToRaw
     * @param customMaskKey 自定义占位符前缀
     * @return
     * @author chenrui
     * @date 2025/8/14 20:32
     */
    public static String maskMyBatisPlaceholders(String text, Map<String, String> tokenToRaw, String customMaskKey) {
        logger.debug("[Mybatis替换占位符] maskMyBatisPlaceholders: " + text);
        // 如果没有传入占位符映射，则直接返回原文本
        if (text == null || text.isEmpty()) {
            return text;
        }
        if(customMaskKey == null || customMaskKey.isEmpty()) {
            customMaskKey = "";
        }
        Matcher m = MB_PLACEHOLDER.matcher(text);
        StringBuffer sb = new StringBuffer();
        int idx = 0;
        while (m.find()) {
            String raw = m.group();
            String token = MB_PREFIX + customMaskKey + idx + MB_SUFFIX;
            tokenToRaw.put(token, raw);
            m.appendReplacement(sb, Matcher.quoteReplacement("'" + token + "'"));
            idx++;
        }
        m.appendTail(sb);
        logger.debug("[Mybatis替换占位符] maskMyBatisPlaceholders result: " + sb.toString());
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
        logger.debug("[Mybatis占位符还原] restoreMyBatisPlaceholders: " + sql);
        if (sql == null || tokenToRaw == null || tokenToRaw.isEmpty()) {
            return sql;
        }
        String out = sql;
        for (Map.Entry<String, String> e : tokenToRaw.entrySet()) {
            out = out.replace("'" + e.getKey() + "'", e.getValue());
        }
        logger.debug("[Mybatis占位符还原] restoreMyBatisPlaceholders result: " + out);
        return out;
    }
}
