package br.com.jbd.user.info.controller.web;

import br.com.jbd.user.info.model.User;
import br.com.jbd.user.info.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/web/user")
public class UserInfoController {

    @Autowired
    private UserService userService;

    @GetMapping("/info/{userId}")
    public String getUserInfo(@PathVariable("userId") Long userId, Model model) {
        Optional<User> user = userService.findUser(userId);
        if (!user.isPresent()) {
            return "404";
        }

        model.addAttribute("user", user.get());
        return "user-info";
    }

}
