package cn.xsaf1207.interceptor;


import cn.xsaf1207.model.Page;
import cn.xsaf1207.utils.page.MSUtil;
import cn.xsaf1207.utils.page.PageUtil;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 自定义分页mybatis拦截器
 *
 * @author yifun
 */
@Intercepts(
    {
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class})
    }
)
public class PageInterceptor implements Interceptor {
  @Override
  public Object intercept(Invocation invocation) throws Throwable {
    Object[] args = invocation.getArgs();
    MappedStatement ms = (MappedStatement) args[0];
    Object parameter = args[1];
    RowBounds rowBounds = (RowBounds) args[2];
    ResultHandler resultHandler = (ResultHandler) args[3];
    Executor executor = (Executor) invocation.getTarget();
    CacheKey cacheKey;
    BoundSql boundSql;
    //由于逻辑关系，只会进入一次
    if(args.length == 4){
      //4 个参数时
      boundSql = ms.getBoundSql(parameter);
      cacheKey = executor.createCacheKey(ms, parameter, rowBounds, boundSql);
    } else {
      //6 个参数时
      cacheKey = (CacheKey) args[4];
      boundSql = (BoundSql) args[5];
    }
    Page page = PageUtil.getLocalPage();
    if (Objects.nonNull(page) && page.getIsPage()) {
      // 获取 count sql
      String countSql = PageUtil.getCountSql(boundSql.getSql(), "0");
      // 构造 BoundSql
      BoundSql countBoundSql = new BoundSql(ms.getConfiguration(), countSql, boundSql.getParameterMappings(), parameter);
      //创建 count 查询的缓存 key
      CacheKey countKey = executor.createCacheKey(ms, parameter, RowBounds.DEFAULT, countBoundSql);
      MappedStatement countMs = MSUtil.newCountMappedStatement(ms, "COUNT");
      // 执行 count sql
      Object countResultList = executor.query(countMs, parameter, RowBounds.DEFAULT, null, countKey, countBoundSql);
      int total = ((Number) ((List) countResultList).get(0)).intValue();
      page.setTotal(total);
      if (total == 0) {
        return new ArrayList<>(0);
      }
      Map<String, Object> paramMap = PageUtil.processParamMap(ms, parameter, boundSql);
      parameter = PageUtil.processPageParameter(ms, paramMap, boundSql, cacheKey);

      //获取分页 sql
      String pageSql = PageUtil.getPageSql(boundSql.getSql());
      BoundSql pageBoundSql = new BoundSql(ms.getConfiguration(), pageSql, boundSql.getParameterMappings(), parameter);

      Field addParameters = BoundSql.class.getDeclaredField("additionalParameters");
      addParameters.setAccessible(true);
      Map<String, Object> additionalParameters = (Map<String, Object>) addParameters.get(boundSql);
      //设置动态参数
      for (String key : additionalParameters.keySet()) {
        pageBoundSql.setAdditionalParameter(key, additionalParameters.get(key));
      }
      return executor.query(ms, parameter, rowBounds, resultHandler, cacheKey, pageBoundSql);
    }
    //注：下面的方法可以根据自己的逻辑调用多次，在分页插件中，count 和 page 各调用了一次
    return executor.query(ms, parameter, rowBounds, resultHandler, cacheKey, boundSql);
  }

  @Override
  public Object plugin(Object target) {
    return Plugin.wrap(target, this);
  }

  @Override
  public void setProperties(Properties properties) {
  }

}
