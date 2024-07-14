package com.Y_LAB.homework.mapper;

import com.Y_LAB.homework.model.dto.request.AdminRequestDTO;
import com.Y_LAB.homework.model.dto.request.UserRequestDTO;
import com.Y_LAB.homework.model.dto.response.AdminResponseDTO;
import com.Y_LAB.homework.model.dto.response.UserResponseDTO;
import com.Y_LAB.homework.model.roles.User;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Интерфейс для преобразования сущностей пользователей в DTO объект и наоборот
 * @author Денис Попов
 * @version 1.0
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Преобразует DTO Request объект пользователя в DTO Response объект пользователя
     * @param userRequestDTO DTO Request объект пользователя
     * @return DTO Response объект пользователя
     */
    UserResponseDTO toUserResponseDTO(UserRequestDTO userRequestDTO);

    /**
     * Преобразует объект пользователя в DTO Response объект пользователя
     * @param user Пользователь
     * @return DTO Response объект пользователя
     */
    UserResponseDTO toUserResponseDTO(User user);

    /**
     * Преобразует список объектов пользователей в список DTO Response объектов пользователей
     * @param users Список объектов пользователей
     * @return Список DTO Response объектов пользователей
     */
    List<UserResponseDTO> toUserResponseDTOList(List<User> users);

    /**
     * Преобразует DTO Request объект администратора в DTO Response объект администратора
     * @param adminRequestDTO DTO Request объект администратора
     * @return DTO Response объект администратора
     */
    AdminResponseDTO toAdminResponseDTO(AdminRequestDTO adminRequestDTO);

    /**
     * Преобразует DTO Request объект пользователя в DTO Request объект администратора
     * @param userRequestDTO DTO Request объект пользователя
     * @return DTO Request объект администратора
     */
    AdminRequestDTO toAdminRequestDTO(UserRequestDTO userRequestDTO);
}