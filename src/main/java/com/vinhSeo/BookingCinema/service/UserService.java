package com.vinhSeo.BookingCinema.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vinhSeo.BookingCinema.dto.request.UserCreateRequest;
import com.vinhSeo.BookingCinema.dto.request.UserPasswordRequest;
import com.vinhSeo.BookingCinema.dto.request.UserUpdateRequest;
import com.vinhSeo.BookingCinema.enums.UserStatus;
import com.vinhSeo.BookingCinema.exception.AppException;
import com.vinhSeo.BookingCinema.exception.ErrorApp;
import com.vinhSeo.BookingCinema.mapper.UserMapper;
import com.vinhSeo.BookingCinema.model.Role;
import com.vinhSeo.BookingCinema.model.User;
import com.vinhSeo.BookingCinema.model.UserHasRole;
import com.vinhSeo.BookingCinema.repository.RoleRepository;
import com.vinhSeo.BookingCinema.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j(topic = "USER_SERVICE")
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final KafkaTemplate<String, JsonNode> kafkaTemplate;

    @Transactional(rollbackFor = Exception.class)
    public User createUser(UserCreateRequest userCreateRequest) throws MessagingException {
        log.info("Creating user: {}", userCreateRequest.getUsername());

        if(userRepository.findByUsername(userCreateRequest.getUsername()) != null) {
            throw new AppException(ErrorApp.USER_USERNAME_EXISTED);
        }

        if(userRepository.findByEmail(userCreateRequest.getEmail()) != null) {
            throw new AppException(ErrorApp.USER_EMAIL_EXISTED);
        }

        User user = userMapper.toUserCreate(userCreateRequest);

        Role userRole = roleRepository.findByName("USER");

        UserHasRole userHasRole = UserHasRole.builder()
                .role(userRole)
                .user(user)
                .build();

        user.getRoles().add(userHasRole);
        user.setUserStatus(UserStatus.NONE);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User result = userRepository.save(user);

        if(result != null) {
//            mailService.verifyMail(user.getEmail(), user.getUsername());
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode verifyEmailMessage = objectMapper.createObjectNode();

            verifyEmailMessage.put("email", user.getEmail());
            verifyEmailMessage.put("username", user.getUsername());

            kafkaTemplate.send("VERIFY_EMAIL_TOPIC", verifyEmailMessage);
        }

        return result;
    }

    public User getUserById(Integer id) {
        log.info("Getting user by id: {}", id);

        return userRepository.findById(id).orElseThrow(() -> new AppException(ErrorApp.USER_NOT_FOUND));
    }

    public Page<User> searchUser(String keyword, Integer number, Integer size, String sortBy, String order) {
        log.info("Searching user with keyword {}", keyword);

        Sort sort = Sort.by(Sort.Direction.valueOf(order.toUpperCase()), sortBy);

        Pageable pageable = PageRequest.of(number, size, sort);

        Page<User> result =  (keyword != null && !keyword.isEmpty())
                ? userRepository.searchUser(keyword, pageable)
                : userRepository.findAll(pageable);

        log.info("Search result: {}", result.getContent());
        return result;
    }

    public User updateUser(Integer id, UserUpdateRequest userUpdateRequest) {
        log.info("Updating user with id {}", id);

        User user = getUserById(id);

        User newUser = userMapper.toUserUpdate(userUpdateRequest);
        newUser.setId(id);
        newUser.setCreatedAt(user.getCreatedAt());
        newUser.setPassword(user.getPassword());
        newUser.setUsername(user.getUsername());

        return userRepository.save(newUser);
    }

    public User changePassword(Integer userId, UserPasswordRequest userPasswordRequest) {
        log.info("Changing password for user: {}", userId);

        User user = getUserById(userId);

        if(!passwordEncoder.matches(userPasswordRequest.getOldPassword(), user.getPassword())) {
            throw new AppException(ErrorApp.USER_OLD_PASSWORD);
        }

        if(passwordEncoder.matches(userPasswordRequest.getPassword(), user.getPassword())) {
            throw new AppException(ErrorApp.USER_CHANGE_PASSWORD);
        }

        if(userPasswordRequest.getPassword().equals(userPasswordRequest.getConfirmPassword())) {
            user.setPassword(passwordEncoder.encode(userPasswordRequest.getPassword()));
        } else {
            throw new AppException(ErrorApp.USER_PASSWORD_CONFIRM);
        }

        return userRepository.save(user);
    }

    public void deleteUser(Integer id) {
        log.info("Deleting user with id: {}", id);

        if(!userRepository.existsById(id)) {
            throw new AppException(ErrorApp.USER_NOT_FOUND);
        }

        userRepository.deleteById(id);
    }


}
