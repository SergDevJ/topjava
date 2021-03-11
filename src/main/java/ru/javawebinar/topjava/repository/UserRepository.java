package ru.javawebinar.topjava.repository;

import org.springframework.data.repository.CrudRepository;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.datajpa.CrudUserRepository;

import java.util.List;

public interface UserRepository {
//    static CrudUserRepository crudRepository;

    // null if not found, when updated
    User save(User user);

    // false if not found
    boolean delete(int id);

    // null if not found
    User get(int id);

    // null if not found
    User getByEmail(String email);

    List<User> getAll();

    public User getWithMeal(int id);
}