package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;

import java.util.Arrays;
import java.util.List;

public class UsersUtil {
    public static final List<User> users = Arrays.asList(
            new User(null, "Admin", "adm@mail.com", "***", Role.ADMIN),
            new User(null, "User1", "user@mail.com", "***", Role.USER)
//            new User(3, "User2", "user@mail.com", "***", Role.USER),
//            new User(4, "User3", "user@mail.com", "***", Role.USER),
//            new User(5, "User4", "user@mail.com", "***", Role.USER),
//            new User(6, "User5", "user@mail.com", "***", Role.USER),
//            new User(7, "User6", "user@mail.com", "***", Role.USER),
//            new User(8, "User7", "user@mail.com", "***", Role.USER)
    );

}
