package com.tulip.host.data;

import java.util.List;
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
    private String userName;
    private String userId;
    private String authority;
    private boolean resetCredential;
}
