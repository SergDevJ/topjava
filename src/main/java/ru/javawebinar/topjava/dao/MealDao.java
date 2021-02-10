package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface MealDao {
    List<Meal> getList();
    int create(Meal meal);
    int update(Meal meal);
    Meal get(int id);
    Meal delete(int id);
}
