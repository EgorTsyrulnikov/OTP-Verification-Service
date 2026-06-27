package com.promoit.otp.service;

import com.promoit.otp.dao.UserDao;
import com.promoit.otp.model.User;
import com.promoit.otp.security.PasswordEncoder;
import com.promoit.otp.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Autowired
    public UserService(UserDao userDao, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public User registerUser(String login, String password, String role) throws SQLException {
        if ("ADMIN".equals(role) && userDao.adminExists()) {
            throw new IllegalArgumentException("An ADMIN already exists.");
        }
        
        Optional<User> existing = userDao.findByLogin(login);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("User with this login already exists.");
        }

        String hash = passwordEncoder.encode(password);
        User user = new User(null, login, hash, role);
        return userDao.save(user);
    }

    public String login(String login, String password) throws SQLException {
        Optional<User> userOpt = userDao.findByLogin(login);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPasswordHash())) {
                return tokenService.generateToken(user);
            }
        }
        throw new IllegalArgumentException("Invalid login or password.");
    }

    public List<User> getAllNonAdmins() throws SQLException {
        return userDao.findAllUsersExceptAdmins();
    }

    public void deleteUser(Long id) throws SQLException {
        userDao.deleteById(id);
    }
}
