package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, Meal> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    public InMemoryMealRepository() {
        MealsUtil.meals.forEach(m -> this.save(m, SecurityUtil.authUserId()));
    }


    @Override
    public Meal save(Meal meal, int userId) {
        if (meal.getUserId() != userId) return null;
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            repository.put(meal.getId(), meal);
            return meal;
        }
        // handle case: update, but not present in storage
        return repository.computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
    }

    @Override
    public boolean delete(int id, int userId) {
        if (get(id, userId) == null) return false;
        return repository.remove(id) != null;
    }

    @Override
    public Meal get(int id, int userId) {
        Meal meal = repository.get(id);
        return meal == null ? null : meal.getUserId() == userId ? meal : null;
    }

//    @Override
//    public List<Meal> getAll(int userId) {
//        return getAll(userId, null, null, null, null);
////        return repository.values().stream().
////                filter(m -> m.getUserId() == userId).
////                sorted(Comparator.comparing(Meal::getDateTime).reversed()).
////                collect(Collectors.toList());
//    }

    @Override
    public List<Meal> getAll(int userId, LocalDate startDate, LocalDate endDate) {
        return repository.values().stream().
                filter(m -> m.getUserId() == userId).
                filter(m -> m.getDate().compareTo(startDate == null ? LocalDate.MIN : startDate) >= 0 &&
                            m.getDate().compareTo(endDate == null ? LocalDate.MAX : endDate) <= 0).
                sorted(Comparator.comparing(Meal::getDateTime).reversed()).
                collect(Collectors.toList());

//        return repository.values().stream().
//                filter(m -> m.getUserId() == userId).
//                filter(m -> DateTimeUtil.isBetweenHalfOpen(m.getDate(),
//                                startDate == null ? LocalDate.MIN : startDate,
//                                endDate == null ? LocalDate.MAX : endDate) &&
//                        DateTimeUtil.isBetweenHalfOpen(m.getTime(),
//                                startTime == null ? LocalTime.MIN : startTime,
//                                endTime == null ? LocalTime.MAX : endTime)).
//                sorted(Comparator.comparing(Meal::getDateTime).reversed()).
//                collect(Collectors.toList());
    }
}

