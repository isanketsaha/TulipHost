package com.tulip.host.data.dto;

import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoggedUserDTO {

    private String idToken;

    private String userName;

    private String userId;

    private List<String> authority;
}
