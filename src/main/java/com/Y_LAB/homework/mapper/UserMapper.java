package com.Y_LAB.homework.mapper;

import com.Y_LAB.homework.model.dto.request.AdminRequestDTO;
import com.Y_LAB.homework.model.dto.request.UserRequestDTO;
import com.Y_LAB.homework.model.dto.response.UserResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface UserMapper {
    UserResponseDTO toUserResponseDTO(UserRequestDTO userRequestDTO);

    AdminRequestDTO toAdminRequestDTO(UserRequestDTO userRequestDTO);
}