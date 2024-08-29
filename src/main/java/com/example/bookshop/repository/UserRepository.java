package com.example.bookshop.repository;

import com.example.bookshop.struct.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    UserEntity findUserEntityById(Integer id);

    @Query("SELECT u FROM UserEntity u WHERE u.name = :username")
    UserEntity getUserByUsername(@Param("username") String username);

    UserEntity findByHash(String hash);

    @Modifying
    @Query(value = "UPDATE users  SET balance=:balance WHERE id=:userId", nativeQuery = true)
    void updateUserBalance(@Param("balance") Double balance, @Param("userId") Integer userId);

    @Query(value = "SELECT u FROM UserEntity AS u JOIN UserContactEntity AS uc ON uc.userId = u where uc.contact = ?1")
    UserEntity findUserEntityByContact(String contact);

    @Modifying
    @Query(value = "DELETE FROM users  WHERE id=?1", nativeQuery = true)
    void deleteUserEntityById(Integer id);

    UserEntity getById(int id);
}
