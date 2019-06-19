package br.com.jbd.user.info.service;

import br.com.jbd.user.info.repository.UserDataBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserLoginServiceImpl implements UserLoginService {

    private Long userId;

    @Autowired
    private transient UserDataBaseRepository userDataBaseRepository;

    @Override
    public Long getCurrentUserId() {
        if (this.userId == null) {
            String userLogin = SecurityContextHolder.getContext().getAuthentication().getName();
            this.userId = this.userDataBaseRepository.findIdByLogin(userLogin);
        }
        return this.userId;
    }

}
