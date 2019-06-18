package br.com.jbd.user.info.controller.api;

import br.com.jbd.user.info.dto.UserData;
import br.com.jbd.user.info.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserRestController {

    @Autowired
    private UserService userService;

    @GetMapping(path = "/info/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody
    ResponseEntity<UserData> getUserInfo(@PathVariable("userId") Long userId) {
        return userService.findUser(userId)
                .map(ud -> ResponseEntity.ok().body(ud))
                .orElse(ResponseEntity.notFound().build());
    }

}
