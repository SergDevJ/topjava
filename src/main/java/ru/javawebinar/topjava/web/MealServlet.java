package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class MealServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(MealServlet.class);
    private static final MealService mealService = new MealService();
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("enter doPost");
        request.setCharacterEncoding("UTF-8");
        if (request.getParameter("save") != null) {
            int id = Integer.parseInt(request.getParameter("id"));
            String description = request.getParameter("description");
            int calories = Integer.parseInt(request.getParameter("calories"));
            System.out.println("*** dateTime: " + request.getParameter("dateTime"));
            LocalDateTime dateTime = LocalDateTime.parse(request.getParameter("dateTime"), dateTimeFormatter);
            Meal meal = new Meal(id, dateTime, description, calories);
            if (id == 0) {
                id = mealService.createMeal(meal);
                log.info("create meal (id = {})", id);
            } else {
                mealService.updateMeal(meal);
                log.info("update meal (id = {})", id);
            }
        }
        doGet(request, response);
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("enter doGet");
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        if (action == null) {
            List<MealTo> mealsTo = MealsUtil.createMealToList(mealService.getMealList(), MealService.MAX_CALORIES_PER_DAY);
            request.setAttribute("mealList", mealsTo);
            log.debug("redirect to /mealList.jsp (show meal list)");
            request.getRequestDispatcher("/mealList.jsp").forward(request, response);
            return;
        }
        switch(action.toLowerCase()) {
            case "add": {
                request.setAttribute("meal", new MealTo(0, null, null, 0, false));
                request.setAttribute("formTitle", "New meal");
                log.debug("redirect to /editMeal.jsp (add new meal)");
                request.getRequestDispatcher("/editMeal.jsp").forward(request, response);
                break;
            }
            case "edit": {
                int id = Integer.parseInt(request.getParameter("id"));
                Meal meal = mealService.getMealById(id);
                request.setAttribute("meal", MealsUtil.createTo(meal, false));
                request.setAttribute("formTitle", "Update meal");
                log.debug("redirect to /editMeal.jsp for update meal (id={})", meal.getId());
                request.getRequestDispatcher("/editMeal.jsp").forward(request, response);
                break;
            }
            case "delete": {
                int id = Integer.parseInt(request.getParameter("id"));
                Meal meal = mealService.deleteMeal(id);
                if (meal != null) {
                    log.info("delete meal (id={})", id);
                    log.debug("redirect to /mealList.jsp (show meal list)");

                    List<MealTo> mealsTo = MealsUtil.createMealToList(mealService.getMealList(), MealService.MAX_CALORIES_PER_DAY);
                    request.setAttribute("mealList", mealsTo);
                    log.debug("redirect to /mealList.jsp (show meal list)");
                    request.getRequestDispatcher("/mealList.jsp").forward(request, response);
                } else
                    log.info("cant't delete meal (id={} not found)", id);
                break;
            }
        }





    }
}
