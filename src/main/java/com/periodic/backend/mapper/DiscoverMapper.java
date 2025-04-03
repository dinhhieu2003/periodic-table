package com.periodic.backend.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

import com.periodic.backend.domain.entity.Discover;
import com.periodic.backend.domain.request.discover.CreateDiscoverRequest;
import com.periodic.backend.domain.request.discover.UpdateDiscoverRequest;
import com.periodic.backend.domain.response.discover.CreateDiscoverResponse;
import com.periodic.backend.domain.response.discover.GetDiscoverResponse;
import com.periodic.backend.domain.response.discover.ToggleActiveDiscoverResponse;
import com.periodic.backend.domain.response.discover.UpdateDiscoverResponse;

@Component
@Mapper(componentModel = "spring")
public interface DiscoverMapper {
    DiscoverMapper INSTANCE = Mappers.getMapper(DiscoverMapper.class);
    
    @Mapping(target = "scientist", ignore = true)
    @Mapping(target = "element", ignore = true)
    Discover createDiscoverRequestToDiscover(CreateDiscoverRequest createDiscoverRequest);
    
    @Mapping(target = "scientist", ignore = true)
    @Mapping(target = "element", ignore = true)
    Discover updateDiscoverRequestToDiscover(UpdateDiscoverRequest updateDiscoverRequest);
    
    @Mapping(source = "scientist.name", target = "scientistName")
    @Mapping(source = "element.name", target = "elementName")
    @Mapping(source = "element.symbol", target = "elementSymbol")
    CreateDiscoverResponse discoverToCreateDiscoverResponse(Discover discover);
    
    @Mapping(source = "scientist.name", target = "scientistName")
    @Mapping(source = "element.name", target = "elementName")
    @Mapping(source = "element.symbol", target = "elementSymbol")
    GetDiscoverResponse discoverToGetDiscoverResponse(Discover discover);
    
    @Mapping(source = "scientist.name", target = "scientistName")
    @Mapping(source = "element.name", target = "elementName")
    @Mapping(source = "element.symbol", target = "elementSymbol")
    UpdateDiscoverResponse discoverToUpdateDiscoverResponse(Discover discover);
    
    default Page<GetDiscoverResponse> pageDiscoverToPageGetDiscoverResponse(Page<Discover> pageDiscover) {
        List<GetDiscoverResponse> content = pageDiscover.getContent()
                .stream()
                .map(this::discoverToGetDiscoverResponse)
                .collect(Collectors.toList());
        return new PageImpl<>(content, pageDiscover.getPageable(), pageDiscover.getTotalElements());
    }
    
    default ToggleActiveDiscoverResponse discoverToToggleActiveDiscoverResponse(Discover discover) {
        return new ToggleActiveDiscoverResponse(discover.getId(), discover.isActive());
    }
} 