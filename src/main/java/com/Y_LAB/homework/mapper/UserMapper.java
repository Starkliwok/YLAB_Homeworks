package com.Y_LAB.homework.mapper;

import com.Y_LAB.homework.model.dto.request.AdminRequestDTO;
import com.Y_LAB.homework.model.dto.request.UserRequestDTO;
import com.Y_LAB.homework.model.dto.response.UserResponseDTO;
import com.Y_LAB.homework.model.roles.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDTO toUserResponseDTO(UserRequestDTO userRequestDTO);

    UserResponseDTO toUserResponseDTO(User user);

    List<UserResponseDTO> toUserResponseDTOList(List<User> users);

    AdminRequestDTO toAdminRequestDTO(UserRequestDTO userRequestDTO);
}