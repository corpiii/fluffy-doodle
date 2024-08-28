package com.clean_light.server.user.service;

import com.clean_light.server.jwt.dto.UserTokenDTO;
import com.clean_light.server.user.domain.User;
import com.clean_light.server.user.dto.AuthInfo;
import com.clean_light.server.user.error.UserAuthError;
import com.clean_light.server.user.error.UserAuthException;
import com.clean_light.server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserAuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, Object> redisTemplate;

    public Long join(User user) {
        User savedUser = userRepository.save(user);

        return savedUser.getId();
    }

    public UserTokenDTO login(User user) throws UserAuthException {
        User foundedUser = userRepository.findByLoginId(user.getLoginId())
                .orElseThrow(() -> new UserAuthException(UserAuthError.INVALID_LOGIN_ID));

        if (!passwordEncoder.matches(user.getPassword(), foundedUser.getPassword())) {
            throw new UserAuthException(UserAuthError.INVALID_PASSWORD);
        }

        return UserTokenDTO.from(user);
    }

    public User fetchUserBySessionId(String sessionId) {
        return (User) redisTemplate.opsForValue().get(sessionId);
    }

    public void deleteUserBySessionId(String sessionId) throws UserAuthException {
        User user = (User) redisTemplate.opsForValue().get(sessionId);

        if (user == null) {
            throw new UserAuthException(UserAuthError.USER_NOT_CONNECTED);
        }

        userRepository.deleteById(user.getId());
    }

    public void logout(String sessionId) {
        User user = (User) redisTemplate.opsForValue().get(sessionId);

        if (user == null) {
            throw new UserAuthException(UserAuthError.USER_NOT_CONNECTED);
        }

        redisTemplate.delete(sessionId);
    }
}
