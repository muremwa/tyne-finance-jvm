package com.tyne.finance.configurations;


import com.tyne.finance.core.models.Group;
import com.tyne.finance.core.repositories.GroupRepository;
import com.tyne.finance.exceptions.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class Config {
    @Value("${tyne.auth.normal-user-group-name}")
    private String userGroupName;

    final private GroupRepository groupRepository;

    @Autowired
    public Config(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Bean
    public Group defaultUserGroup() throws AuthenticationException {
        List<Group> groups = this.groupRepository.findGroupsByName(this.userGroupName);

        if (groups.size() != 1) {
            throw new AuthenticationException("Default user group not found: found " + groups.size());
        }
        return groups.get(0);
    }
}
