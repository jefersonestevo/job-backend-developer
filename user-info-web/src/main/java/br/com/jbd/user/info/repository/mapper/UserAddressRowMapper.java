package br.com.jbd.user.info.repository.mapper;

import br.com.jbd.user.info.model.UserAddress;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserAddressRowMapper implements RowMapper<UserAddress> {

    @Override
    public UserAddress mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserAddress userAddress = new UserAddress();
        userAddress.setId(rs.getLong("ID"));
        userAddress.setStreet(rs.getString("STREET"));
        userAddress.setNumber(rs.getString("NUMBER"));
        userAddress.setCity(rs.getString("CITY"));
        userAddress.setState(rs.getString("STATE"));
        userAddress.setCountry(rs.getString("COUNTRY"));
        return userAddress;
    }

}
