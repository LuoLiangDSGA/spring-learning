package org.boot.security.service.impl;

import org.boot.security.exception.BusinessException;
import org.boot.security.model.Role;
import org.boot.security.model.User;
import org.boot.security.repository.UserRepository;
import org.boot.security.security.JwtTokenHandler;
import org.boot.security.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Objects;

/**
 * @author luoliang
 * @date 2018/7/14
 */
@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserRepository userRepository;
    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private UserDetailsService userDetailsService;
    @Resource
    private JwtTokenHandler jwtTokenHandler;

    @Override
    public User register(User user) {
        if (Objects.nonNull(userRepository.findUserByUsername(user.getUsername()))) {
            throw new BusinessException("用户已存在，注册失败", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        user.setRoles(Collections.singletonList(Role.ROLE_USER));

        return userRepository.save(user);
    }

    @Override
    public String login(String username, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        return jwtTokenHandler.generateToken(userDetails);
    }

    @Override
    public String refresh(String oldToken) {
        //从token中拿到username
        String username = jwtTokenHandler.getUsernameByToken(oldToken);
        //获取UserDetails
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        //验证
        if (jwtTokenHandler.validateToken(oldToken, userDetails)) {
            return jwtTokenHandler.refreshToken(userDetails);
        }

        return null;
    }
}
