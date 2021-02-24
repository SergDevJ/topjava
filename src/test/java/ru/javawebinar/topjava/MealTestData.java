package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;
import static ru.javawebinar.topjava.UserTestData.USER_ID;
import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

public class MealTestData {
    private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static final int MEAL_START_SEQ = START_SEQ + 2;

    public static final int MEAL_NOT_FOUND_ID = START_SEQ - 100;

    public static final int MEAL_ID = MEAL_START_SEQ + 1;

    public static final LocalDate MEAL_START_DATE = LocalDateTime.parse("2000-01-30 14:00", fmt).toLocalDate();
    public static final LocalDate MEAL_END_DATE = LocalDateTime.parse("2000-01-31 13:00", fmt).plusDays(1).toLocalDate();

//    public static final Meal MEAL = new Meal(MEAL_ID, LocalDateTime.parse("2020-12-10 11:00", fmt),
//            "Завтрак", 1400);

    public static final Map<Integer, HashMap<Integer, Meal>> MEALS = new ConcurrentHashMap<>();
    static {
        MEALS.put(USER_ID, new HashMap<>());
        MEALS.put(ADMIN_ID, new HashMap<>());
        MEALS.get(USER_ID).put(MEAL_START_SEQ, new Meal(MEAL_START_SEQ, LocalDateTime.parse("2000-01-30 10:00", fmt), "Завтрак", 500));
        MEALS.get(USER_ID).put(MEAL_START_SEQ+1, new Meal(MEAL_START_SEQ + 1, LocalDateTime.parse("2000-01-30 13:00", fmt), "Обед", 1000));
        MEALS.get(USER_ID).put(MEAL_START_SEQ+2, new Meal(MEAL_START_SEQ + 2, LocalDateTime.parse("2000-01-30 20:00", fmt), "Ужин", 500));
        MEALS.get(USER_ID).put(MEAL_START_SEQ+3, new Meal(MEAL_START_SEQ + 3, LocalDateTime.parse("2000-01-31 00:00", fmt), "Еда на граничное значение", 100));
        MEALS.get(USER_ID).put(MEAL_START_SEQ+4, new Meal(MEAL_START_SEQ + 4, LocalDateTime.parse("2000-01-31 10:00", fmt), "Завтрак", 1000));
        MEALS.get(USER_ID).put(MEAL_START_SEQ+5, new Meal(MEAL_START_SEQ + 5, LocalDateTime.parse("2000-01-31 13:00", fmt), "Обед", 500));
        MEALS.get(USER_ID).put(MEAL_START_SEQ+6, new Meal(MEAL_START_SEQ + 6, LocalDateTime.parse("2000-01-31 20:00", fmt), "Ужин", 410));
        MEALS.get(ADMIN_ID).put(MEAL_START_SEQ+7, new Meal(MEAL_START_SEQ + 7, LocalDateTime.parse("2015-06-01 14:00", fmt), "Админ ланч", 510));
        MEALS.get(ADMIN_ID).put(MEAL_START_SEQ+8, new Meal(MEAL_START_SEQ + 8, LocalDateTime.parse("2015-06-01 21:00", fmt), "Админ ужин", 1500));

    }

    public static final Meal MEAL = MEALS.get(USER_ID).get(MEAL_ID);


    public static Meal getNew() {
        return new Meal(null, LocalDateTime.parse("2099-01-30 19:15", fmt), "Тест", 1999);
    }

    public static Meal getUpdated() {
        Meal meal = new Meal(MEAL);
        meal.setDateTime(LocalDateTime.of(2020, 10, 10, 13, 33));
        meal.setDescription("Description");
        meal.setCalories(999);
        return meal;
    }

    public static List<Meal> getAllByDateTime(int userId) {
        return MEALS.get(userId).values().stream().sorted(Comparator.comparing(Meal::getDateTime).reversed()).collect(Collectors.toList());
    }

    public static List<Meal> getAllFilterByDate(LocalDate startDate, LocalDate endDate, int userId) {
        return MEALS.get(userId).values().stream().
                filter(m -> m.getDateTime().
                        compareTo(startDate.atStartOfDay()) >= 0 && m.getDateTime().compareTo(endDate.atStartOfDay()) < 0).
                sorted(Comparator.comparing(Meal::getDateTime).reversed()).collect(Collectors.toList());
    }


    
//    "Завтрак", to_timestamp("2000-01-30 10:00", "YYYY-MM-DD HH24:MI"), 500, 100000),
//            ("Обед", to_timestamp("2000-01-30 13:00", "YYYY-MM-DD HH24:MI"), 1000, 100000),
//            ("Ужин", to_timestamp("2000-01-30 20:00", "YYYY-MM-DD HH24:MI"), 500, 100000),
//            ("Еда на граничное значение", to_timestamp("2000-01-31 00:00", "YYYY-MM-DD HH24:MI"), 100, 100000),
//            ("Завтрак", to_timestamp("2000-01-31 10:00", "YYYY-MM-DD HH24:MI"), 1000, 100000),
//            ("Обед", to_timestamp("2000-01-31 13:00", "YYYY-MM-DD HH24:MI"), 500, 100000),
//            ("Ужин", to_timestamp("2000-01-31 20:00", "YYYY-MM-DD HH24:MI"), 410, 100000),
//            ("Админ ланч", to_timestamp("2015-06-01 14:00", "YYYY-MM-DD HH24:MI"), 510, 100001),
//            ("Админ ужин", to_timestamp("2015-06-01 21:00", "YYYY-MM-DD HH24:MI"), 1500, 100001);
}
