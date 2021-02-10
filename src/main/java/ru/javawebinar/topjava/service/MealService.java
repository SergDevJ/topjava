package ru.javawebinar.topjava.service;

import ru.javawebinar.topjava.dao.MealDao;
import ru.javawebinar.topjava.dao.MealDaoMemory;
import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public class MealService {
    private final MealDao mealDao = new MealDaoMemory();
    public static final int MAX_CALORIES_PER_DAY = 2000;

    public List<Meal> getMealList() {
        return mealDao.getList();
    }

    public int createMeal(Meal meal) {
        return mealDao.create(meal);
    }

    public Meal getMealById(int id) {
        return mealDao.get(id);
    }

    public int updateMeal(Meal meal) {
        return mealDao.update(meal);
    }

    public Meal deleteMeal(int id) {
        return mealDao.delete(id);
    }
}
