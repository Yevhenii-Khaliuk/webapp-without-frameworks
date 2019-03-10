package com.khaliuk.dao;

import com.khaliuk.model.User;

public interface UserDao {

    User addUser (User user);

    User getByToken(String token);

}
