package br.com.jbd.user.info.controller.mvc;

import br.com.jbd.user.info.dto.UserData;
import br.com.jbd.user.info.service.UserLoginService;
import br.com.jbd.user.info.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Controller
public class UserMvcController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserLoginService userLoginService;

    @GetMapping("/home")
    public String home(Model model) {
        Optional<UserData> userData = userService.findUser(userLoginService.getCurrentUserId());
        if (!userData.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        model.addAttribute("user", userData.get());
        return "home";
    }

}
