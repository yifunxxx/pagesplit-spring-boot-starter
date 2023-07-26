package cn.xsaf1207.config;


import cn.xsaf1207.aspectj.PageSplitAspect;
import cn.xsaf1207.interceptor.PageInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yifun
 */
@Configuration
public class PageConfig {

  @Bean
  public PageSplitAspect pageSplitAspect() {
    return new PageSplitAspect();
  }

  @Bean
  public PageInterceptor pageInterceptor() {
    return new PageInterceptor();
  }

}
