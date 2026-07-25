package com.hostel.mapper;

import com.hostel.models.Hostel;
import com.hostel.web.request.CreateHostelRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface HostelMapper {

    Hostel toEntity(CreateHostelRequest request);

    CreateHostelRequest toDto(Hostel entity);


}
