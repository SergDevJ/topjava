package ru.javawebinar.topjava.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.UserTestData;
import ru.javawebinar.topjava.model.Meal;
import org.assertj.core.api.Assertions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import ru.javawebinar.topjava.util.exception.NotFoundException;


import java.util.List;

import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    @Autowired
    private MealService service;


    @Test
    public void duplicateMeal() {
        assertThrows(Exception.class, () -> {
            service.create(new Meal(null,
                    MealTestData.MEAL.getDateTime(),
                    MealTestData.MEAL.getDescription(),
                    MealTestData.MEAL.getCalories()), USER_ID);
        });
    }

    @Test
    public void get() {
        Meal actual = service.get(MealTestData.MEAL.getId(), USER_ID);
        assertThat(actual).usingRecursiveComparison().isEqualTo(MealTestData.MEAL);
    }

    @Test
    public void getNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(MEAL_NOT_FOUND_ID, UserTestData.NOT_FOUND));
    }

    @Test
    public void delete() {
        service.delete(MealTestData.MEAL.getId(), USER_ID);
        assertThrows(NotFoundException.class, () -> service.get(MealTestData.MEAL.getId(), USER_ID));
    }

    @Test
    public void deletedNotFound() {
        Assert.assertThrows(NotFoundException.class, () -> service.delete(0, 0));
    }

    @Test
    public void getBetweenInclusive() {
        List<Meal> actual = service.getBetweenInclusive(MEAL_START_DATE, MEAL_END_DATE, USER_ID);
        List<Meal> expected = MealTestData.getAllFilterByDate(MEAL_START_DATE, MEAL_END_DATE, USER_ID);
        Assertions.assertThat(actual)
                .usingRecursiveFieldByFieldElementComparator()
                .isEqualTo(expected);
    }

    @Test
    public void getAll() {
        Assertions.assertThat(service.getAll(USER_ID)).
                usingRecursiveFieldByFieldElementComparator().
                isEqualTo(MealTestData.getAllByDateTime(USER_ID));
    }

    @Test
    public void update() {
        Meal updated = MealTestData.getUpdated();
        service.update(updated, USER_ID);
        Meal actual = service.get(updated.getId(), USER_ID);
        Assertions.assertThat(actual).usingRecursiveComparison().isEqualTo(MealTestData.getUpdated());
    }

    @Test
    public void create() {
        Meal created = service.create(MealTestData.getNew(), USER_ID);
        Meal actual = service.get(created.getId(), USER_ID);
        Assertions.assertThat(actual).usingRecursiveComparison().ignoringFields("id").isEqualTo(MealTestData.getNew());
    }
}