package pccth.code.review.Backend.Service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pccth.code.review.Backend.DTO.Request.*;
import pccth.code.review.Backend.DTO.Response.*;
import pccth.code.review.Backend.Entity.ProjectEntity;
import pccth.code.review.Backend.Entity.UserEntity;
import pccth.code.review.Backend.Repository.UserRepository;
import pccth.code.review.Backend.Util.CookieUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    @Transactional
    public RegisterResponseDTO register(RegisterRequestDTO request) {
        validateDuplicateUser(request);

        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole("USER");
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setCreateAt(new Date());

        userRepository.save(user);

        RegisterResponseDTO response = new RegisterResponseDTO();
        response.setMessage("User registered successfully");
        return response;
    }

    public LoginResponseDTO login(LoginRequestDTO request, HttpServletResponse response) {
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }

        // สร้าง Access Token
        String accessToken = jwtService.generateAccessToken(user.getUsername());

        // สร้าง Refresh Token
        String refreshToken = jwtService.generateRefreshToken(user.getUsername());
        refreshTokenService.save(user.getUsername(), refreshToken);

        // สร้าง Cookie สำหรับ Refresh Token
        response.addHeader("Set-Cookie", CookieUtil.createRefreshTokenCookie(refreshToken).toString());

        // สร้าง Response DTO
        LoginResponseDTO loginResponse = new LoginResponseDTO();
        loginResponse.setAccessToken(accessToken);
        loginResponse.setId(user.getId().toString());
        loginResponse.setUsername(user.getUsername());
        loginResponse.setEmail(user.getEmail());
        loginResponse.setPhone(user.getPhone());
        loginResponse.setRole(user.getRole());
        return loginResponse;

    }

    private void validateDuplicateUser(RegisterRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already in use");
        }

        if (userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Phone number already in use");
        }
    }

    public void resetPassword(String username, ResetPassDTO request) {

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPasswordHash())) {
            throw new RuntimeException("Current password is incorrect");
        }

        user.setPasswordHash(
                passwordEncoder.encode(request.getNewPassword())
        );

        userRepository.save(user);
    }

    // เพิ่ม user
    public RegisterResponseDTO addUser(ManageUserRequestDTO manageUser){
        try {
            UserEntity user = new UserEntity();
            user.setUsername(manageUser.getUsername());
            user.setPasswordHash(passwordEncoder.encode(manageUser.getPassword()));
            user.setEmail(manageUser.getEmail());
            user.setPhone(manageUser.getPhone());
            user.setRole(manageUser.getRole());
            user.setCreateAt(new Date());

            userRepository.save(user);

            RegisterResponseDTO response = new RegisterResponseDTO();
            response.setMessage("User added successfully");
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Server Error", e);
        }
    }

    // ดึงข้อมูล user ทั้งหมด
    public List<UserResponseDTO> allUser() {
        List<UserEntity> user = userRepository.findAll();
        List<UserResponseDTO> dtoList = new ArrayList<>();

        for (UserEntity u : user) {
            UserResponseDTO dto = new UserResponseDTO();
            dto.setId(u.getId());
            dto.setUsername(u.getUsername());
            dto.setEmail(u.getEmail());
            dto.setPhone(u.getPhone());
            dto.setRole(u.getRole());
            dto.setCreateAt(u.getCreateAt());
            dtoList.add(dto);
        }

        return dtoList;
    }

    // แก้ไข user
    public RegisterResponseDTO updateUser(UUID id, UserRequestsDTO userRequestsDTO) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("user not found")); // ตรวจสอบว่ามี id มั้ย
        try {
            entity.setUsername(userRequestsDTO.getUsername());
            entity.setEmail(userRequestsDTO.getEmail());
            entity.setRole(userRequestsDTO.getRole());
            userRepository.save(entity);

            RegisterResponseDTO response = new RegisterResponseDTO();
            response.setMessage("user updated successfully");
            return response;

        } catch (Exception e) {
            throw new RuntimeException("Server Error", e);
        }
    }

    // ลบ user
    public RegisterResponseDTO deleteUser(UUID id) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found")); // ตรวจสอบว่ามี id มั้ย
        try {
            userRepository.delete(entity);

            RegisterResponseDTO response = new RegisterResponseDTO();
            response.setMessage("User deleted successfully");
            return response;

        } catch (Exception e) {
            throw new RuntimeException("Server Error", e);
        }

    }

    // ดึงข้อมูล repository ตาม id
    public UserResponseDTO searchUser(UUID id) {

        UserEntity u = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(u.getId());
        dto.setUsername(u.getUsername());
        dto.setEmail(u.getEmail());
        dto.setPhone(u.getPhone());
        dto.setRole(u.getRole());
        dto.setCreateAt(u.getCreateAt());
        return dto;
    }



}
