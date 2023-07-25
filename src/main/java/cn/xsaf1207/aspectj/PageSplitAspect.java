package cn.xsaf1207.aspectj;

import cn.xsaf1207.annotation.PageSplit;
import cn.xsaf1207.constant.PageConstants;
import cn.xsaf1207.model.Page;
import cn.xsaf1207.utils.ServletUtil;
import cn.xsaf1207.utils.page.PageUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @author yifun
 */
@Aspect
@Component
public class PageSplitAspect {

    @Around("@annotation(pageSplit)")
    public Object pageSplitExexution(ProceedingJoinPoint joinPoint, PageSplit pageSplit) throws Throwable {
        Integer pageNum = ServletUtil.getParameterToInt(PageConstants.PAGE_NUM, 1);
        Integer pageSize = ServletUtil.getParameterToInt(PageConstants.PAGE_SIZE, 10);
        Page page = Page.builder().pageNum(pageNum).pageSize(pageSize).isPage(Boolean.TRUE).build();
        PageUtil.setLocalPage(page);
        Object result = joinPoint.proceed();
        PageUtil.clearPage();
        return result;
    }

}
