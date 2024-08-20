package com.clean_light.server.user.repository;

import com.clean_light.server.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByLoginId(String LoginId);

    boolean existsByNickName(String nickName);
}
