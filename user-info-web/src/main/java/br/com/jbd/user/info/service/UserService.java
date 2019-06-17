package br.com.jbd.user.info.service;

import br.com.jbd.user.info.model.User;
import br.com.jbd.user.info.repository.UserDataBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserService {

    @Autowired
    private UserDataBaseRepository userDataBaseRepository;

    public Optional<User> findUser(Long id) {
        return userDataBaseRepository.findById(id);
    }
    
}
