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
    public void updateUserProfile(ProfileFormDto profileFormDto, Integer userId) {
        UserEntity userEntity = getUserById(userId);
        List<UserContactEntity> listContactUser = userEntity.getListContact();
            userEntity.setName(profileFormDto.getName());
            userEntity.setPassword(encoder.encode(profileFormDto.getPasswordRepl()));
            userEntity.setRegTime(new Date());
            userRepository.save(userEntity);
            for (UserContactEntity contact : listContactUser) {
                contact.setUserId(userEntity);
                contact.setType(ContactType.EMAIL);
                contact.setContact(profileFormDto.getMail());
                contactRepository.save(contact);
                contact.setUserId(userEntity);
                contact.setType(ContactType.PHONE);
                contact.setContact(profileFormDto.getPhone());
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
    public void confirmChanges(String phone, String email,  String name,
                               String passwordRepl) throws JsonProcessingException {
        SimpleMailMessage message = new SimpleMailMessage();
        ProfileFormDto form = getUserFormProfile(phone, email, name, passwordRepl);
        message.setFrom("rabota822@bk.ru");
        message.setTo(email);
        message.setSubject("User profile update verification!");
        message.setText(setMessageText(phone, email, name, passwordRepl,
                uniqueTokenUtil.generateToken(form)));
        javaMailSender.send(message);
    }

    @Override
    public void confirmChangingUserProfile(String phone, String email,  String name,
                                           String passwordReply) throws JsonProcessingException {
        Object curUser = registerUser.getCurrentUser();
        if (curUser instanceof BookstoreUserDetails) {
            confirmChanges(phone, email, name,passwordReply);
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

    public String setMessageText(String phone, String email,  String name,
                                  String passwordRepl, String token){

        return "You have changed your credentials!!! "
                + " Name: " + name
                + " E-mail: " + email
                + " Phone: " + phone
                + " New password: " + passwordRepl
                + " Verification link is: " + " http://192.168.1.3:8081/profile/verify/"
                + token + " please, follow it.";
    }

    @Override
    public UserEntity getUserRegistrationByContact(String contact){
        return userRepository.findUserEntityByContact(contact);
    }

    @Override
    public void checkPassword(String password, String passwordReply, Model model) throws InvalidPasswordException {
        if (password.length() < 6 || password.trim().isEmpty()) {
            throw new InvalidPasswordException("Пароль должен быть больше шести символов");
        } else if (!password.equals(passwordReply)) {
            throw new InvalidPasswordException("Пароли не совпадают");
        }
    }

    public ProfileFormDto getUserFormProfile(String phone, String email,  String name,
                                             String passwordRepl){
        ProfileFormDto userProfile = new ProfileFormDto();
        userProfile.setName(name);
        userProfile.setMail(email);
        userProfile.setPhone(phone);
        userProfile.setPasswordRepl(passwordRepl);
        return userProfile;

    }
}
