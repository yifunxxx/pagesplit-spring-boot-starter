package cn.xsaf1207.utils.page;

import cn.xsaf1207.model.Page;
import cn.xsaf1207.utils.TypeConvertUtil;
import org.apache.ibatis.builder.annotation.ProviderSqlSource;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分页相关变量方法
 *
 * @author yifun
 */
public class PageUtil {
  protected static final ThreadLocal<Page> LOCAL_PAGE = new ThreadLocal<>();

  /**
   * 设置分页参数
   *
   * @param page
   */
  public static void setLocalPage(Page page) {
    LOCAL_PAGE.set(page);
  }

  /**
   * 获取 Page 参数
   *
   * @return
   */
  public static Page getLocalPage() {
    return LOCAL_PAGE.get();
  }

  /**
   * 获取 Page.count
   */
  public static int getCount() {
    return TypeConvertUtil.toInt(LOCAL_PAGE.get().getTotal(), 0);
  }

  /**
   * 移除本地变量
   */
  public static void clearPage() {
    LOCAL_PAGE.remove();
  }

  /**
   * 拼接countsql
   */
  public static String getCountSql(final String sql, String name) {
    StringBuilder stringBuilder = new StringBuilder(sql.length() + 40);
    stringBuilder.append("select count(");
    stringBuilder.append(name);
    stringBuilder.append(") from ( \n");
    stringBuilder.append(sql);
    stringBuilder.append("\n ) tmp_count");
    return stringBuilder.toString();
  }

  /**
   * 拼接limit
   */
  public static String getPageSql(String sql) {
    StringBuilder sqlBuilder = new StringBuilder(sql.length() + 14);
    sqlBuilder.append(sql);
    if (LOCAL_PAGE.get().getPageNum() == 1) {
      sqlBuilder.append("\n LIMIT ? ");
    } else {
      sqlBuilder.append("\n LIMIT ?, ? ");
    }
    return sqlBuilder.toString();
  }

  /**
   * 处理sql参数
   */
  public static Map<String, Object> processParamMap(MappedStatement ms, Object parameter, BoundSql boundSql) throws NoSuchFieldException, IllegalAccessException {
    Map<String, Object> paramMap = null;
    if (parameter == null) {
      paramMap = new HashMap<String, Object>();
    } else if (parameter instanceof Map) {
      //解决不可变Map的情况
      paramMap = new HashMap<String, Object>();
      paramMap.putAll((Map) parameter);
    } else {
      paramMap = new HashMap<String, Object>();
      // sqlSource为ProviderSqlSource时，处理只有1个参数的情况
      if (ms.getSqlSource() instanceof ProviderSqlSource) {
        Field methodArgumentNames = ProviderSqlSource.class.getDeclaredField("providerMethodArgumentNames");
        String[] providerMethodArgumentNames = (String[]) methodArgumentNames.get(ms.getSqlSource());
        if (providerMethodArgumentNames != null && providerMethodArgumentNames.length == 1) {
          paramMap.put(providerMethodArgumentNames[0], parameter);
          paramMap.put("param1", parameter);
        }
      }
      //动态sql时的判断条件不会出现在ParameterMapping中，但是必须有，所以这里需要收集所有的getter属性
      //TypeHandlerRegistry可以直接处理的会作为一个直接使用的对象进行处理
      boolean hasTypeHandler = ms.getConfiguration().getTypeHandlerRegistry().hasTypeHandler(parameter.getClass());
      MetaObject metaObject = MetaObject.forObject(parameter, new DefaultObjectFactory(), new DefaultObjectWrapperFactory(), new DefaultReflectorFactory());
      //需要针对注解形式的MyProviderSqlSource保存原值
      if (!hasTypeHandler) {
        for (String name : metaObject.getGetterNames()) {
          paramMap.put(name, metaObject.getValue(name));
        }
      }
      //下面这段方法，主要解决一个常见类型的参数时的问题
      if (boundSql.getParameterMappings() != null && boundSql.getParameterMappings().size() > 0) {
        for (ParameterMapping parameterMapping : boundSql.getParameterMappings()) {
          String name = parameterMapping.getProperty();
          if (!name.equals("page_first")
              && !name.equals("page_second")
              && paramMap.get(name) == null) {
            if (hasTypeHandler
                || parameterMapping.getJavaType().equals(parameter.getClass())) {
              paramMap.put(name, parameter);
              break;
            }
          }
        }
      }
    }
    return paramMap;
  }

  /**
   * 处理分页参数
   */
  public static Object processPageParameter(MappedStatement ms, Map<String, Object> paramMap, BoundSql boundSql, CacheKey pageKey) {
    Page page = LOCAL_PAGE.get();
    int start = page.getPageNum() == 1 ? 0 : ((page.getPageNum() - 1) * page.getPageSize());
    paramMap.put("page_first", start);
    paramMap.put("page_second", page.getPageSize());
    //处理pageKey
    pageKey.update(start);
    pageKey.update(page.getPageSize());
    //处理参数配置
    if (boundSql.getParameterMappings() != null) {
      List<ParameterMapping> newParameterMappings = new ArrayList<>(boundSql.getParameterMappings());
      if (start == 0) {
        newParameterMappings.add(new ParameterMapping.Builder(ms.getConfiguration(), "page_second", int.class).build());
      } else {
        newParameterMappings.add(new ParameterMapping.Builder(ms.getConfiguration(), "page_first", int.class).build());
        newParameterMappings.add(new ParameterMapping.Builder(ms.getConfiguration(), "page_second", int.class).build());
      }
      MetaObject metaObject = MetaObject.forObject(boundSql, new DefaultObjectFactory(), new DefaultObjectWrapperFactory(), new DefaultReflectorFactory());
      metaObject.setValue("parameterMappings", newParameterMappings);
    }
    return paramMap;
  }

}
