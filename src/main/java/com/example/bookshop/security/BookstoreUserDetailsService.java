package com.example.bookshop.security;

import com.example.bookshop.repository.UserContactRepository;
import com.example.bookshop.repository.UserRepository;
import com.example.bookshop.security.exception.UserNotFoundException;
import com.example.bookshop.struct.enums.ContactType;
import com.example.bookshop.struct.user.RoleEntity;
import com.example.bookshop.struct.user.UserContactEntity;
import com.example.bookshop.struct.user.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class BookstoreUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserContactRepository contactRepository;

    @Autowired
    public BookstoreUserDetailsService(UserRepository userRepository, UserContactRepository contactRepository) {
        this.userRepository = userRepository;
        this.contactRepository = contactRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String contact) throws UsernameNotFoundException {

        UserContactEntity userContact = contactRepository.findFirstByContactAndType(contact, ContactType.EMAIL);
        UserEntity user = userRepository.findUserEntityById(userContact.getUserId().getId());

        if (user != null){
            return new BookstoreUserDetails(userContact);
        }
        Set<GrantedAuthority> authorities = new HashSet<>();
        for (RoleEntity role : userContact.getUserId().getRoles()){
            authorities.add(new SimpleGrantedAuthority(role.getAuthority()));
        }
        userContact = contactRepository.findFirstByContactAndType(contact, ContactType.PHONE);
        user = userRepository.findUserEntityById(userContact.getUserId().getId());
        if (user != null){
            return new PhoneNumberUserDetailsService(userContact);
        }
        else {
            throw new UserNotFoundException("Пользователь не зарегистрирован!!!");
        }

    }
}
