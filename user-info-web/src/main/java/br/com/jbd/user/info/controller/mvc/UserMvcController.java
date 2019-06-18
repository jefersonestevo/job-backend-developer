package br.com.jbd.user.info.controller.mvc;

import br.com.jbd.user.info.dto.UserData;
import br.com.jbd.user.info.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/mvc/user")
public class UserMvcController {

    @Autowired
    private UserService userService;

    @GetMapping("/info/{userId}")
    public String getUserInfo(@PathVariable("userId") Long userId, Model model) {
        Optional<UserData> userData = userService.findUser(userId);
        if (!userData.isPresent()) {
            return "404";
        }

        model.addAttribute("user", userData.get());
        return "user-info";
    }

}
