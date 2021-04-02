package ru.javawebinar.topjava.web;

import org.assertj.core.matcher.AssertionMatcher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.ui.Model;
import static ru.javawebinar.topjava.UserTestData.*;

import ru.javawebinar.topjava.TestMatcher;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.service.UserService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.javawebinar.topjava.UserTestData.*;

class RootControllerTest extends AbstractControllerTest {

    @Autowired
    MealService mealService;

    @Test
    void getUsers() throws Exception {
        ResultActions rs = perform(get("/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("users"))
                .andExpect(forwardedUrl("/WEB-INF/jsp/users.jsp"))
                .andExpect(model().attribute("users",
                        new AssertionMatcher<List<User>>() {
                            @Override
                            public void assertion(List<User> actual) throws AssertionError {
                                USER_MATCHER.assertMatch(actual, admin, user);
                            }
                        }
                ));
    }

    @Test
    void getMeals() throws Exception {
        int userId = USER_ID;
        final TestMatcher<MealTo> MEALTO_MATCHER = TestMatcher.usingIgnoringFieldsComparator(MealTo.class);
        SecurityUtil.setAuthUserId(userId);
        List<MealTo> expected = MealsUtil.getTos(mealService.getAll(userId), SecurityUtil.authUserCaloriesPerDay());
        ResultActions rs = perform(get("/meals"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("meals"))
//                .andExpect(MEALTO_MATCHER.contentJson(expected)
                .andExpect(model().attribute("meals",
                        new AssertionMatcher<List<MealTo>>() {
                            @Override
                            public void assertion(List<MealTo> actual) throws AssertionError {
                                MEALTO_MATCHER.assertMatch(actual, expected);
                            }
                        }
                ));
    }
}