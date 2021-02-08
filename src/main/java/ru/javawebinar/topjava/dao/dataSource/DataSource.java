package ru.javawebinar.topjava.dao.dataSource;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface DataSource {
    List<Meal> getMealList();
}
