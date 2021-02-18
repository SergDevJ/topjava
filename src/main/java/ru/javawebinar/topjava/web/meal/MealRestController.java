package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.service.UserService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNew;

import static ru.javawebinar.topjava.web.SecurityUtil.authUserId;


@Controller
public class MealRestController {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    private MealService service;
    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setService(MealService service) {
        this.service = service;
    }



    public List<MealTo> getAll() {
        log.info("getAll");
        return MealsUtil.getTos(service.getAll(authUserId()), userService.get(authUserId()).getCaloriesPerDay());
    }

    public List<MealTo> getByDateAndTime(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
        log.info("getByDateAndTime");
        if (startTime == null) startTime = LocalTime.MIN;
        if (endTime == null) endTime = LocalTime.MAX;
        return MealsUtil.getFilteredTos(service.getAll(authUserId(), startDate, endDate),
                userService.get(authUserId()).getCaloriesPerDay(),
                startTime, endTime);
//        return MealsUtil.getTos(service.getAll(authUserId(), startDate, endDate, startTime, endTime), userService.get(authUserId()).getCaloriesPerDay());
    }

    public Meal get(int id) {
        log.info("get {}", id);
        return service.get(id, authUserId());
    }

    public Meal create(Meal meal) {
        log.info("create {}", meal);
        checkNew(meal);
        return service.create(meal, authUserId());
    }

    public void delete(int id) {
        log.info("delete {}", id);
        service.delete(id, authUserId());
    }

    public void update(Meal meal) {
        log.info("update {} with id={}", meal, meal.getId());
        assureIdConsistent(meal, meal.getId());
        service.update(meal, authUserId());
    }

//    public User getByMail(String email) {
//        log.info("getByEmail {}", email);
//        return service.getByEmail(email);
//    }


}