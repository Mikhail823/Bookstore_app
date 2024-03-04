package com.example.bookshop.security;

import com.example.bookshop.repository.UserContactRepository;
import com.example.bookshop.repository.UserRepository;
import com.example.bookshop.security.exception.UserNotFoundException;
import com.example.bookshop.struct.enums.ContactType;
import com.example.bookshop.struct.user.UserContactEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class BookstoreUserDetailsService implements UserDetailsService {

    private final UserContactRepository contactRepository;

    @Autowired
    public BookstoreUserDetailsService(UserContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String contact) throws UsernameNotFoundException {

        UserContactEntity userContact = contactRepository.findFirstByContactAndType(contact, ContactType.EMAIL);

        if (userContact != null) {
            return new BookstoreUserDetails(userContact);
        }
        userContact = contactRepository.findFirstByContactAndType(contact, ContactType.PHONE);

        if (userContact != null) {
           return new PhoneNumberUserDetailsService(userContact);
        } else {
            throw new UserNotFoundException("Пользователь не зарегистрирован!!!");
        }
    }
}
