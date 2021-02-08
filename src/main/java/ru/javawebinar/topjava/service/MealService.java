package ru.javawebinar.topjava.service;

import ru.javawebinar.topjava.dao.MealDao;
import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public class MealService {
    private MealDao mealDao = new MealDao();
    public static final int MAX_CALORIES_PER_DAY = 2000;

    public List<Meal> getMealList() {
        return mealDao.getList();
    }
}
