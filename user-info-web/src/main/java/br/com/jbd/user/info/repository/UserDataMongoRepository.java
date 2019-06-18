package br.com.jbd.user.info.repository;

import br.com.jbd.user.info.dto.UserData;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserDataMongoRepository extends MongoRepository<UserData, Long> {
}
