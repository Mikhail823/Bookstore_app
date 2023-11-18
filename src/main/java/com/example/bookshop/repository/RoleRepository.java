package com.example.bookshop.repository;

import com.example.bookshop.struct.user.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Integer> {
    RoleEntity findRoleEntityByName(String name);
}
