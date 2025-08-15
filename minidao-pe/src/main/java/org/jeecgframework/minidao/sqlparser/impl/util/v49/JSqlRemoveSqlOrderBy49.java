package org.jeecgframework.minidao.sqlparser.impl.util.v49;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jeecgframework.minidao.sqlparser.impl.util.SqlParserUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * sqlserver工具类，去掉order by
 */
public class JSqlRemoveSqlOrderBy49 {
    private static final Log logger = LogFactory.getLog(JSqlRemoveSqlOrderBy49.class);
    /**
     * 匹配:user.name这样的参数表达式
     */
    public static Pattern dynamic = Pattern.compile(":[ tnx0Bfr]*[0-9a-z.A-Z_]+");
    public static String DIAN = ".";
    public static String DIAN_TMP = "@@@";

    /**
     * SQLServer去除子查询中的order by
     *
     * @param sql
     * @return
     * @throws JSQLParserException
     */
    public String removeOrderBy(String sql) throws JSQLParserException {
        Statement stmt = null;
        List<String> sqList = null;
        //---------------------------------------------------------------------------------------------
        // 如果包含mybatis变量，先将其替换为占位符，避免解析时出错
        Map<String, String> mbMap = new LinkedHashMap<>();
        sql = SqlParserUtils.maskMyBatisPlaceholders(sql, mbMap);
        //---------------------------------------------------------------------------------------------
        try {
            //update-begin---author:wangshuai ---date:20220215  for：[issues/I4STNJ]SQL Server表名关键字查询失败
            stmt = CCJSqlParserUtil.parse(sql, parser -> parser.withSquareBracketQuotation(true));
            //update-end---author:wangshuai ---date:20220215  for：[issues/I4STNJ]SQL Server表名关键字查询失败
        } catch (JSQLParserException e) {
            //---------------------------------------------------------------------------------------------
            //如果是 :user.name 类似含点的表达式，特殊处理下sql再解析
            if (e.toString().indexOf(DIAN) != -1) {
                sqList = getKeyListByContent(sql);
                for (String s : sqList) {
                    sql = sql.replace(s, s.replace(DIAN, DIAN_TMP));
                }
                stmt = CCJSqlParserUtil.parse(sql, parser -> parser.withSquareBracketQuotation(true));
            } else {
                e.printStackTrace();
            }
            logger.debug(" --- JSQLParser with DIAN --- convert begin sql=" + sql);
            //---------------------------------------------------------------------------------------------
        }
        Select select = (Select) stmt;
        processSelectBody(select);
        String returnSql = select.toString();

        //---------------------------------------------------------------------------------------------
        //如果是 :user.name 类似含点的表达式，特殊处理下sql再解析
        if (sqList != null) {
            for (String s : sqList) {
                returnSql = returnSql.replace(s.replace(DIAN, DIAN_TMP), s.replace(DIAN_TMP, DIAN));
            }
            logger.debug(" --- JSQLParser with DIAN --- convert end sql=" + sql);
        }
        //---------------------------------------------------------------------------------------------
        //---------------------------------------------------------------------------------------------
        // 如果包含mybatis变量，恢复占位符
        returnSql = SqlParserUtils.restoreMyBatisPlaceholders(returnSql, mbMap);
        //---------------------------------------------------------------------------------------------
        return returnSql;
    }

    /**
     * 按照动态内容的参数出现顺序,将参数放到List中
     *
     * @param content
     * @return
     */
    public static List<String> getKeyListByContent(String content) {
        Set<String> paramSet = new LinkedHashSet<>();
        Matcher m = dynamic.matcher(content);
        while (m.find()) {
            if (m.group() != null && m.group().indexOf(DIAN) != -1) {
                paramSet.add(m.group());
            }
        }
        return new ArrayList<>(paramSet);
    }

    public void processSelectBody(Select selectBody) {
        if (selectBody instanceof PlainSelect) {
            processPlainSelect((PlainSelect) selectBody);
        } else if (selectBody instanceof WithItem) {
            WithItem withItem = (WithItem) selectBody;
            if (JSqlSubSelectBody49.getItemSelectBody(withItem) != null) {
                processSelectBody(JSqlSubSelectBody49.getItemSelectBody(withItem));
            }
        } else {
            SetOperationList operationList = (SetOperationList) selectBody;
            if (operationList.getSelects() != null && operationList.getSelects().size() > 0) {
                List<Select> optSelects = operationList.getSelects();
                for (Select optSelect : optSelects) {
                    if (optSelect instanceof PlainSelect) {
                        processPlainSelect((PlainSelect) optSelect);
                    } else if (optSelect instanceof WithItem) {
                        WithItem withItem = (WithItem) optSelect;
                        processSelectBody(withItem.getPlainSelect());
                    }
                }
            }
            if (!orderByHashParameters(operationList.getOrderByElements())) {
                operationList.setOrderByElements(null);
            }
        }
    }

    public void processPlainSelect(PlainSelect plainSelect) {
        if (!orderByHashParameters(plainSelect.getOrderByElements())) {
            plainSelect.setOrderByElements(null);
        }
        if (plainSelect.getFromItem() != null) {
            processFromItem(plainSelect.getFromItem());
        }
        if (plainSelect.getJoins() != null && !plainSelect.getJoins().isEmpty()) {
            List<Join> joins = plainSelect.getJoins();
            for (Join join : joins) {
                if (join.getRightItem() != null) {
                    processFromItem(join.getRightItem());
                }
            }
        }
    }

    public void processFromItem(FromItem fromItem) {
        if (fromItem instanceof ParenthesedFromItem) {
            ParenthesedFromItem pFromItem = (ParenthesedFromItem) fromItem;
            if ((pFromItem.getJoins() != null && !pFromItem.getJoins().isEmpty())) {
                ParenthesedFromItem subJoin = (ParenthesedFromItem) fromItem;
                for (Join join : subJoin.getJoins()) {
                    if (join.getRightItem() != null) {
                        processFromItem(join.getRightItem());
                    }
                    if (join.isLeft()) {
                        processFromItem(subJoin);
                    }
                }
            }
        } else if (fromItem instanceof LateralSubSelect) {
            LateralSubSelect lateralSubSelect = (LateralSubSelect) fromItem;
            if (lateralSubSelect.getSelect() != null) {
                Select subSelect = lateralSubSelect.getSelect();
                processSelectBody(subSelect);
            }
        } else if (fromItem instanceof ParenthesedSelect) {
            ParenthesedSelect pFromItem = (ParenthesedSelect) fromItem;
            PlainSelect subSelect = pFromItem.getPlainSelect();
            processSelectBody(subSelect);
        }
    }

    public boolean orderByHashParameters(List<OrderByElement> orderByElements) {
        if (orderByElements == null) {
            return false;
        }
        for (OrderByElement orderByElement : orderByElements) {
            if (orderByElement.toString().toUpperCase().contains("?")) {
                return true;
            }
        }
        return false;
    }

//    public static void main(String[] args) {
//        String sql1 = "select * from (select s.username,s.create_time,s.realname,jr.create_by,jr.`name` from sys_user s INNER JOIN jimu_report jr on s.username = jr.create_by where jr.type='chartinfo'  ORDER BY jr.create_time)a";
//        String sql2 = "select * from sys_user ORDER BY create_time,a,c desc";
//        String sql3 = "SELECT cf.DB_FIELD_NAME,cf.DB_FIELD_TXT FROM ONL_CGFORM_FIELD cf INNER JOIN ONL_CGFORM_HEAD ch ON cf.CGFORM_HEAD_ID = ch.ID WHERE ch.TABLE_NAME = :tableName ORDER BY cf.ORDER_NUM ";
//        String sql4 = "  select count(*) as visit\n" +
//                "        \t   ,count(distinct(ip)) as ip\n" +
//                "             ,CONVERT(varchar(100), create_time, 23) as tian\n" +
//                "        \t   ,RIGHT(CONVERT(varchar(100), create_time, 23),5) as type\n" +
//                "         from sys_log \n" +
//                "         where log_type = 1 and create_time >= :dayStart and create_time < :dayEnd \n" +
//                "         group by CONVERT(varchar(100), create_time, 23),RIGHT(CONVERT(varchar(100), create_time, 23),5)  \n" +
//                "         order by CONVERT(varchar(100), create_time, 23) \n" +
//                " asc\t  ";
//        String sql5 = "SELECT * FROM jimu_report jr WHERE 1=1 and jr.CREATE_BY = :jimuReport.createBy and jr.TYPE = :jimuReport.type and jr.DEL_FLAG = :jimuReport.delFlag and jr.TEMPLATE = :jimuReport.template ORDER BY jr.create_time DESC";
//        String sql6 = "SELECT count(*) FROM (SELECT * FROM sys_user order by id OFFSET 1 ROWS FETCH NEXT 3 ROWS ONLY) AS a";
//        String sql7 = "SELECT * FROM jimu_report as jr WHERE jr.create_by in (SELECT top 100 username FROM sys_user  ORDER BY create_time) ORDER BY create_time desc";
//        String sql8 = "select  a.*  from (SELECT top 100  jr.create_time,jr.name,jr.code from jimu_report jr LEFT JOIN sys_user s on jr.create_by = s.username ORDER BY  s.create_time) a ORDER BY  a.create_time ASC";
//        String sql9 = "select * from sys_user order by CASE WHEN sex='1' THEN create_time else update_time END";
//        try {
//            System.out.println("1= "+ RemoveSqlOrderByUtil.class.newInstance().removeOrderBy(sql1));
//            System.out.println("2= "+ RemoveSqlOrderByUtil.class.newInstance().removeOrderBy(sql2));
//            System.out.println("3= "+ RemoveSqlOrderByUtil.class.newInstance().removeOrderBy(sql3));
//            System.out.println("4= "+ RemoveSqlOrderByUtil.class.newInstance().removeOrderBy(sql4));
//            System.out.println("5= "+ RemoveSqlOrderByUtil.class.newInstance().removeOrderBy(sql5));
//            System.out.println("6= "+ RemoveSqlOrderByUtil.class.newInstance().removeOrderBy(sql6));
//            System.out.println("7= "+ RemoveSqlOrderByUtil.class.newInstance().removeOrderBy(sql7));
//            System.out.println("8= "+ RemoveSqlOrderByUtil.class.newInstance().removeOrderBy(sql8));
//            System.out.println("9= "+ RemoveSqlOrderByUtil.class.newInstance().removeOrderBy(sql9));
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}