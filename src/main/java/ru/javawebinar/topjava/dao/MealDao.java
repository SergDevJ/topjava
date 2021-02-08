package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.dao.dataSource.DataSource;
import ru.javawebinar.topjava.dao.dataSource.MemoryDataSource;
import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public class MealDao {
    private final DataSource dataSource = MemoryDataSource.getInstance();

    public List<Meal> getList() {
        return dataSource.getMealList();
    }
}
