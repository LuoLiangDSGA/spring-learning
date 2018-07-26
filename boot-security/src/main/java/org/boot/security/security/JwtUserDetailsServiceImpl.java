package org.boot.security.security;

import org.boot.security.exception.BusinessException;
import org.boot.security.model.User;
import org.boot.security.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class JwtUserDetailsServiceImpl implements UserDetailsService {
    private final static Logger log = LoggerFactory.getLogger(JwtUserDetailsServiceImpl.class);
    @Resource
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username);
        if (Objects.isNull(user)) {
            log.error("loadUserByUsername failed, user: {} not found", username);            log.error("loadUserByUsername failed, user: {} not found", username);

            throw new BusinessException("user: " + username + " not found", HttpStatus.FORBIDDEN);
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password(user.getPassword())
                .authorities(user.getRoles())
                .accountExpired(false)
                .accountLocked(false)
                .disabled(false)
                .build();
    }
}
