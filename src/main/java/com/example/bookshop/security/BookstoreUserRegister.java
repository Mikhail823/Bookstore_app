package com.example.bookshop.security;

import com.example.bookshop.aop.annotations.LoggableExceptionHandler;
import com.example.bookshop.repository.UserContactRepository;
import com.example.bookshop.repository.UserRepository;
import com.example.bookshop.security.jwt.JWTUtil;
import com.example.bookshop.struct.user.RoleType;
import com.example.bookshop.struct.user.UserContactEntity;
import com.example.bookshop.struct.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookstoreUserRegister {

    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final UserContactRepository contactRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private final AuthenticationManager authenticationManager;
    @Autowired
    private final JWTUtil jwtUtil;
    @Autowired
    private final BookstoreUserDetailsService bookstoreUserDetailsService;

    public void registrationNewUser(RegistrationForm registrationForm, HttpServletRequest request){
        UserEntity userRegAny = userRepository.findByHash(getHashOfTheUserFromCookie(request));

        if (userRegAny != null){
            UserContactEntity emailContact = contactRepository.findFirstByContact(registrationForm.getEmail());
            UserContactEntity phoneContact = contactRepository.findFirstByContact(registrationForm.getPhone());
            userRegAny.setName(registrationForm.getName());
            userRegAny.setPassword(passwordEncoder.encode(registrationForm.getPass()));
            userRegAny.setBalance(0);
            userRegAny.setRegTime(new Date());
            userRegAny.setHash(generateString());
            userRegAny.setRoleType(RoleType.USER);
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
    public ContactConfirmationResponse jwtLogin(ContactConfirmationPayload payload) throws Exception{
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
    public ContactConfirmationResponse jwtLoginByPhoneNumber(ContactConfirmationPayload payload) throws Exception{
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

    public String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    public static String generateString() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replace("-", "");
    }

    public boolean isAuthUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String anonymousUser = String.valueOf(auth.getPrincipal());
        if (anonymousUser.equals("anonymousUser")){
            return false;
        } else {
            return true;
        }
    }

    public String encodingPasswordCode(String pass){
        return passwordEncoder.encode(pass);
    }



}
