package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNew;

@RestController
@RequestMapping(value = MealRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class MealRestController {
    private static final Logger log = LoggerFactory.getLogger(MealRestController.class);

    static final String REST_URL = "/rest/meals";

    private final MealService service;

    public MealRestController(MealService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Meal get(@PathVariable(name = "id") int id) {
        int userId = SecurityUtil.authUserId();
        log.info("get meal {} for user {}", id, userId);
        return service.get(id, userId);
//        model.addObject("meal", service.get(id, userId)).setViewName("mealForm");
//        return model;
//        return "mealForm";
//        return service.get(id, userId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public String delete(@PathVariable int id) {
        int userId = SecurityUtil.authUserId();
        log.info("delete meal {} for user {}", id, userId);
        service.delete(id, userId);
        return "redirect:" + REST_URL;
    }


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<MealTo> getAll(Model model) {
        int userId = SecurityUtil.authUserId();
        log.info("getAll for user {}", userId);
        return MealsUtil.getTos(service.getAll(userId), SecurityUtil.authUserCaloriesPerDay());
//        model.addAttribute("meals", MealsUtil.getTos(service.getAll(userId), SecurityUtil.authUserCaloriesPerDay()));
//        return model;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Meal> create(@RequestBody Meal meal) {
        int userId = SecurityUtil.authUserId();
        checkNew(meal);
        log.info("create {} for user {}", meal, userId);
        Meal newMeal = service.create(meal, userId);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(newMeal.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(newMeal);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public String update(@RequestBody Meal meal, @PathVariable(name = "id") int id) {
        int userId = SecurityUtil.authUserId();
        assureIdConsistent(meal, id);
        log.info("update {} for user {}", meal, userId);
        service.update(meal, userId);
        return "redirect:" + REST_URL;
    }

    /**
     * <ol>Filter separately
     * <li>by date</li>
     * <li>by time for every date</li>
     * </ol>
     */
    @GetMapping("/filter")
    @ResponseStatus(HttpStatus.OK)
    public List<MealTo> getBetween(
            @Nullable @RequestParam(name = "startDate", required = false) LocalDate startDate,
            @Nullable @RequestParam(name = "endDate", required = false) LocalDate endDate,
            @Nullable @RequestParam(name = "startTime", required = false) LocalTime startTime,
            @Nullable @RequestParam(name = "endTime", required = false) LocalTime endTime) {
        int userId = SecurityUtil.authUserId();
        log.info("getBetween dates({} - {}) time({} - {}) for user {}", startDate, endDate, startTime, endTime, userId);
        List<Meal> mealsDateFiltered = service.getBetweenInclusive(startDate, endDate, userId);
        return MealsUtil.getFilteredTos(mealsDateFiltered, SecurityUtil.authUserCaloriesPerDay(), startTime, endTime);
    }


    @GetMapping("/filter2")
    @ResponseStatus(HttpStatus.OK)
    public List<MealTo> getBetween2(
            @Nullable @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Nullable @RequestParam(name = "startTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Nullable @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @Nullable @RequestParam(name = "endTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        int userId = SecurityUtil.authUserId();

        LocalDate startLocalDate = startDate.toLocalDate();
        LocalDate endLocalDate = endDate.toLocalDate();
        LocalTime startLocalTime = startTime.toLocalTime();
        LocalTime endLocalTime = endTime.toLocalTime();

        log.info("getBetween dates({} - {}) time({} - {}) for user {}", startLocalDate, endLocalDate, startLocalTime, endLocalTime, userId);

//        List<Meal> mealsDateFiltered = service.getBetweenInclusive(startDate, endDate, userId);
//        return MealsUtil.getFilteredTos(mealsDateFiltered, SecurityUtil.authUserCaloriesPerDay(), startTime, endTime);

        List<Meal> mealsDateFiltered = service.getBetweenInclusive(startLocalDate, endLocalDate, userId);
        return MealsUtil.getFilteredTos(mealsDateFiltered, SecurityUtil.authUserCaloriesPerDay(), startLocalTime, endLocalTime);
    }

}