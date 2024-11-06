package org.jeecgframework.minidao.pagehelper.dialect;

import org.apache.commons.lang3.StringUtils;
import org.jeecgframework.minidao.pagehelper.PageException;
import org.jeecgframework.minidao.pagehelper.dialect.helper.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 基础方言信息
 */
public class PageAutoDialect {

    private static Map<String, Class<? extends Dialect>> dialectAliasMap = new HashMap<String, Class<? extends Dialect>>();

    public static void registerDialectAlias(String alias, Class<? extends Dialect> dialectClass) {
        dialectAliasMap.put(alias, dialectClass);
    }

    static {
        //注册别名
        registerDialectAlias("hsqldb", HsqldbDialect.class);
        registerDialectAlias("h2", HsqldbDialect.class);
        registerDialectAlias("phoenix", HsqldbDialect.class);
        //瀚高数据库
        registerDialectAlias("highgo", HsqldbDialect.class);
        //虚谷数据库
        registerDialectAlias("xugu", HsqldbDialect.class);


        registerDialectAlias("postgresql", PostgreSqlDialect.class);
        //人大金仓 [国产]
        registerDialectAlias("kingbase", PostgreSqlDialect.class);
        registerDialectAlias("kingbase8", PostgreSqlDialect.class);
        //华为高斯
        registerDialectAlias("zenith", PostgreSqlDialect.class);


        registerDialectAlias("mysql", MySqlDialect.class);
        registerDialectAlias("mariadb", MySqlDialect.class);
        //阿里云PolarDB
        registerDialectAlias("polardb", MySqlDialect.class);
        //SQLite数据库 应用平台mobile
        registerDialectAlias("sqlite", MySqlDialect.class);
        registerDialectAlias("clickhouse", MySqlDialect.class);
        //涛思数据库 [国产]
        registerDialectAlias("taos", MySqlDialect.class);

        registerDialectAlias("herddb", HerdDBDialect.class);

        registerDialectAlias("oracle", OracleDialect.class);
        registerDialectAlias("oracle9i", Oracle9iDialect.class);
        //达梦数据库 [国产]
        registerDialectAlias("dm", OracleDialect.class);
        //阿里云PPAS数据库
        registerDialectAlias("edb", OracleDialect.class);


        registerDialectAlias("db2", Db2Dialect.class);
        registerDialectAlias("informix", InformixDialect.class);
        //解决 informix-sqli #129，仍然保留上面的
        registerDialectAlias("informix-sqli", InformixDialect.class);

        registerDialectAlias("sqlserver", SqlServerDialect.class);
        registerDialectAlias("sqlserver2012", SqlServer2012Dialect.class);
        registerDialectAlias("derby", SqlServer2012Dialect.class);

        //神通数据库 [国产]
        registerDialectAlias("oscar", OscarDialect.class);
    }

    //是否多数据源
    private boolean  multiDialect = false;
    //多数据源时，获取jdbcurl后是否关闭连接
    private boolean closeConn = true;
    //缓存
    private AbstractHelperDialect delegate;
    //方言缓存
    private Map<String, AbstractHelperDialect> urlDialectMap = new ConcurrentHashMap<String, AbstractHelperDialect>();
    //方言线程缓存
    private ThreadLocal<AbstractHelperDialect> dialectThreadLocal = new ThreadLocal<AbstractHelperDialect>();
    private ReentrantLock lock = new ReentrantLock();

    /**
     * 多数据动态获取时，每次需要初始化
     * @param dataSource
     */
    public void initDelegateDialect(DataSource dataSource) {
        if (multiDialect) {
            dialectThreadLocal.set(getDialect(dataSource));
        } else {
            if(delegate==null){
                this.delegate = getDialect(dataSource);
            }
        }
    }

    /**
     * 获取当前的代理对象
     * @return
     */
    public AbstractHelperDialect getDelegate() {
        if (multiDialect) {
            return dialectThreadLocal.get();
        } else {
            return delegate;
        }
    }

    /**
     * 获取数据库类型 dialect
     * @param jdbcUrl
     * @return
     */
    public String getDialectKeyByJdbcUrl(String jdbcUrl) {
        final String url = jdbcUrl.toLowerCase();
        for (String dialect : dialectAliasMap.keySet()) {
            if (url.contains(":" + dialect.toLowerCase() + ":")) {
                return dialect;
            }
        }
        return null;
    }

    /**
     * 反射类
     *
     * @param className
     * @return
     * @throws Exception
     */
    private Class resloveDialectClass(String className) throws Exception {
        if (dialectAliasMap.containsKey(className.toLowerCase())) {
            return dialectAliasMap.get(className.toLowerCase());
        } else {
            return null;
        }
    }

    /**
     * 初始化 helper
     *
     * @param dialectKey
     */
    private AbstractHelperDialect initDialect(String dialectKey) {
        AbstractHelperDialect dialect;
        if (StringUtils.isBlank(dialectKey)) {
            throw new PageException("使用分页时，必须设置 helper 属性");
        }
        try {
            Class sqlDialectClass = resloveDialectClass(dialectKey);
            if (AbstractHelperDialect.class.isAssignableFrom(sqlDialectClass)) {
                dialect = (AbstractHelperDialect) sqlDialectClass.newInstance();
            } else {
                throw new PageException("方言必须是实现 " + AbstractHelperDialect.class.getCanonicalName() + " 接口的实现类!");
            }
        } catch (Exception e) {
            throw new PageException("初始化 helper [" + dialectKey + "]时出错:" + e.getMessage(), e);
        }
        return dialect;
    }

    /**
     * 获取url
     *
     * @param dataSource
     * @return
     */
    private String getUrl(DataSource dataSource) {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            return conn.getMetaData().getURL();
        } catch (SQLException e) {
            throw new PageException(e);
        } finally {
            if (conn != null) {
                try {
                    if (closeConn) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    //ignore
                }
            }
        }
    }

    /**
     * 根据 jdbcUrl 获取数据库方言
     *
     * @param dataSource
     * @return
     */
    private AbstractHelperDialect getDialect(DataSource dataSource) {
        //改为对dataSource做缓存
        String url = getUrl(dataSource);
        if (urlDialectMap.containsKey(url)) {
            return urlDialectMap.get(url);
        }
        try {
            lock.lock();
            if (StringUtils.isBlank(url)) {
                throw new PageException("无法自动获取jdbcUrl");
            }
            String dialectKey = getDialectKeyByJdbcUrl(url);
            if (dialectKey == null) {
                throw new PageException("无法自动获取数据库类型");
            }
            AbstractHelperDialect dialect = initDialect(dialectKey);
            urlDialectMap.put(url, dialect);
            return dialect;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 根据 jdbcUrl 获取数据库方言
     * @param dbUrl
     * @return
     */
    public AbstractHelperDialect getDialect(String dbUrl) {
        //改为对dataSource做缓存
        if (urlDialectMap.containsKey(dbUrl)) {
            return urlDialectMap.get(dbUrl);
        }
        try {
            lock.lock();
            if (StringUtils.isBlank(dbUrl)) {
                throw new PageException("无法自动获取jdbcUrl");
            }
            String dialectStr = getDialectKeyByJdbcUrl(dbUrl);
            if (dialectStr == null) {
                throw new PageException("无法自动获取数据库类型");
            }
            AbstractHelperDialect dialect = initDialect(dialectStr);
            urlDialectMap.put(dbUrl, dialect);
            return dialect;
        } finally {
            lock.unlock();
        }
    }
}
