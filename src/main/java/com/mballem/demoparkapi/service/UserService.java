package com.mballem.demoparkapi.service;

import com.mballem.demoparkapi.entity.User;
import com.mballem.demoparkapi.expection.EntityNotFoundException;
import com.mballem.demoparkapi.expection.PasswordInvalidException;
import com.mballem.demoparkapi.expection.UsernameUniqueViolationException;
import com.mballem.demoparkapi.repository.UserRepository;
import com.mballem.demoparkapi.web.dto.UserCreateDto;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Transactional
    public User createUser(User user, String password) {
        try{
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return userRepository.save(user);
        }catch (org.springframework.dao.DataIntegrityViolationException ex){
            throw new UsernameUniqueViolationException(String.format("Username %s yet exists", user.getUsername()));
        }

    }
    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("User id=%s yet exists", id))
        );
    }

    @Transactional
    public User updatePassword(Long id, String currentPassword, String newPassword, String confirmPassword) {
        if(currentPassword.equals(newPassword)){
            throw new PasswordInvalidException("you don't can use your actual password");
        }
        if (!newPassword.equals(confirmPassword)){
            throw new PasswordInvalidException("New password don't match");
        }

        User user = findById(id);
        if (!passwordEncoder.matches(currentPassword, user.getPassword())){
            throw new PasswordInvalidException("passwords don't match");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        return user;
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username){
        return userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException(String.format("User %s don't exists", username))
        );
    }

    @Transactional(readOnly = true)
    public User.Role findRoleByUsername(String username) {
        return userRepository.findRoleByUsername(username);
    }
}
