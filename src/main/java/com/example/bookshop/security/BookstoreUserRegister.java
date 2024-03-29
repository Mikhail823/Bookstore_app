package com.example.bookshop.security;

import com.example.bookshop.aop.annotations.LoggableExceptionHandler;
import com.example.bookshop.repository.UserContactRepository;
import com.example.bookshop.repository.UserRepository;
import com.example.bookshop.security.jwt.JWTUtil;
import com.example.bookshop.service.RoleService;
import com.example.bookshop.struct.user.RoleEntity;
import com.example.bookshop.struct.user.UserContactEntity;
import com.example.bookshop.struct.user.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
public class BookstoreUserRegister {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final UserContactRepository contactRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final BookstoreUserDetailsService bookstoreUserDetailsService;

    @Autowired
    public BookstoreUserRegister(UserRepository userRepository, RoleService roleService,
                                 UserContactRepository contactRepository, PasswordEncoder passwordEncoder,
                                 AuthenticationManager authenticationManager, JWTUtil jwtUtil,
                                 BookstoreUserDetailsService bookstoreUserDetailsService) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.contactRepository = contactRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.bookstoreUserDetailsService = bookstoreUserDetailsService;
    }

    @Transactional
    public void registrationNewUser(RegistrationForm registrationForm, HttpServletRequest request){
        UserEntity userRegAny = userRepository.findByHash(getHashOfTheUserFromCookie(request));

        if (userRegAny != null){
            UserContactEntity emailContact = contactRepository.findFirstByContact(registrationForm.getEmail());
            UserContactEntity phoneContact = contactRepository.findFirstByContact(registrationForm.getPhone());
            RoleEntity role = roleService.findRoleByName("ROLE_USER");
            Set<RoleEntity> roles = new HashSet<>();
            roles.add(role);
            userRegAny.setName(registrationForm.getName());
            userRegAny.setPassword(passwordEncoder.encode(registrationForm.getPass()));
            userRegAny.setBalance(0);
            userRegAny.setRegTime(new Date());
            userRegAny.setHash(generateString());
            userRegAny.setRoles(roles);
            userRepository.save(userRegAny);
            emailContact.setApproved((short) 1);
            contactRepository.save(emailContact);
            phoneContact.setApproved((short) 1);
            contactRepository.save(phoneContact );
        }
    }

    public String getHashOfTheUserFromCookie(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        String hash = "";
        for (Cookie cookie : cookies){
            if (cookie.getName().equals("USER-ANONYMOUS")){
                hash = cookie.getValue();
            }
        }
        return hash;
    }

    @LoggableExceptionHandler
    public ContactConfirmationResponse jwtLogin(ContactConfirmationPayload payload){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(payload.getContact(),
                payload.getCode()));
        BookstoreUserDetails userDetails =
                (BookstoreUserDetails) bookstoreUserDetailsService.loadUserByUsername(payload.getContact());
        String jwtToken = jwtUtil.generateToken(userDetails);
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        response.setResult(jwtToken);
        return response;
    }

    @LoggableExceptionHandler
    public ContactConfirmationResponse jwtLoginByPhoneNumber(ContactConfirmationPayload payload){
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        UserDetails userDetails = bookstoreUserDetailsService.loadUserByUsername(payload.getContact());
        String jwtToken = jwtUtil.generateToken(userDetails);
        response.setResult(jwtToken);
        return response;
    }

    @LoggableExceptionHandler
    public ContactConfirmationResponse login(ContactConfirmationPayload payload) {
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(payload.getContact(),
                        payload.getCode()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        response.setResult("true");
        return response;
    }

    public Object getCurrentUser() {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    }

    public static String generateString() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replace("-", "");
    }

    public boolean isAuthAnonymousUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String anonymousUser = String.valueOf(auth.getPrincipal());
        return (anonymousUser.equals("anonymousUser"));
    }
}
