package com.example.bookshop.service;

import com.example.bookshop.dto.ProfileFormDto;
import com.example.bookshop.struct.enums.ContactType;
import com.example.bookshop.struct.user.UserContactEntity;
import com.example.bookshop.struct.user.UserEntity;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface UserService {
    UserEntity getUserById(Integer id);

    UserEntity findByUserFromHash(String hash);

    UserEntity createAnonymousUser();

    String generatingRandomString();

    String generateNameUser();

    UserEntity getUserName(String name);


    void updateUserProfile(ProfileFormDto profileDto, Integer userId);

    void saveBalanceUser(UserEntity user, Double balance);

    void confirmChanges(ProfileFormDto profileForm) throws JsonProcessingException;


    void confirmChangingUserProfile(ProfileFormDto  profileForm) throws JsonProcessingException;

    void changeUserProfile(String token) throws JsonProcessingException;

    UserEntity saveUserEntity(UserEntity user);

    UserContactEntity findContactUser(UserEntity user, ContactType type);



}
