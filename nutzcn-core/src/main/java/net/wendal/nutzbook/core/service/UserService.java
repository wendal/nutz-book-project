package net.wendal.nutzbook.core.service;

import net.wendal.nutzbook.core.bean.User;

public interface UserService {

    User add(String name, String password);

    long fetch(String username, String password);

    void updatePassword(int userId, String password);

    boolean checkPassword(User user, String password);

    User fetch(long uid);

    int getUserScore(long userId);

}