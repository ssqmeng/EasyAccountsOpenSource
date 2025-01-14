package com.deepblue.yd_jz.config;

// JwtAuthenticationFilter.java
import com.aliyun.oss.HttpMethod;
import com.deepblue.yd_jz.utils.JwtTokenUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String HEADER_STRING = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//
//        System.out.println(request.getMethod());
//        Enumeration<String> headerNames = request.getHeaderNames();
//        while (headerNames.hasMoreElements()) {
//            String headerName = headerNames.nextElement();
//            System.out.println("Header Name: " + headerName + ", Value: " + request.getHeader(headerName));
//        }
//        String header = request.getHeader(HEADER_STRING);
//        if (!HttpMethod.OPTIONS.toString().equals(request.getMethod())) {
//            System.out.println("获取到header"+header);
//        }
//
//
//        if (header != null && header.startsWith(TOKEN_PREFIX)) {
//            System.out.println("获取到header"+header);
//        }
//
//
//        // 如果是 OPTIONS 请求，则跳过认证处理
//        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
//            chain.doFilter(request, response);
//            return;
//        }
//
//        String username = "user";
//        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null, null);
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
////        if (header != null && header.startsWith(TOKEN_PREFIX)) {
////            String token = header.replace(TOKEN_PREFIX, "");
////            if (JwtTokenUtil.validateToken(token)) {
////                String username = JwtTokenUtil.getUsernameFromToken(token);
////                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null, null);
////                SecurityContextHolder.getContext().setAuthentication(authentication);
////            }
////        }
////        if (HttpMethod.OPTIONS.toString().equals(request.getMethod())) {
////            response.setStatus(HttpServletResponse.SC_OK);
////        }else{
//            filterChain.doFilter(request, response);
////        }
//    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 如果是 OPTIONS 请求，跳过认证
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader(HEADER_STRING);
        if (header != null && header.startsWith(TOKEN_PREFIX)) {
            String token = header.replace(TOKEN_PREFIX, "");
            if (JwtTokenUtil.validateToken(token)) {
                String username = JwtTokenUtil.getUsernameFromToken(token);
                // 这里你可以设置用户的角色等权限信息
                // SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(username, null, null));
                // 或者你可以在这里解析用户的角色权限并设置
                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(username, null, null)  // 此处可以添加权限角色
                );
            }
//            else{
//                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//                response.getWriter().write("Token has expired");
//                return;  // 结束过滤器链，不再进行后续的处理
//            }
        }
        filterChain.doFilter(request, response);
    }


}

