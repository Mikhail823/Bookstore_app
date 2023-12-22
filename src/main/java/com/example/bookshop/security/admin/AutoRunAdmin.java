package com.example.bookshop.security.admin;

import com.example.bookshop.repository.RoleRepository;
import com.example.bookshop.repository.UserContactRepository;
import com.example.bookshop.repository.UserRepository;
import com.example.bookshop.struct.enums.ContactType;
import com.example.bookshop.struct.user.RoleEntity;
import com.example.bookshop.struct.user.UserContactEntity;
import com.example.bookshop.struct.user.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

@Component
@Order(3)
public class AutoRunAdmin implements CommandLineRunner{

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AdminConfig adminDataConfig;
    private final PasswordEncoder passwordEncoder;
    private final UserContactRepository userContactRepository;

    @Autowired
    public AutoRunAdmin(UserRepository userRepository,
                        RoleRepository roleRepository,
                        AdminConfig adminDataConfig,
                        PasswordEncoder passwordEncoder,
                        UserContactRepository userContactRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.adminDataConfig = adminDataConfig;
        this.passwordEncoder = passwordEncoder;
        this.userContactRepository = userContactRepository;
    }

    @Override
    public void run(String... args){

            UserEntity adm = userRepository.getUserByUsername(adminDataConfig.getLogin());
            if (adm == null) {
                saveAdmin();
                saveContactPhoneAdmin(adminDataConfig);
                saveContactEmailAdmin(adminDataConfig);
            }
    }


    public void saveAdmin(){
        RoleEntity role = roleRepository.findRoleEntityByName("ROLE_ADMIN");
        Set<RoleEntity> roles = new HashSet<>();
        roles.add(role);
        UserEntity adm = new UserEntity();
        adm.setHash(generateString());
        adm.setBalance(0);
        adm.setRegTime(new Date());
        adm.setPassword(passwordEncoder.encode(adminDataConfig.getPass()));
        adm.setName(adminDataConfig.getLogin());
        adm.setRoles(roles);
        userRepository.save(adm);
    }

    public void saveContactPhoneAdmin(AdminConfig adminDataConfig){
        UserEntity admin = userRepository.getUserByUsername(adminDataConfig.getLogin());

        UserContactEntity admContactPhone = new UserContactEntity();
        admContactPhone.setContact(adminDataConfig.getPhone());
        admContactPhone.setApproved((short)1);
        admContactPhone.setType(ContactType.PHONE);
        admContactPhone.setCode("25 12");
        admContactPhone.setCodeTime(LocalDateTime.now());
        admContactPhone.setUserId(admin);
        admContactPhone.setCodeTrails(1);
        userContactRepository.save(admContactPhone);
    }

    public void saveContactEmailAdmin(AdminConfig adminDataConfig){
        UserEntity admin = userRepository.getUserByUsername(adminDataConfig.getLogin());

        UserContactEntity admContactPhone = new UserContactEntity();
        admContactPhone.setContact(adminDataConfig.getEmail());
        admContactPhone.setApproved((short)1);
        admContactPhone.setType(ContactType.EMAIL);
        admContactPhone.setCode("478 456");
        admContactPhone.setCodeTime(LocalDateTime.now());
        admContactPhone.setUserId(admin);
        admContactPhone.setCodeTrails(1);
        userContactRepository.save(admContactPhone);
    }

    public static String generateString() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replace("-", "");
    }
}
