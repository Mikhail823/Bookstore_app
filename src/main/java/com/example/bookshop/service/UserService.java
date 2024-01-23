package com.example.bookshop.service;

import com.example.bookshop.dto.ProfileFormDto;
import com.example.bookshop.exeption.InvalidPasswordException;
import com.example.bookshop.struct.enums.ContactType;
import com.example.bookshop.struct.user.UserContactEntity;
import com.example.bookshop.struct.user.UserEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.ui.Model;

public interface UserService {
    UserEntity getUserById(Integer id);

    UserEntity findByUserFromHash(String hash);

    UserEntity createAnonymousUser();

    String generatingRandomString();

    String generateNameUser();

    UserEntity getUserName(String name);

    void updateUserProfile(ProfileFormDto profileFormDto, Integer userId);

    void saveBalanceUser(UserEntity user, Double balance);

    void confirmChanges(String phone, String email,  String name,
                        String passwordRepl) throws JsonProcessingException;


    void confirmChangingUserProfile(String phone, String email,  String name,
                                    String passwordReply) throws JsonProcessingException;

    void changeUserProfile(String token) throws JsonProcessingException;

    UserEntity saveUserEntity(UserEntity user);

    UserContactEntity findContactUser(UserEntity user, ContactType type);

    UserEntity getUserRegistrationByContact(String contact);

    void checkPassword(String password, String passwordReply, Model model) throws InvalidPasswordException;
}
