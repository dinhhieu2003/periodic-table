package com.periodic.backend.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

import com.periodic.backend.domain.entity.User;
import com.periodic.backend.domain.response.user.GetUserResponse;
import com.periodic.backend.domain.response.user.ToggleActiveResponse;
import com.periodic.backend.domain.response.user.UpdateUserResponse;
import com.periodic.backend.domain.response.user.UpdateUserRoleResponse;

@Component
@Mapper(componentModel = "spring")
public interface UserMapper {
	UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
	
	UpdateUserRoleResponse userToUpdateUserRoleResponse(User user);
	GetUserResponse userToGetUserResponse(User user);
	UpdateUserResponse userToUpdateUserResponse(User user);
	default ToggleActiveResponse userToToggleActiveResponse(User user) {
		return new ToggleActiveResponse(user.getId(), user.isActive());
	}
	
	default Page<GetUserResponse> pageUserToPageGetUserResponse(Page<User> pageUser) {
		List<GetUserResponse> content = pageUser.getContent()
				.stream()
				.map(this::userToGetUserResponse)
				.collect(Collectors.toList());
		return new PageImpl<>(content, pageUser.getPageable(), pageUser.getTotalElements());
	}
}
