package com.periodic.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import com.periodic.backend.domain.entity.User;
import com.periodic.backend.domain.response.user.ToggleActiveResponse;
import com.periodic.backend.domain.response.user.UpdateUserRoleResponse;

@Component
@Mapper(componentModel = "spring")
public interface UserMapper {
	UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
	
	UpdateUserRoleResponse userToUpdateUserRoleResponse(User user);
	default ToggleActiveResponse userToToggleActiveResponse(User user) {
		return new ToggleActiveResponse(user.getId(), user.isActive());
	}
}
