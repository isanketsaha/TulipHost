package com.tulip.host.web.rest;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tulip.host.data.ActionNotificationDTO;
import com.tulip.host.service.ActionNotificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final ActionNotificationService actionNotificationService;

    @GetMapping
    public ResponseEntity<List<ActionNotificationDTO>> get() {
        return ResponseEntity.ok(actionNotificationService.fetchNotification());
    }
}
