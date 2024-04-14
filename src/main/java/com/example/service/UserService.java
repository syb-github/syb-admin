package com.example.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.BCrypt;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.crypto.symmetric.AES;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.Permission;
import com.example.entity.Role;
import com.example.entity.User;
import com.example.exception.CustomException;
import com.example.mapper.UserMapper;
import com.example.utils.CryptoUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@Service
public class UserService extends ServiceImpl<UserMapper, User> {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RoleService roleService;

    @Resource
    private PermissionService permissionService;

    public User login(User user) {
        LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>lambdaQuery().eq(User::getUsername, user.getUsername());
        User one = getOne(queryWrapper);
//        String decodePassword = new String(Base64Utils.decode(user.getPassword().getBytes()));

        String decodePassword = CryptoUtil.decrypt(user.getPassword());
        String password = SecureUtil.md5().digestHex(decodePassword, Charset.forName("UTF-8"));
        if (one == null || !password.equals(one.getPassword())) {
            throw new CustomException("-1", "账号或密码错误");
        }
        one.setPermission(getPermissions(one.getId()));
        return one;
    }

    public User register(User user) {
        User one = getOne((Wrappers.<User>lambdaQuery().eq(User::getUsername, user.getUsername())));
        if (one != null) {
            throw new CustomException("-1", "用户已注册");
        }
        if (user.getPassword() == null) {
            user.setPassword("123456");
        }
        user.setRole(CollUtil.newArrayList(2L));  // 默认普通用户角色
        save(user);
        return getOne((Wrappers.<User>lambdaQuery().eq(User::getUsername, user.getUsername())));
    }

    /**
     * 设置权限
     *
     * @param userId
     * @return
     */
    public List<Permission> getPermissions(Long userId) {
        User user = getById(userId);
        List<Permission> permissions = new ArrayList<>();
        List<Long> role = user.getRole();
        if (role != null) {
            for (Object roleId : role) {
                Role realRole = roleService.getById((int) roleId);
                if (CollUtil.isNotEmpty(realRole.getPermission())) {
                    for (Object permissionId : realRole.getPermission()) {
                        Permission permission = permissionService.getById((int) permissionId);
                        if (permission != null && permissions.stream().noneMatch(p -> p.getPath().equals(permission.getPath()))) {
                            permissions.add(permission);
                        }
                    }
                }
            }
            user.setPermission(permissions);
        }
        return permissions;
    }

    public User getbyUsername(String username) {
        User one = getOne((Wrappers.<User>lambdaQuery().eq(User::getUsername, username)));
        one.setPermission(getPermissions(one.getId()));
        return one;
    }


    public static void main(String[] args) {
        System.out.println(SecureUtil.md5().digestHex("admin123456", Charset.forName("UTF-8")));
        String password = new String(Base64Utils.decode(Base64Utils.encode("admin123456".getBytes())), Charset.forName("UTF-8"));
        System.out.println(password);
    }
}
