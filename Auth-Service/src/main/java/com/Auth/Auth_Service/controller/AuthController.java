package com.Auth.Auth_Service.controller;

import com.Auth.Auth_Service.dto.*;
import com.Auth.Auth_Service.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/registeruser")
    public ResponseEntity<ApiResponse<RegisterRespDTO>> register(@Valid @RequestBody RegisterReqDTO req) {
        RegisterRespDTO response = authService.registerUser(req);

        return ResponseEntity.ok(ApiResponse.<RegisterRespDTO>builder()
                .success(true)
                .message("User registered successfully. Please login.")
                .data(response)
                .build());
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginRespDTO>> login(@Valid @RequestBody LoginReqDTO req) {
        LoginRespDTO response = authService.login(req);

        return ResponseEntity.ok(ApiResponse.<LoginRespDTO>builder()
                .success(true)
                .message("Login successful")
                .data(response)
                .build());
    }

    @DeleteMapping("/user/delete/{id}")
    public ResponseEntity<ApiResponse<UserDeleteRespDTO>> deleteUser(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Role", required = false) String role) {

        if (role == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.<UserDeleteRespDTO>builder()
                            .success(false)
                            .message("Unauthorized (missing X-User-Role)")
                            .data(null)
                            .build());
        }

        if (!role.equalsIgnoreCase("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.<UserDeleteRespDTO>builder()
                            .success(false)
                            .message("Only ADMIN can delete users")
                            .data(null)
                            .build());
        }

        UserDeleteRespDTO response = authService.deleteUserById(id);

        return ResponseEntity.ok(ApiResponse.<UserDeleteRespDTO>builder()
                .success(response.isSuccess())
                .message(response.getMessage())
                .data(response)
                .build());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<UserRespDTO>> getUser(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Role", required = false) String role,
            @RequestHeader(value = "X-User-Email", required = false) String emailFromToken) {

        if (role == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.<UserRespDTO>builder()
                            .success(false)
                            .message("Unauthorized (missing X-User-Role)")
                            .data(null)
                            .build());
        }

        if (role.equalsIgnoreCase("ADMIN")) {
            UserRespDTO user = authService.getUserById(id);
            return ResponseEntity.ok(ApiResponse.<UserRespDTO>builder()
                    .success(true)
                    .message("User fetched successfully")
                    .data(user)
                    .build());
        }

        if (emailFromToken == null || emailFromToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.<UserRespDTO>builder()
                            .success(false)
                            .message("Unauthorized (missing X-User-Email)")
                            .data(null)
                            .build());
        }

        UserRespDTO user = authService.getUserById(id);

        if (!user.getEmail().equalsIgnoreCase(emailFromToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.<UserRespDTO>builder()
                            .success(false)
                            .message("You can only access your own profile")
                            .data(null)
                            .build());
        }

        return ResponseEntity.ok(ApiResponse.<UserRespDTO>builder()
                .success(true)
                .message("User fetched successfully")
                .data(user)
                .build());
    }

    @GetMapping("/users/all")
    public ResponseEntity<ApiResponse<AllUsersRespDTO>> getAllUsers(
            @RequestHeader(value = "X-User-Role", required = false) String role) {

        if (role == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.<AllUsersRespDTO>builder()
                            .success(false)
                            .message("Unauthorized (missing X-User-Role)")
                            .data(null)
                            .build());
        }

        if (!role.equalsIgnoreCase("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.<AllUsersRespDTO>builder()
                            .success(false)
                            .message("Only ADMIN can access all users")
                            .data(null)
                            .build());
        }

        AllUsersRespDTO users = authService.getAllUsers();

        return ResponseEntity.ok(ApiResponse.<AllUsersRespDTO>builder()
                .success(true)
                .message("Users fetched successfully")
                .data(users)
                .build());
    }

    @PostMapping("/admin/create")
    public ResponseEntity<ApiResponse<UserRespDTO>> createAdmin(
            @Valid @RequestBody RegisterReqDTO req,
            @RequestHeader(value = "X-User-Role", required = false) String role) {

        if (role == null || !role.equalsIgnoreCase("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.<UserRespDTO>builder()
                            .success(false)
                            .message("Only ADMIN can create another ADMIN")
                            .data(null)
                            .build());
        }

        UserRespDTO admin = authService.createAdmin(req);

        return ResponseEntity.ok(ApiResponse.<UserRespDTO>builder()
                .success(true)
                .message("Admin created successfully")
                .data(admin)
                .build());
    }
}




//package com.Auth.Auth_Service.controller;
//
//import com.Auth.Auth_Service.dto.*;
//import com.Auth.Auth_Service.entity.UserEntity;
//import com.Auth.Auth_Service.jwt.JwtUtil;
//import com.Auth.Auth_Service.service.AuthService;
//import jakarta.validation.Valid;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/auth")
//public class AuthController {
//
//    private final AuthService authService;
//    private final JwtUtil jwtUtil;
//
//    public AuthController(AuthService authService, JwtUtil jwtUtil) {
//        this.authService = authService;
//        this.jwtUtil = jwtUtil;
//    }
//
//    @PostMapping("/registeruser")
//    public ResponseEntity<RegisterRespDTO> register(@Valid @RequestBody RegisterReqDTO req) {
//        RegisterRespDTO response = authService.registerUser(req);
//        return ResponseEntity.ok(response);
//    }
//
//
//
//    @PostMapping("/login")
//    public ResponseEntity<LoginRespDTO> login(
//            @Valid @RequestBody LoginReqDTO req) {
//
//        return ResponseEntity.ok(authService.login(req));
//    }
//
//
//
//    @DeleteMapping("/user/delete/{id}")
//    public ResponseEntity<UserDeleteRespDTO> deleteUser(
//            @PathVariable Long id,
//            @RequestHeader(value = "X-User-Role", required = false) String role) {
//
//        if (role == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body(new UserDeleteRespDTO(false, "Unauthorized"));
//        }
//
//        if (!role.equalsIgnoreCase("ADMIN")) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body(new UserDeleteRespDTO(false, "Only ADMIN can delete users"));
//        }
//
//        UserDeleteRespDTO response = authService.deleteUserById(id);
//        return ResponseEntity.ok(response);
//    }
//
//
//    @GetMapping("/users/{id}")
//    public ResponseEntity<ApiResponse<UserRespDTO>> getUser(
//            @PathVariable Long id,
//            @RequestHeader(value = "X-User-Role", required = false) String role,
//            @RequestHeader(value = "X-User-Email", required = false) String emailFromToken) {
//
//        // 1) Must have role header (gateway should forward it)
//        if (role == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body(ApiResponse.<UserRespDTO>builder()
//                            .success(false)
//                            .message("Unauthorized (missing X-User-Role)")
//                            .data(null)
//                            .build());
//        }
//
//        // 2) Admin can access any user
//        if (role.equalsIgnoreCase("ADMIN")) {
//            UserRespDTO user = authService.getUserById(id);
//            return ResponseEntity.ok(ApiResponse.<UserRespDTO>builder()
//                    .success(true)
//                    .message("User fetched successfully")
//                    .data(user)
//                    .build());
//        }
//
//        // 3) For USER, must have email header for self-check
//        if (emailFromToken == null || emailFromToken.isBlank()) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body(ApiResponse.<UserRespDTO>builder()
//                            .success(false)
//                            .message("Unauthorized (missing X-User-Email)")
//                            .data(null)
//                            .build());
//        }
//
//        // 4) Fetch requested user and compare
//        UserRespDTO user = authService.getUserById(id);
//
//        if (!user.getEmail().equalsIgnoreCase(emailFromToken)) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body(ApiResponse.<UserRespDTO>builder()
//                            .success(false)
//                            .message("You can only access your own profile")
//                            .data(null)
//                            .build());
//        }
//
//        // 5) Success
//        return ResponseEntity.ok(ApiResponse.<UserRespDTO>builder()
//                .success(true)
//                .message("User fetched successfully")
//                .data(user)
//                .build());
//    }
//
//    // Get all users
//    @GetMapping("/users/all")
//    public ResponseEntity<AllUsersRespDTO> getAllUsers(
//            @RequestHeader(value = "X-User-Role", required = false) String role) {
//
//        // Gateway should send this after validating JWT
//        if (role == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//
//        System.out.println("ROLE: " + role);
//
//
//        // Only ADMIN can see all users
//        if (!role.equalsIgnoreCase("ADMIN")) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//        }
//
//        AllUsersRespDTO users = authService.getAllUsers();
//        return ResponseEntity.ok(users);
//    }
//
//    @PostMapping("/admin/create")
//    public ResponseEntity<ApiResponse<UserRespDTO>> createAdmin(
//            @Valid @RequestBody RegisterReqDTO req,
//            @RequestHeader(value = "X-User-Role", required = false) String role) {
//
//        // Only ADMIN can create another ADMIN
//        if (role == null || !role.equalsIgnoreCase("ADMIN")) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body(ApiResponse.<UserRespDTO>builder()
//                            .success(false)
//                            .message("Only ADMIN can create another ADMIN")
//                            .data(null)
//                            .build());
//        }
//
//        UserRespDTO admin = authService.createAdmin(req);
//
//        return ResponseEntity.ok(ApiResponse.<UserRespDTO>builder()
//                .success(true)
//                .message("Admin created successfully")
//                .data(admin)
//                .build());
//    }
//
//}


