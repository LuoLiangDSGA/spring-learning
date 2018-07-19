package org.boot.security.security;

import lombok.extern.slf4j.Slf4j;
import org.boot.security.exception.BusinessException;
import org.boot.security.model.User;
import org.boot.security.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author luoliang
 * @date 2018/7/8
 */
@Service
@Slf4j
public class JwtUserDetailsServiceImpl implements UserDetailsService {
    @Resource
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(s);
        if (Objects.isNull(user)) {
            log.error("loadUserByUsername failed, user: {} not found", s);
            throw new BusinessException("user: " + s + " not found", HttpStatus.FORBIDDEN);
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(s)
                .password(user.getPassword())
                .authorities(user.getRoles())
                .accountExpired(false)
                .accountLocked(false)
                .disabled(false)
                .build();
    }
}
