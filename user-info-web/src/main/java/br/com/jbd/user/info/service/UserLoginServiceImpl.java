package br.com.jbd.user.info.service;

import br.com.jbd.user.info.repository.UserDataBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class UserLoginServiceImpl implements UserLoginService {

    private static final String SESSION_USER_ID = "jbd.user.id";

    @Autowired
    private UserDataBaseRepository userDataBaseRepository;

    @Override
    public Long getCurrentUserId() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();

        Long userId = (Long) attr.getAttribute(SESSION_USER_ID, RequestAttributes.SCOPE_SESSION);

        if (userId == null) {
            String userLogin = SecurityContextHolder.getContext().getAuthentication().getName();
            userId = this.userDataBaseRepository.findIdByLogin(userLogin);

            attr.setAttribute(SESSION_USER_ID, userId, RequestAttributes.SCOPE_SESSION);
        }

        return userId;
    }

}
