package com.jsh.erp.filter;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;


/**
 @ author: Drunkbaby
 @ usages: 用于 SQL 注入的自定义防护
 需要加入相应的 Servlet 环境，因为我这里是纯代码，就不打环境了
 @ 过滤 url：在 WebFilter 当中添加 urlPatterns
 */

@Component
@WebFilter(urlPatterns = "/*", filterName = "sqlInjectFilter")
public class SqliFilter implements Filter {
    public void destroy() {
    }

    public void init(FilterConfig arg0) throws ServletException {
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        // 获得所有请求参数名
        Enumeration params = request.getParameterNames();
        String sql = "";
        while (params.hasMoreElements()) {
            // 得到参数名
            String name = params.nextElement().toString();
            // 得到参数对应值
            String[] value = request.getParameterValues(name);
            for (int i = 0; i < value.length; i++) {
                sql = sql + value[i];
            }
        }
        if (sqlValidate(sql)) {
            throw new IOException("您发送请求中的参数中含有非法字符");
        } else {
            chain.doFilter(request, response);
        }
    }

    /**
     * 参数校验
     * @param str
     */
    public static boolean sqlValidate(String str) {
        str = str.toLowerCase();//统一转为小写
        String badStr = "select|update|and|or|delete|insert|truncate|char|into|substr|ascii|declare|exec|count|master|into|drop|execute|table|sleep|insert| +" +
                "information_schema.columns|table_schema|union|where|select|delete|update|order|by|count";
        String[] badStrs = badStr.split("\\|");
        for (int i = 0; i < badStrs.length; i++) {
            //循环检测，判断在请求参数当中是否包含SQL关键字
            if (str.indexOf(badStrs[i]) >= 0) {
                return true;
            }
        }
        return false;
    }
}