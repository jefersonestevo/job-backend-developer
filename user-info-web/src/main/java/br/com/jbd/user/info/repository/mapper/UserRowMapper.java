package br.com.jbd.user.info.repository.mapper;

import br.com.jbd.user.info.model.User;
import br.com.jbd.user.info.model.UserInfo;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRowMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        long userId = rs.getLong("ID");

        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(userId);
        userInfo.setName(rs.getString("NAME"));
        userInfo.setLastName(rs.getString("LAST_NAME"));
        userInfo.setEmail(rs.getString("EMAIL"));
        userInfo.setPhoneNumber(rs.getString("PHONE_NUMBER"));

        User user = new User();
        user.setId(userId);
        user.setLogin(rs.getString("LOGIN"));
        user.setUserInfo(userInfo);
        return user;
    }

}
