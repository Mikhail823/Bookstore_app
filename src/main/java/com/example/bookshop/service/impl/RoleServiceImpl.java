package com.example.bookshop.service.impl;

import com.example.bookshop.repository.RoleRepository;
import com.example.bookshop.service.RoleService;
import com.example.bookshop.struct.user.RoleEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    @Autowired
    private final RoleRepository roleRepository;

    @Override
    public RoleEntity findRoleByName(String name) {
        return roleRepository.findRoleEntityByName(name);
    }
}
