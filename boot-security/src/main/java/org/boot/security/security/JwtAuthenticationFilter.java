package org.boot.security.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author luoliang
 * @date 2018/7/9
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Resource
    private UserDetailsService userDetailsService;
    @Resource
    private JwtTokenHandler jwtTokenHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String jwt = request.getHeader("token");
        if (!StringUtils.isEmpty(jwt) && jwtTokenHandler.validateToken(jwt)) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(jwtTokenHandler.getUsernameByToken(jwt));
            Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(request, response);
    }
}
