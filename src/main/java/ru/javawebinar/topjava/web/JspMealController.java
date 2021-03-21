package ru.javawebinar.topjava.web;

import org.hsqldb.auth.AuthUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.service.UserService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.meal.MealRestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;


class MealControllerContext {
    static class MealFilterParams {
        LocalDate startDate;
        LocalDate endDate;
        LocalTime startTime;
        LocalTime endTime;

        public MealFilterParams(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
            this.startDate = startDate;
            this.endDate = endDate;
            this.startTime = startTime;
            this.endTime = endTime;
        }
    }

    static private ThreadLocal<MealFilterParams> mealFilterParams = new ThreadLocal<>();

    static void setMealFilterParams(LocalDate startDate, LocalDate endDate,
                                    LocalTime startTime, LocalTime endTime) {
        mealFilterParams.set(new MealFilterParams(startDate, endDate, startTime, endTime));
    }

    static MealFilterParams getMealFilterParams() {
        return mealFilterParams.get() != null ? mealFilterParams.get() : new MealFilterParams(null, null, null, null);
//        return mealFilterParams.get();
    }
}



@Controller
@RequestMapping("meals")
public class JspMealController {
    private static final Logger log = LoggerFactory.getLogger(JspMealController.class);
    private DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    MealControllerContext mealControllerContext;
//    MealControllerContext mealControllerContext = new MealControllerContext();


    @Autowired
    MealService service;

    @Autowired
    UserService userService;

    @GetMapping("/delete")
    public String deleteMeal(@RequestParam(value = "id") Integer id) {
        log.info("enter deleteMeal(): id={}", id);

        Objects.requireNonNull(id, "id can't be null for delete");
        final int userId = SecurityUtil.authUserId();
        service.delete(id, userId);
        return "redirect:/meals/list";
    }

    @GetMapping("/create")
    public String createMeal(Model model) {
        log.info("enter createMeal()");

        final int userId = SecurityUtil.authUserId();
        model.addAttribute("meal", new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", MealsUtil.DEFAULT_CALORIES_PER_DAY));
        return "mealForm";
    }

    @GetMapping("/update")
    public String updateMeal(@RequestParam("id") Integer id,
                             HttpServletRequest request,
                             Model model) {
        log.info("enter updateMeal(): id={}", id);

        Objects.requireNonNull(id, "id can't be null for update");
        final int userId = SecurityUtil.authUserId();
        Meal meal = service.get(id, userId);
        request.setAttribute("meal", meal);
//        model.addAttribute("meal", meal);
        return "mealForm";
    }



    public List<MealTo> getBetween(@Nullable LocalDate startDate, @Nullable LocalTime startTime,
                                   @Nullable LocalDate endDate, @Nullable LocalTime endTime,
                                   int CaloriesPerDay) {
        int userId = SecurityUtil.authUserId();
        log.info("getBetween dates({} - {}) time({} - {}) for user {}", startDate, endDate, startTime, endTime, userId);

        List<Meal> mealsDateFiltered = service.getBetweenInclusive(startDate, endDate, userId);
        return MealsUtil.getFilteredTos(mealsDateFiltered, CaloriesPerDay, startTime, endTime);
    }

    @GetMapping("/filter")
    public String filterMeal(HttpServletRequest request,
        @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
        @RequestParam(value = "startTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
        @RequestParam(value = "endTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime,
        Model model)
    {
        log.info("enter filterMeal(): startDate={}, endDate={}, startTime={}, endTime={}", startDate, endDate, startTime, endTime);


        HttpSession session = request.getSession();
        session.setAttribute("mealFilterParams", new MealControllerContext.MealFilterParams(startDate, endDate, startTime, endTime));

//        MealControllerContext.setMealFilterParams(startDate, endDate, startTime, endTime);
        getMeals(request, model);
        return "meals";
//        getMeals(startDate, endDate, startTime, endTime, model)

//        final int userId = SecurityUtil.authUserId();
//        model.addAttribute("meal", new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", MealsUtil.DEFAULT_CALORIES_PER_DAY));
//        return "mealForm";
    }

    @GetMapping("/list")
//    public String getMeals(@RequestParam(value = "action", required = false) String action,
//            @RequestParam(value = "id", required = false) Integer id,
//            Model model) {
    public String getMeals(
//        @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
//        @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
//        @RequestParam(value = "startTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
//        @RequestParam(value = "endTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime,
        HttpServletRequest request,
        Model model) {

        HttpSession session = request.getSession();
        MealControllerContext.MealFilterParams mealFilterParams = (MealControllerContext.MealFilterParams) session.getAttribute("mealFilterParams");
//        MealControllerContext.MealFilterParams mealFilterParams = MealControllerContext.getMealFilterParams();
        LocalDate startDate = null;
        LocalDate endDate = null;
        LocalTime startTime = null;
        LocalTime endTime = null;
        if (mealFilterParams != null) {
            startDate = mealFilterParams.startDate;
            endDate = mealFilterParams.endDate;
            startTime = mealFilterParams.startTime;
            endTime = mealFilterParams.endTime;
        }



        log.info("enter getMeals(): startDate={}, endDate={}, startTime={}, endTime={}", startDate, endDate, startTime, endTime);


        final int userId = SecurityUtil.authUserId();
        User user = userService.get(userId);
        Objects.requireNonNull(user, "Can't find user with id=" + userId);
        List<MealTo> meals = getBetween(startDate, startTime, endDate, endTime, user.getCaloriesPerDay());
        model.addAttribute("meals", meals);
//        model.addAttribute("startDate", startDate);
//        model.addAttribute("endDate", endDate);
//        model.addAttribute("startTime", startTime);
//        model.addAttribute("endTime", endTime);

        model.addAttribute("startDate", startDate);


//        request.setAttribute("startDate", startDate);
        request.setAttribute("startDate", startDate);
        request.setAttribute("endDate", endDate);
        request.setAttribute("startTime", startTime);
        request.setAttribute("endTime", endTime);

        return "meals";


//        if (action == null) {
//            User user = userService.get(userId);
//            Objects.requireNonNull(user, "Can't find user with id=" + userId);
//            List<MealTo> meals = MealsUtil.getTos(service.getAll(userId), user.getCaloriesPerDay());
//            model.addAttribute("meals", meals);
//            return "meals";
//        }
//        switch (action.toLowerCase()) {
//            case "delete" -> {
//                Objects.requireNonNull(id, "id can't be null for delete");
//                service.delete(id, userId);
//                return "redirect:meals";
//            }
//            case "update" -> {
//                Objects.requireNonNull(id, "id can't be null for update");
//                model.addAttribute("meal", service.get(id, userId));
//                return "mealForm";
//            }
//            case "create" -> {
//                model.addAttribute("meal", new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", MealsUtil.DEFAULT_CALORIES_PER_DAY));
//                return "mealForm";
//            }
//            default -> {
//                return "redirect:meals";
//            }

    }




    @PostMapping("/save")
    public String setMeal(HttpServletRequest request) throws UnsupportedEncodingException {
        request.setCharacterEncoding("UTF-8");
        log.info("enter setMeal()");
        final int userId = SecurityUtil.authUserId();

        Integer id = request.getParameter("id") != null  && !request.getParameter("id").isEmpty() ? Integer.valueOf(request.getParameter("id")) : null;
        LocalDateTime dateTime = request.getParameter("dateTime") != null ? LocalDateTime.parse(request.getParameter("dateTime"), fmt) : null;
        String description = request.getParameter("description");
        int calories = request.getParameter("calories") != null ? Integer.parseInt(request.getParameter("calories")) : MealsUtil.DEFAULT_CALORIES_PER_DAY;
        Meal meal = new Meal(id, dateTime, description, calories);
        if (id == null) {
            service.create(meal, userId);
        } else {
            service.update(meal, userId);
        }
        return "redirect:/meals/list";
    }


}
