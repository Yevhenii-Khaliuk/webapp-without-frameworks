package com.khaliuk.service;

import com.khaliuk.model.User;
import java.util.Optional;

public interface UserService {

    Optional<User> authorize(User user);

    Optional<User> addUser(User user);

    Optional<User> findByToken(String token);
}
