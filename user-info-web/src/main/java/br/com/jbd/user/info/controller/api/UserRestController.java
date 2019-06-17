package br.com.jbd.user.info.controller.api;

import br.com.jbd.user.info.model.User;
import br.com.jbd.user.info.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserRestController {

    @Autowired
    private UserService userService;

    @GetMapping(path = "/info/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody
    User getUserInfo(@PathVariable("userId") Long userId) {
        return userService.findUser(userId).orElse(null);
    }

}
