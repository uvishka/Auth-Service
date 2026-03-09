package com.Auth.Auth_Service.service;
import com.Auth.Auth_Service.entity.Role;
import com.Auth.Auth_Service.dto.*;
import com.Auth.Auth_Service.entity.UserEntity;
import com.Auth.Auth_Service.jwt.JwtUtil;
import com.Auth.Auth_Service.repo.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;


import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // Register
    public RegisterRespDTO registerUser(RegisterReqDTO request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered: " + request.getEmail());
        }

        UserEntity user = UserEntity.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .companyName(request.getCompanyName())
                .contactNumber(request.getContactNumber())
                .owner(request.getOwner())
                .build();

        userRepository.save(user);

        UserRespDTO userResp = UserRespDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .companyName(user.getCompanyName())
                .contactNumber(user.getContactNumber())
                .owner(user.getOwner())
                .build();

        return RegisterRespDTO.builder()
                .message("User registered successfully. Please login.")
                .user(userResp)
                .build();
    }

    public LoginRespDTO login(LoginReqDTO req) {

        UserEntity user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        // Generate token
        String token = jwtUtil.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );

        // Convert to DTO
        UserRespDTO userResp = UserRespDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .companyName(user.getCompanyName())
                .contactNumber(user.getContactNumber())
                .owner(user.getOwner())
                .build();

        return LoginRespDTO.builder()
                .message("Login successful")
                .token(token)
                .user(userResp)
                .build();
    }



    public UserDeleteRespDTO deleteUserById(Long id) {

        Optional<UserEntity> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()) {
            return new UserDeleteRespDTO(false, "User not found with id: " + id);
        }

        userRepository.deleteById(id);

        return new UserDeleteRespDTO(true, "User deleted successfully");
    }


    // Get single user by Id
    public UserRespDTO getUserById(Long id) {

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return UserRespDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .companyName(user.getCompanyName())
                .contactNumber(user.getContactNumber())
                .owner(user.getOwner())
                .build();
    }

    // Get all users
    public AllUsersRespDTO getAllUsers() {
        List<UserRespDTO> userList = userRepository.findAll().stream()
                .map(user -> UserRespDTO.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .role(user.getRole().name())
                        .companyName(user.getCompanyName())
                        .contactNumber(user.getContactNumber())
                        .owner(user.getOwner())
                        .build())
                .toList();

        return new AllUsersRespDTO(userList);
    }

    public UserRespDTO createAdmin(RegisterReqDTO request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered: " + request.getEmail());
        }

        UserEntity user = UserEntity.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ADMIN)   // 🔥 Force ADMIN role
                .companyName(request.getCompanyName())
                .contactNumber(request.getContactNumber())
                .owner(request.getOwner())
                .build();

        userRepository.save(user);

        return UserRespDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .companyName(user.getCompanyName())
                .contactNumber(user.getContactNumber())
                .owner(user.getOwner())
                .build();
    }

}
