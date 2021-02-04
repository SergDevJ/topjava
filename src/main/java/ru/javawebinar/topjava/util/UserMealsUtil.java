package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;



public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);
        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
        System.out.println(filteredByCyclesOptional2(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
        System.out.println(filteredByStreamsFlatMap(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));


    }

    public static List<UserMealWithExcess> filteredByCycles2(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        class ExcessRef {
            boolean excess;
            public ExcessRef(boolean excess) {
                this.excess = excess;
            }
            public boolean isExcess() {
                return excess;
            }
            public void setExcess(boolean excess) {
                this.excess = excess;
            }
        }

        class UserMealWithExcessRef {
            LocalDateTime dateTime;
            String description;
            int calories;
            ExcessRef excessRef;
            public UserMealWithExcessRef(LocalDateTime dateTime, String description, int calories, ExcessRef excessRef) {
                this.dateTime = dateTime;
                this.description = description;
                this.calories = calories;
                this.excessRef = excessRef;
            }
            public LocalTime getTime() {
                return dateTime.toLocalTime();
            }
        }

        Map<LocalDate, Integer> caloriesByDate = new HashMap<>();
        Map<LocalDate, ExcessRef> excessByDate = new HashMap<>();
        List<UserMealWithExcessRef> tempMeals = new LinkedList<>();
        boolean isExcess;
        LocalDate mealDate;
        for (UserMeal meal: meals) {
            mealDate = meal.getDate();
            caloriesByDate.merge(mealDate, meal.getCalories(), Integer::sum);
            isExcess = caloriesByDate.get(mealDate) > caloriesPerDay;
            ExcessRef excessRef;
            if (!excessByDate.containsKey(mealDate)) {
                excessRef = new ExcessRef(isExcess);
                excessByDate.put(mealDate, excessRef);
            }
            else {
                excessRef = excessByDate.get(mealDate);
                excessRef.setExcess(isExcess);
            }
            tempMeals.add(new UserMealWithExcessRef(meal.getDateTime(), meal.getDescription(), meal.getCalories(), excessRef));
        }

        return tempMeals.stream().
                filter(m -> TimeUtil.isBetweenHalfOpen(m.getTime(), startTime, endTime)).
                map(m -> new UserMealWithExcess(m.dateTime, m.description, m.calories, m.excessRef.excess)).
                collect(Collectors.toList());
    }



    public static List<UserMealWithExcess> filteredByCyclesOptional2(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        List<UserMealWithExcess> result = new LinkedList<>();
        Map<LocalDate, Integer> caloriesByDay = new HashMap<>();
        Map<LocalDate, List<UserMealWithExcess>> mealListByDate = new HashMap<>();
        LocalDate mealDate;
        boolean isExcess;
        for (UserMeal meal: meals) {
            mealDate = meal.getDate();
            caloriesByDay.merge(mealDate, meal.getCalories(), Integer::sum);
            isExcess = caloriesByDay.get(mealDate) > caloriesPerDay;
            UserMealWithExcess mealWithExcess = new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(), isExcess);
            if (isExcess && mealListByDate.containsKey(mealDate)) {
                for (UserMealWithExcess m: mealListByDate.get(mealDate)) m.setExcess(true);
                mealListByDate.remove(mealDate);
            }
            if (!isExcess) {
                if (!mealListByDate.containsKey(mealDate))
                    mealListByDate.put(mealDate, new LinkedList<>(Collections.singletonList(mealWithExcess)));
                else mealListByDate.get(mealDate).add(mealWithExcess);
            }
            if (TimeUtil.isBetweenHalfOpen(meal.getTime(), startTime, endTime)) result.add(mealWithExcess);
        }
        return result;
    }


    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesByDay = new HashMap<>();
        for (UserMeal meal: meals) {
            caloriesByDay.merge(meal.getDate(), meal.getCalories(), Integer::sum);
        }

        List<UserMealWithExcess> result = new LinkedList<>();
        for (UserMeal meal: meals) {
            if (!TimeUtil.isBetweenHalfOpen(meal.getTime(), startTime, endTime)) continue;
            result.add(new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(),
                    caloriesByDay.get(meal.getDate()) > caloriesPerDay));
        }
        return result;
    }


    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesByDay = meals.stream().
                collect(Collectors.toMap(UserMeal::getDate, UserMeal::getCalories, Integer::sum, HashMap::new));
        return meals.stream().
                filter(x -> TimeUtil.isBetweenHalfOpen(x.getTime(), startTime, endTime)).
                map(x -> new UserMealWithExcess(x.getDateTime(), x.getDescription(), x.getCalories(),
                        caloriesByDay.get(x.getDate()) > caloriesPerDay)).
                collect(Collectors.toList());

    }

    public static List<UserMealWithExcess> filteredByStreamsFlatMap(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Collection<List<UserMeal>> temp = meals.stream().collect(Collectors.groupingBy(UserMeal::getDate)).values();
        return temp.stream().flatMap(list -> list.stream().
                map(x -> new UserMealWithExcess(x.getDateTime(), x.getDescription(), x.getCalories(),
                        list.stream().mapToInt(UserMeal::getCalories).sum() > caloriesPerDay))).
                filter(x -> TimeUtil.isBetweenHalfOpen(x.getTime(), startTime, endTime)).
                collect(Collectors.toList());
    }
}
