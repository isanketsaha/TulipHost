package com.tulip.host.repository;

import com.tulip.host.domain.UserGroup;
import org.mapstruct.Mapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {
    UserGroup findUserGroupByAuthority(String authority);
}
