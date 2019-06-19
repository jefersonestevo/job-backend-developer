package br.com.jbd.user.info.repository;

import br.com.jbd.user.info.model.User;
import br.com.jbd.user.info.model.UserAddress;
import br.com.jbd.user.info.repository.mapper.UserAddressRowMapper;
import br.com.jbd.user.info.repository.mapper.UserRowMapper;
import br.com.jbd.user.info.tracer.Traced;
import br.com.jbd.user.info.tracer.TracedTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserDataBaseRepository extends NamedParameterJdbcDaoSupport {

    @Autowired
    public UserDataBaseRepository(DataSource dataSource) {
        setDataSource(dataSource);
    }

    @Traced(tags = {@TracedTag(name = "db", value = "postgres")})
    public Optional<User> findById(Long id) {
        String sql = " SELECT U.ID, U.LOGIN, I.ID AS USER_INFO_ID, I.NAME, I.LAST_NAME, I.EMAIL, I.PHONE_NUMBER " +
                " FROM JBD_USER U " +
                " LEFT JOIN JBD_USER_INFO I ON U.ID = I.USER_ID " +
                " WHERE U.ID = :ID ";

        Map<String, Object> params = new HashMap<>();
        params.put("ID", id);

        try {
            User user = getNamedParameterJdbcTemplate().queryForObject(sql, params, new UserRowMapper());
            user.setAddresses(this.findUserAdresses(id));
            return Optional.of(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private List<UserAddress> findUserAdresses(Long id) {
        String sql = " SELECT * FROM JBD_USER_ADDRESS WHERE ID = :ID ";

        Map<String, Object> params = new HashMap<>();
        params.put("ID", id);

        return getNamedParameterJdbcTemplate().query(sql, params, new UserAddressRowMapper());
    }

    @Traced(tags = {@TracedTag(name = "db", value = "postgres")})
    public void registerImport(Long id) {
        String sql = " INSERT INTO JBD_USER_IMPORT(ID) VALUES(:ID) ";

        Map<String, Object> params = new HashMap<>();
        params.put("ID", id);

        getNamedParameterJdbcTemplate().update(sql, params);
    }

    public Long findIdByLogin(String login) {
        String sql = " SELECT ID FROM JBD_USER WHERE LOGIN = :LOGIN ";

        Map<String, Object> params = new HashMap<>();
        params.put("LOGIN", login);

        try {
            return getNamedParameterJdbcTemplate().queryForObject(sql, params, Long.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
