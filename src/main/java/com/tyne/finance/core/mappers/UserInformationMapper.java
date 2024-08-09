package com.tyne.finance.core.mappers;

import com.tyne.finance.core.dto.UserInformation;
import com.tyne.finance.core.models.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserInformationMapper {
    UserInformation userToUserInformation(User user);
}
