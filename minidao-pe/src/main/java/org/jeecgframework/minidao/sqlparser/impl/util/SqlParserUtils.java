package org.jeecgframework.minidao.sqlparser.impl.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
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
    // 存储引用标记的特殊键
    private static final String QUOTED_INFO_KEY = "__MB__QUOTED_INFO__";
    // 分隔符
    private static final char ENTRY_SEP = ';';
    private static final char KV_SEP = '=';

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
        //update-begin---author:chenrui ---date:20251110  for：[issues/9000]Online报表（带参数）预览后台报错------------
        // 局部上下文: 引号标记
        Map<String, Boolean> quotedMap = new HashMap<>();
        while (m.find()) {
            String raw = m.group();
            String token = MB_PREFIX + customMaskKey + idx + MB_SUFFIX;
            int start = m.start();
            int end = m.end();
            // 跳过左右空白后判断
            int left = start - 1;
            while (left >= 0 && Character.isWhitespace(text.charAt(left))) { left--; }
            int right = end;
            while (right < text.length() && Character.isWhitespace(text.charAt(right))) { right++; }
            char leftChar = left >= 0 ? text.charAt(left) : '\0';
            char rightChar = right < text.length() ? text.charAt(right) : '\0';
            boolean alreadyQuoted = leftChar == '\'' && rightChar == '\'';
            // 回退检测紧邻字符
            boolean adjacentLeftQuote = start > 0 && text.charAt(start - 1) == '\'';
            boolean adjacentRightQuote = end < text.length() && text.charAt(end) == '\'';
            if (!alreadyQuoted && adjacentLeftQuote && adjacentRightQuote) {
                alreadyQuoted = true;
            }
            quotedMap.put(token, alreadyQuoted);
            tokenToRaw.put(token, raw);
            logger.debug("[Mybatis替换占位符] raw=" + raw + ", token=" + token + ", alreadyQuoted=" + alreadyQuoted);
            String replacement = alreadyQuoted ? token : ("'" + token + "'");
            m.appendReplacement(sb, Matcher.quoteReplacement(replacement));
            idx++;
        }
        m.appendTail(sb);
        String masked = sb.toString();
        // 规范化重复单引号 ''token'' -> 'token'
        masked = masked.replaceAll("''(__MB_PARAM_[^']+__)''", "'$1'");
        // 序列化 quotedMap
        if (!quotedMap.isEmpty()) {
            StringBuilder serial = new StringBuilder();
            for (Map.Entry<String, Boolean> e : quotedMap.entrySet()) {
                serial.append(e.getKey()).append(KV_SEP).append(e.getValue() ? '1' : '0').append(ENTRY_SEP);
            }
            tokenToRaw.put(QUOTED_INFO_KEY, serial.toString());
        }
        //update-end---author:chenrui ---date:20251110  for：[issues/9000]Online报表（带参数）预览后台报错------------
        logger.debug("[Mybatis替换占位符] 输出: " + masked);
        return masked;
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
        //update-begin---author:chenrui ---date:20251110  for：[issues/9000]Online报表（带参数）预览后台报错------------
        // 反序列化引号标记
        Map<String, Boolean> quotedMap = new HashMap<>();
        String quotedSerial = tokenToRaw.get(QUOTED_INFO_KEY);
        if (quotedSerial != null) {
            int pos = 0; int len = quotedSerial.length();
            while (pos < len) {
                int sep = quotedSerial.indexOf(ENTRY_SEP, pos);
                if (sep == -1) sep = len;
                String entry = quotedSerial.substring(pos, sep).trim();
                if (!entry.isEmpty()) {
                    int kv = entry.indexOf(KV_SEP);
                    if (kv > 0) {
                        String k = entry.substring(0, kv);
                        char v = entry.charAt(kv + 1);
                        quotedMap.put(k, v == '1');
                    }
                }
                pos = sep + 1;
            }
        }
        //update-end---author:chenrui ---date:20251110  for：[issues/9000]Online报表（带参数）预览后台报错------------
        String out = sql;
        for (Map.Entry<String, String> e : tokenToRaw.entrySet()) {
            //update-begin---author:chenrui ---date:20251110  for：[issues/9000]Online报表（带参数）预览后台报错------------
            String token = e.getKey();
            if (QUOTED_INFO_KEY.equals(token)) { // 跳过序列化键
                continue;
            }
            String raw = e.getValue();
            // 仅处理真实占位符原始值 (#{} 或 ${})
            if (!MB_PLACEHOLDER.matcher(raw).matches()) {
                continue;
            }
            Boolean wasQuoted = quotedMap.get(token);
            if (wasQuoted != null && wasQuoted) {
                // 原本有引号: 保留引号
                out = out.replace("'" + token + "'", "'" + raw + "'");
            } else {
                // 原本无引号: 去掉我们添加的引号
                out = out.replace("'" + token + "'", raw);
            }
            // 非引号形态（极少数拼接场景）
            out = out.replace(token, raw);
            //update-end---author:chenrui ---date:20251110  for：[issues/9000]Online报表（带参数）预览后台报错------------
        }
        logger.debug("[Mybatis占位符还原] restoreMyBatisPlaceholders result: " + out);
        return out;
    }
}
