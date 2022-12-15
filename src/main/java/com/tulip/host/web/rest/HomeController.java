package com.tulip.host.web.rest;

import com.tulip.host.security.SecurityUtils;
import java.util.Optional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HomeController {
    //    @GetMapping("/test")
    //    public String test(){
    //         SecurityUtils.getCurrentUserLogin();
    //        return currentUserLogin.get();
    //    }
}
