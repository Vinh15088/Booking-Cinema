package com.vinhSeo.BookingCinema.controller;

import com.vinhSeo.BookingCinema.dto.request.UserCreateRequest;
import com.vinhSeo.BookingCinema.dto.request.UserPasswordRequest;
import com.vinhSeo.BookingCinema.dto.request.UserUpdateRequest;
import com.vinhSeo.BookingCinema.dto.response.DataApiResponse;
import com.vinhSeo.BookingCinema.dto.response.UserResponse;
import com.vinhSeo.BookingCinema.mapper.UserMapper;
import com.vinhSeo.BookingCinema.model.User;
import com.vinhSeo.BookingCinema.service.UserService;
import com.vinhSeo.BookingCinema.utils.PageInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/user")
@Validated
@Tag(name = "User Controller")
@Slf4j(topic = "USE_CONTROLLER")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    private static final String PAGE_SIZE = "10";
    private static final String PAGE_NUMBER = "1";

    @Operation(method = "POST", summary = "Create new user",
            description = "Send a request via this API to create new user")
    @PostMapping("/register")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserCreateRequest userCreateRequest) throws MessagingException {
        log.info("Create new movie");

        User user = userService.createUser(userCreateRequest);

        UserResponse userResponse = userMapper.toUserResponse(user);

        DataApiResponse<?> dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.CREATED.value())
                .timestamp(new Date())
                .message("User created successfully")
                .data(userResponse)
                .build();
        return ResponseEntity.ok().body(dataApiResponse);
    }

    @Operation(method = "GET", summary = "Get user by Id",
            description = "Send a request via this API to get user by Id")
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(
            @PathVariable @Min(value = 1, message = "id must be greater than 0")  Integer id) {
        log.info("Get user by Id");;

        User user = userService.getUserById(id);

        UserResponse userResponse = userMapper.toUserResponse(user);

        DataApiResponse<?> dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .timestamp(new Date())
                .message("Get user by Id successfully")
                .data(userResponse)
                .build();

        return ResponseEntity.ok().body(dataApiResponse);
    }

    @Operation(method = "GET", summary = "Get users with optional pagination and search",
            description = "Send a request via this API to get users. You can optionally paginate and search.")
    @GetMapping("/list")
    public ResponseEntity<?> getMovies(
            @RequestParam(name = "keyword") String keyword,
            @RequestParam(name = "size", defaultValue = PAGE_SIZE) Integer size,
            @RequestParam(name = "number", defaultValue = PAGE_NUMBER) Integer number,
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(name = "order", defaultValue = "asc") String order
    ) {
        log.info("Get users with optional pagination and search");

        Page<User> userPage = userService.searchUser(keyword, number-1, size, sortBy, order);

        List<User> userList = userPage.getContent();

        List<UserResponse> userResponses = userList.stream().map(userMapper::toUserResponse).toList();

        PageInfo pageInfo = null;
        if (userPage != null) {
            pageInfo = PageInfo.builder()
                    .pageSize(userPage.getSize())
                    .pageNumber(userPage.getNumber() + 1)
                    .totalPages(userPage.getTotalPages())
                    .totalElements(userPage.getNumberOfElements())
                    .build();
        }

        DataApiResponse<?> dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .timestamp(new Date())
                .message("Search users successfully")
                .data(userResponses)
                .pageInfo(pageInfo)
                .build();

        return ResponseEntity.ok().body(dataApiResponse);
    }

    @Operation(method = "PUT", summary = "Update user by Id",
            description = "Send a request via this API to update user by Id")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable @Min(value = 1, message = "id must be greater than 0")  int id,
                                         @Valid @RequestBody UserUpdateRequest userUpdateRequest) {
        log.info("Update user by Id");;

        User user = userService.updateUser(id, userUpdateRequest);

        UserResponse userResponse = userMapper.toUserResponse(user);

        DataApiResponse<?> dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.ACCEPTED.value())
                .timestamp(new Date())
                .message("Update user successfully")
                .data(userResponse)
                .build();

        return ResponseEntity.ok().body(dataApiResponse);
    }

    @Operation(method = "PATCH", summary = "Change password by user",
            description = "Send a request via this API to change password")
    @PatchMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody UserPasswordRequest userPasswordRequest) {
        log.info("Change password");
        User user = userService.changePassword(userPasswordRequest);

        UserResponse userResponse = userMapper.toUserResponse(user);

        DataApiResponse<?> dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.NO_CONTENT.value())
                .timestamp(new Date())
                .message("Change password successfully")
                .data(userResponse)
                .build();

        return ResponseEntity.ok().body(dataApiResponse);
    }

    @Operation(method = "DELETE", summary = "Delete user by Id",
            description = "Send a request via this API to delete user by Id")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable @Min(value = 1, message = "id must be greater than 0")  Integer id) {
        log.info("Delete user by Id");;

        userService.deleteUser(id);

        DataApiResponse<?> dataApiResponse = DataApiResponse.builder()
                .success(true)
                .code(HttpStatus.RESET_CONTENT.value())
                .timestamp(new Date())
                .message("Delete user successfully")
                .build();

        return ResponseEntity.ok().body(dataApiResponse);
    }

}
