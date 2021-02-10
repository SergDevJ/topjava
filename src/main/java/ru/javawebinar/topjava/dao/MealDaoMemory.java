package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MealDaoMemory implements MealDao {
    private static volatile AtomicInteger currentMealId = new AtomicInteger(0);
    private static List<Meal> mealList;


    private static class _store {
        private static List<Meal> mealList = Collections.synchronizedList(new ArrayList<>(Arrays.asList(
                new Meal(getNextMealId(), LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new Meal(getNextMealId(), LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new Meal(getNextMealId(), LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new Meal(getNextMealId(), LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new Meal(getNextMealId(), LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new Meal(getNextMealId(), LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new Meal(getNextMealId(), LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410))));;
    }

    public MealDaoMemory() {
        initData();
    }

    private static void initData() {
        mealList = _store.mealList;
    }

    private static int getNextMealId() {
        return currentMealId.incrementAndGet();
    }


    @Override
    public List<Meal> getList() {
        return mealList;
    }

    @Override
    public int create(Meal meal) {
        Objects.requireNonNull(meal);
        int id = getNextMealId();
        mealList.add(new Meal(id, meal.getDateTime(), meal.getDescription(), meal.getCalories()));
        return id;
    }

    @Override
    public Meal get(int id) {
        return mealList.stream().filter(x -> x.getId() == id).findAny().orElse(null);
    }

    @Override
    public int update(Meal meal) {
        Objects.requireNonNull(meal);
        Meal find = get(meal.getId());
        if (find == null) return 0;
        find.setDescription(meal.getDescription());
        find.setCalories(meal.getCalories());
        find.setDateTime(meal.getDateTime());
        return find.getId();
    }

    @Override
    public Meal delete(int id) {
        Iterator<Meal> itr = mealList.iterator();
        Meal del;
        while (itr.hasNext()) {
            del = itr.next();
            if (del.getId() == id) {
                itr.remove();
                return del;
            }
        }
        return null;
    }

}
