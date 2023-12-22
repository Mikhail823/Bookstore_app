package com.example.bookshop.service.impl;

import com.example.bookshop.repository.RoleRepository;
import com.example.bookshop.service.RoleService;
import com.example.bookshop.struct.user.RoleEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public RoleEntity findRoleByName(String name) {
        return roleRepository.findRoleEntityByName(name);
    }
}
