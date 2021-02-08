package ru.javawebinar.topjava.dao.dataSource;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MemoryDataSource implements DataSource {
    private volatile AtomicInteger currentMealId = new AtomicInteger(0);

    private final List<Meal> mealList = Collections.synchronizedList(new ArrayList<>(Arrays.asList(
            new Meal(getNextMealId(), LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
            new Meal(getNextMealId(), LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
            new Meal(getNextMealId(), LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
            new Meal(getNextMealId(), LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
            new Meal(getNextMealId(), LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
            new Meal(getNextMealId(), LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
            new Meal(getNextMealId(), LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410))));


    private MemoryDataSource() {
    }

    public static MemoryDataSource getInstance() {
        return _store.instance;
    }

    private static class _store {
        private static final MemoryDataSource instance = new MemoryDataSource();
    }

    @Override
    public List<Meal> getMealList() {
        return Collections.synchronizedList(mealList);
    }

    public int getNextMealId() {
        return currentMealId.incrementAndGet();
    }
}
