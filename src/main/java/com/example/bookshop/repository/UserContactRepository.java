package com.example.bookshop.repository;

import com.example.bookshop.struct.enums.ContactType;
import com.example.bookshop.struct.user.UserContactEntity;
import com.example.bookshop.struct.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserContactRepository extends JpaRepository<UserContactEntity, Integer> {

    UserContactEntity findFirstByContactAndType(String contact, ContactType type);

    UserContactEntity findUserContactEntityByCode(String code);

    UserContactEntity findFirstByContact(String contact);

    UserContactEntity findUserContactEntityByContactAndUserId(String contact, UserEntity user);

    List<UserContactEntity> findUserContactEntitiesByUserId(UserEntity userId);

    UserContactEntity findFirstUserContactEntityByUserIdAndType(UserEntity user, ContactType type);


}
