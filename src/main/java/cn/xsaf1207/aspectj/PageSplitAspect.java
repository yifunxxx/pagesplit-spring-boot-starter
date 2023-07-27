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
public class PageSplitAspect {

    @Around("@annotation(pageSplit)")
    public Object pageSplitExexution(ProceedingJoinPoint joinPoint, PageSplit pageSplit) throws Throwable {
        Integer pageNum = ServletUtil.getParameterToInt(PageConstants.PAGE_NUM, 1);
        Integer pageSize = ServletUtil.getParameterToInt(PageConstants.PAGE_SIZE, 10);
        pageNum = pageNum < 1 ? 1 : pageNum;
        pageSize = pageSize < 1 ? 10 : pageSize;
        Page page = new Page(pageNum, pageSize, Boolean.TRUE);
        PageUtil.setLocalPage(page);
        Object result = joinPoint.proceed();
        PageUtil.clearPage();
        return result;
    }

}
