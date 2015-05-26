package com.ericpol.notifier.data.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.ericpol.notifier.model.User;

/**
 * Row mapper for User class.
 */
public class UserMapper implements RowMapper<User>
{
    @Override
    public final User mapRow(final ResultSet aResultSet, final int aRowNum) throws SQLException
    {
        User user = new User();
        user.setId(aResultSet.getInt("iduser"));
        user.setName(aResultSet.getString("username"));
        user.setSipNumber(aResultSet.getInt("sip"));
        user.setNotified(aResultSet.getBoolean("notified"));
        return user;
    }
}
