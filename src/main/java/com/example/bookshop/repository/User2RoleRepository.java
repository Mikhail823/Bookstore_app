package com.example.bookshop.repository;

import com.example.bookshop.struct.user.links.User2Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface User2RoleRepository extends JpaRepository<User2Role, Integer> {
    @Query(value = "SELECT * FROM user2role WHERE user_id=?", nativeQuery = true)
    User2Role findFirstByUser_id(Integer userId);
}
