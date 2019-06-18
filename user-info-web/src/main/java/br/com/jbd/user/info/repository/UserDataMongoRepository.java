package br.com.jbd.user.info.repository;

import br.com.jbd.user.info.dto.UserData;
import br.com.jbd.user.info.tracer.Traced;
import br.com.jbd.user.info.tracer.TracedTag;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserDataMongoRepository extends MongoRepository<UserData, Long> {

    @Override
    @Traced(tags = {@TracedTag(name = "db", value = "mongo")})
    Optional<UserData> findById(Long id);

    @Override
    @Traced(tags = {@TracedTag(name = "db", value = "mongo")})
    UserData save(UserData entity);

}
