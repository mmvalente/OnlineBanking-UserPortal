package com.onlinebanking.userportal.service.serviceImpl;

import com.onlinebanking.userportal.dao.RoleDao;
import com.onlinebanking.userportal.dao.UserDao;
import com.onlinebanking.userportal.domain.User;
import com.onlinebanking.userportal.domain.security.UserRole;
import com.onlinebanking.userportal.service.AccountService;
import com.onlinebanking.userportal.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;


@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    public void save(User user) {
        userDao.save(user);
    }


    public User findByUsername(String username) {
        return userDao.findByUsername(username);
    }


    public User findByEmail(String email) {
        return userDao.findByEmail(email);
    }


    public boolean userExists(String username, String email) {
        if (usernameExists(username) || emailExists(email)) {
            return true;
        } else {
            return false;
        }
    }


    public boolean usernameExists(String username) {
        if (null != this.findByUsername(username)) {
            return true;
        } else {
            return false;
        }
    }


    public boolean emailExists(String email) {
        if (null != findByEmail(email)) {
            return true;
        } else {
            return false;
        }
    }


    public User createUser(User user, Set<UserRole> userRoles) {

        User localUser = userDao.findByUsername(user.getUsername());
        if (localUser != null) {
            LOG.info("User with username {} already exists.",  user.getUsername());
        } else {
            String encryptedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encryptedPassword);

            for (UserRole ur : userRoles) {
                roleDao.save(ur.getRole());
            }

            user.getUserRoles().addAll(userRoles);

            user.setPrimaryAccount(accountService.createPrimaryAccount());
            user.setSavingsAccount(accountService.createSavingsAccount());

            localUser = userDao.save(user);
        }

        return localUser;
    }
}
