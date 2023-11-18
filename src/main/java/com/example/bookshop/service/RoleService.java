package com.example.bookshop.service;

import com.example.bookshop.struct.user.RoleEntity;

public interface RoleService {

    RoleEntity findRoleByName(String name);
}
