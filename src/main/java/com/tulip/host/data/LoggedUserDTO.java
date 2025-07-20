package com.tulip.host.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoggedUserDTO {

    private String idToken;
    private String name;
    private String userId;
    private String authority;
    private boolean resetCredential;
}
