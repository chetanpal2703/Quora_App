package com.example.quora_app.feature.user;

import com.example.quora_app.core.common.dto.PageResponse;
import com.example.quora_app.core.common.mapper.PageMapper;
import com.example.quora_app.core.exception.BadRequestException;
import com.example.quora_app.core.exception.ResourceAlreadyExistsException;
import com.example.quora_app.core.exception.ResourceNotFoundException;
import com.example.quora_app.feature.user.dto.UserRegistrationRequest;
import com.example.quora_app.feature.user.dto.UserResponse;
import com.example.quora_app.feature.user.dto.UserUpdateRequest;
import com.example.quora_app.feature.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final PageMapper pageMapper;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "name", "username", "email", "createdAt", "updatedAt");



    @Override
    public UserResponse getUserById(UUID id) {
        User user=userRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("user not found"));
        return userMapper.toResponse(user);
    }

    @Override
    public List<UserResponse> getAllUsersWithoutParams() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse).toList();
    }

    @Override
    public UserResponse updateUser(UUID id, UserUpdateRequest userUpdateRequest) {
        User user =userRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("user not found"));
        if (userUpdateRequest.getUsername() != null && !userUpdateRequest.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(userUpdateRequest.getUsername())) {
                throw new ResourceAlreadyExistsException("Username already exists");
            }
            user.setUsername(userUpdateRequest.getUsername());
        }

        if (userUpdateRequest.getEmail() != null && !userUpdateRequest.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(userUpdateRequest.getEmail())) {
                throw new ResourceAlreadyExistsException("Email already exists");
            }
            user.setEmail(userUpdateRequest.getEmail());
        }
        if(userUpdateRequest.getName()!=null){
            user.setName(userUpdateRequest.getName());
        }
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Override
    public void deleteUser(UUID id) {
        User user=userRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("user not found"));
        userRepository.delete(user);
    }

    @Override
    public PageResponse<UserResponse> getAllUsers(int page, int size, String sortBy, String sortDir, String search) {
        if (page < 0) {
            throw new BadRequestException("Page cannot be negative");
        }

        if (size <= 0) {
            throw new BadRequestException("Size must be greater than 0");
        }

        if (size > 100) {
            throw new BadRequestException("Size cannot be greater than 100");
        }

        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new BadRequestException(
                    "Invalid sort field: " + sortBy
            );
        }

        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir)
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<User> userPage;

        if (search == null || search.isBlank()) {
            userPage = userRepository.findAll(pageable);
        } else {
            String keyword = search.trim();
            userPage = userRepository.findByNameContainingIgnoreCaseOrUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword, keyword, pageable);
        }

//        List<UserResponse> users = userPage.getContent()
//                .stream()
//                .map(userMapper::toResponse)
//                .toList();
//
//        return PageResponse.<UserResponse>builder()
//                .content(users)
//                .page(userPage.getNumber())
//                .size(userPage.getSize())
//                .totalElements(userPage.getTotalElements())
//                .totalPages(userPage.getTotalPages())
//                .first(userPage.isFirst())
//                .last(userPage.isLast())
//                .build();
        return pageMapper.toPageResponse(
                userPage,
                userMapper::toResponse
        );
    }
}
