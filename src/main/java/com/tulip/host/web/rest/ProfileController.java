package com.tulip.host.web.rest;

import com.tulip.host.service.ProfileService;
import com.tulip.host.web.rest.vm.CredentialVM;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/resetCredential")
    public void reset(@Valid @RequestBody CredentialVM credentialVM) {
        profileService.reset(credentialVM);
    }

    @PostMapping("/checkUserId")
    public ResponseEntity<Boolean> checkUserId(@Valid @RequestParam String userId) {
        return ResponseEntity.ok(profileService.checkUserId(userId));
    }
}
