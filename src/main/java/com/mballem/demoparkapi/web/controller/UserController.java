package com.mballem.demoparkapi.web.controller;

import com.mballem.demoparkapi.entity.User;
import com.mballem.demoparkapi.service.UserService;
import com.mballem.demoparkapi.web.dto.UserCreateDto;
import com.mballem.demoparkapi.web.dto.UserPasswordDto;
import com.mballem.demoparkapi.web.dto.UserResponseDto;
import com.mballem.demoparkapi.web.dto.mapper.UserMapper;
import com.mballem.demoparkapi.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
@Tag(name = "Users", description = "This set of information includes all the required details for implementing user creation, updating (patching), and reading functionalities in the API documentation.")
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/users")
public class UserController {
    private final UserService userService;
    @Operation(summary = "Create a new user", description = "Resource for creating a new user.",
    responses = {
            @ApiResponse(responseCode = "201", description = "created with successfully",content =
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "e-mail already exists", content = @Content(mediaType = "application/json", schema = @Schema(
                    implementation = ErrorMessage.class
            ))),
            @ApiResponse(responseCode = "422", description = "Resource not processed due to invalid input data", content = @Content(mediaType = "application/json", schema = @Schema(
                    implementation = ErrorMessage.class
            )))
    })
    @PostMapping()
    public ResponseEntity<UserResponseDto> create(@Valid @RequestBody UserCreateDto createDto){
        User userToCreate = UserMapper.toUser(createDto);
        User createdUser = userService.createUser(userToCreate, createDto.getPassword());

        return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.toDto(createdUser));
    }
    @Operation(summary = "Retrieve a user by ID", description = "Resource to search for user by ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "user found with successfully",content =
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "user not found", content =
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class
                    ))),
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getById(@PathVariable Long id){
        User user = userService.findById(id);

        return ResponseEntity.ok(UserMapper.toDto(user));
    }
    @Operation(summary = "Patch password", description = "Resource to patch password by user ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "User password updated successfully.",content =
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))),
                    @ApiResponse(responseCode = "400", description = "Wrong password",content =
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Resource not found", content =
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            })
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updatePassword( @PathVariable Long id,@Valid @RequestBody UserPasswordDto dto){
        User userFound = userService.updatePassword(id, dto.getActualPassword(), dto.getNewPassword(), dto.getConfirmPassword());

        return ResponseEntity.noContent().build();
    }
    @Operation(summary = "Get all users", description = "Resource to get all registered users",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List with all registered users",content =
                    @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = UserResponseDto.class)))),
            })
    @GetMapping()
    public ResponseEntity<List<UserResponseDto>> getAll(){
        List<User> users = userService.getAllUsers();

        return ResponseEntity.ok(UserMapper.toListDto(users));
    }

}
