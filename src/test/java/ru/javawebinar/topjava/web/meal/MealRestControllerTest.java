package ru.javawebinar.topjava.web.meal;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.Formatter;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.mvc.AbstractController;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.javawebinar.topjava.MealTestData.*;

import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.TestMatcher;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.util.exception.NotFoundException;
import ru.javawebinar.topjava.web.AbstractControllerTest;
import ru.javawebinar.topjava.web.MyConversionService;
import ru.javawebinar.topjava.web.SecurityUtil;
import ru.javawebinar.topjava.web.json.JsonUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.javawebinar.topjava.UserTestData.*;
import static ru.javawebinar.topjava.web.json.JacksonObjectMapper.getMapper;

class MealRestControllerTest extends AbstractControllerTest {

    static final String REST_URL = MealRestController.REST_URL + '/';

    @Autowired
    MealService mealService;

//    @Autowired
//    private MyConversionService myConversionService;

    @BeforeAll
    static void init() {
        SecurityUtil.setAuthUserId(USER_ID);
    }

    @Test
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + MEAL1_ID))
                .andDo(print())
                .andExpect(status().isOk())
//                .andExpect(view().name("mealForm"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MEAL_MATCHER.contentJson(meal1))
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    Meal meal = getMapper().readValue(json, Meal.class);
                    assertThat(meal).usingRecursiveComparison().ignoringFields("user").isEqualTo(meal1);
                });
    }

    @Test
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + MEAL1_ID))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThrows(NotFoundException.class, () -> mealService.get(MEAL1_ID, SecurityUtil.authUserId()));
    }

    @Test
    void getAll() throws Exception {
        perform(MockMvcRequestBuilders.get(MealRestController.REST_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MEALTO_MATCHER.contentJson(
                        MealsUtil.getTos(List.of(meal7, meal6, meal5, meal4, meal3, meal2, meal1), SecurityUtil.authUserCaloriesPerDay())));
    }

    @Test
    void create() throws Exception {
        Meal meal = MealTestData.getNew();
        SecurityUtil.setAuthUserId(USER_ID);
        ResultActions actions = perform(MockMvcRequestBuilders.post(MealRestController.REST_URL).
                    contentType(MediaType.APPLICATION_JSON).
                    content(JsonUtil.writeValue(meal)))
                .andDo(print())
                .andExpect(status().isCreated());
        Meal jsonMeal = JsonUtil.readValue(actions.andReturn().getResponse().getContentAsString(), Meal.class);
        meal.setId(jsonMeal.getId());
        assertThat(jsonMeal).isEqualTo(meal);
        MEAL_MATCHER.assertMatch(jsonMeal, meal);
        MEAL_MATCHER.assertMatch(mealService.get(jsonMeal.getId(), USER_ID), meal);

    }

    @Test
    void update() throws Exception {
        Meal updated = MealTestData.getUpdated();
        perform(MockMvcRequestBuilders.post(REST_URL + updated.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonUtil.writeValue(updated)))
                .andDo(print())
                .andExpect(status().isNoContent());
        Meal actual = mealService.get(updated.getId(), SecurityUtil.authUserId());
        MEAL_MATCHER.assertMatch(actual, updated);
    }

    @Test
    void getBetween() throws Exception {
        final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        final DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm");
        LocalDate startDate = LocalDate.of(2020, Month.JANUARY, 30);
        LocalDate endDate = LocalDate.of(2020, Month.JANUARY, 30);
        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime endTime = LocalTime.of(20, 0);

        String url = REST_URL + "filter?" +
                "startDate=" + startDate.format(df) + "&" +
                "endDate=" + endDate.format(df) + "&" +
                "startTime=" + startTime.format(tf) + "&" +
                "endTime=" + endTime.format(tf);

        perform(MockMvcRequestBuilders.get(url))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MEALTO_MATCHER.contentJson(
                        MealsUtil.getTos(List.of(meal2, meal1), SecurityUtil.authUserCaloriesPerDay())));

    }
}