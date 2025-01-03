package com.clean_light.server.auth.user.repository;

import com.clean_light.server.auth.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByLoginId(String loginId);

    boolean existsByNickName(String nickName);

    Optional<User> findByLoginId(String loginId);

    boolean deleteByLoginId(String loginId);
}
