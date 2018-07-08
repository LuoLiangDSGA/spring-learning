package org.boot.security.security;

import org.boot.security.model.User;
import org.boot.security.repository.UserRepository;
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
public class JwtUserDetailsImpl implements UserDetailsService {
    @Resource
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(s);
        if (Objects.isNull(user)) {
            throw new UsernameNotFoundException("user: " + s + " not found");
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
