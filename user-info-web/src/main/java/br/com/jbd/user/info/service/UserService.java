package br.com.jbd.user.info.service;

import br.com.jbd.user.info.dto.UserData;

import java.util.Optional;

public interface UserService {

    Optional<UserData> findUser(Long id);

}
