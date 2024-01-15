package com.example.bookshop.service.impl;

import com.example.bookshop.dto.ProfileFormDto;
import com.example.bookshop.exeption.InvalidPasswordException;
import com.example.bookshop.repository.UserContactRepository;
import com.example.bookshop.repository.UserRepository;
import com.example.bookshop.security.BookstoreUserDetails;
import com.example.bookshop.security.BookstoreUserRegister;
import com.example.bookshop.service.RoleService;
import com.example.bookshop.service.UserService;
import com.example.bookshop.service.util.UniqueTokenUtil;
import com.example.bookshop.struct.enums.ContactType;
import com.example.bookshop.struct.user.RoleEntity;
import com.example.bookshop.struct.user.UserContactEntity;
import com.example.bookshop.struct.user.UserEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;


import java.util.*;

@Component
@Slf4j
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;
    private final UniqueTokenUtil uniqueTokenUtil;
    private JavaMailSender javaMailSender;
    private final BookstoreUserRegister registerUser;
    private final UserContactRepository contactRepository;
    private final RoleService roleService;
    PasswordEncoder encoder;

    @Autowired
    public UserServiceImp(UserRepository userRepository,
                          UniqueTokenUtil uniqueTokenUtil,
                          JavaMailSender javaMailSender,
                          @Lazy BookstoreUserRegister registerUser,
                          UserContactRepository contactRepository,
                          RoleService roleService,
                          PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.uniqueTokenUtil = uniqueTokenUtil;
        this.javaMailSender = javaMailSender;
        this.registerUser = registerUser;
        this.contactRepository = contactRepository;
        this.roleService = roleService;
        this.encoder = encoder;
    }



    @Override
    public UserEntity getUserById(Integer id) {
        return userRepository.findUserEntityById(id);
    }


    @Override
    public UserEntity findByUserFromHash(String hash) {
        return userRepository.findByHash(hash);
    }

    @Override
    @Transactional
    public UserEntity createAnonymousUser() {
        RoleEntity role = roleService.findRoleByName("ROLE_ANONYMOUS");
        Set<RoleEntity> roles = new HashSet<>();
        roles.add(role);
        UserEntity user = new UserEntity();
        user.setBalance(0);
        user.setHash(generatingRandomString());
        user.setPassword("");
        user.setName(generateNameUser());
        user.setRoles(roles);
        user.setRegTime(new Date());
        userRepository.save(user);
        return user;
    }

    @Override
    public String generatingRandomString() {
        return RandomStringUtils.randomAlphabetic(10);
    }

    @Override
    public String generateNameUser() {
        return RandomStringUtils.random(4, true, false);
    }


    @Override
    public UserEntity getUserName(String name) {
        return userRepository.getUserByUsername(name);
    }

    @Override
    @Transactional
    public void updateUserProfile(ProfileFormDto profileDto, Integer userId) {
        UserEntity userEntity = getUserById(userId);
        List<UserContactEntity> listContactUser = userEntity.getListContact();
            userEntity.setName(profileDto.getName());
            userEntity.setPassword(encoder.encode(profileDto.getPassword()));
            userEntity.setRegTime(new Date());
            userRepository.save(userEntity);
            for (UserContactEntity contact : listContactUser) {
                contact.setUserId(userEntity);
                contact.setType(ContactType.EMAIL);
                contact.setContact(profileDto.getMail());
                contactRepository.save(contact);
                contact.setUserId(userEntity);
                contact.setType(ContactType.PHONE);
                contact.setContact(profileDto.getPhone());
                contactRepository.save(contact);
            }

    }

    @Transactional
    @Override
    public void saveBalanceUser(UserEntity user, Double balance) {
        UserEntity userEntity = userRepository.findUserEntityById(user.getId());
       double count = user.getBalance() + balance;
        userEntity.setBalance(count);
        userRepository.save(userEntity);
    }

    @Override
    public void confirmChanges(ProfileFormDto profileForm) throws JsonProcessingException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("rabota822@bk.ru");
        message.setTo(profileForm.getMail());
        message.setSubject("User profile update verification!");
        message.setText(setMessageText(profileForm, uniqueTokenUtil.generateToken(profileForm)));
        javaMailSender.send(message);
    }

    @Override
    public void confirmChangingUserProfile(ProfileFormDto profileForm) throws JsonProcessingException {
        Object curUser = registerUser.getCurrentUser();
        if (curUser instanceof BookstoreUserDetails) {
            confirmChanges(profileForm);
        }
    }

    @Override
    @Transactional
    public void changeUserProfile(String token) throws JsonProcessingException {
        Object user = registerUser.getCurrentUser();
        if (user instanceof BookstoreUserDetails) {
            Integer id = ((BookstoreUserDetails) user).getContact().getUserId().getId();
            ProfileFormDto profileForm = uniqueTokenUtil.extractProfileForm(token);
            updateUserProfile(profileForm, id);
        }
    }

    @Override
    public UserEntity saveUserEntity(UserEntity user) {
        return userRepository.save(user);
    }

    @Override
    public UserContactEntity findContactUser(UserEntity user, ContactType type) {
        return contactRepository.findFirstUserContactEntityByUserIdAndType(user, type);
    }

    public String setMessageText(ProfileFormDto profileFormDto, String token){
        StringBuilder sb = new StringBuilder();
                    sb
                    .append("You have changed your credentials!!! ")
                    .append(" Name: " + profileFormDto.getName())
                    .append(" E-mail: " + profileFormDto.getMail())
                    .append(" Phone: " + profileFormDto.getPhone())
                    .append(" New password: " + profileFormDto.getPassRepeated())
                    .append(" Verification link is: " + "http://localhost:8081/profile/verify/" + token + " please, follow it.");

        return sb.toString();
    }
}
