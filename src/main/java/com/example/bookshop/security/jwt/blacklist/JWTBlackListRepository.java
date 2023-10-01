package com.example.bookshop.security.jwt.blacklist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JWTBlackListRepository extends JpaRepository<JWTBlackListEntity, Integer> {
    JWTBlackListEntity save(JWTBlackListEntity jwtBlackListEntity);
}
