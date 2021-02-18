package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.repository.inmemory.InMemoryMealRepository;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.meal.MealRestController;
import ru.javawebinar.topjava.web.user.AdminRestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Objects;

public class MealServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(MealServlet.class);
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

//    private MealRepository repository;
    private MealRestController mealRestController;
    private ConfigurableApplicationContext appCtx;

    @Override
    public void init() {
//        repository = new InMemoryMealRepository();
        appCtx = new ClassPathXmlApplicationContext("spring/spring-app.xml");
        System.out.println("Bean definition names: " + Arrays.toString(appCtx.getBeanDefinitionNames()));
//            AdminRestController ad
//            minUserController = appCtx.getBean(AdminRestController.class);
//            adminUserController.create(new User(null, "userName", "email@mail.ru", "password", Role.ADMIN));

        mealRestController = appCtx.getBean(MealRestController.class);
//            mealRestController.create(new Meal(LocalDateTime.now(), "жратва", 5000));
//            System.out.println(mealRestController.getAll());
    }

    @Override
    public void destroy() {
        super.destroy();
        appCtx.close();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String id = request.getParameter("id");

        Meal meal = new Meal(id.isEmpty() ? null : Integer.valueOf(id),
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories")));

        log.info(meal.isNew() ? "Create {}" : "Update {}", meal);
        if (meal.isNew()) mealRestController.create(meal);
        else mealRestController.update(meal);
//        repository.save(meal, SecurityUtil.authUserId());
        response.sendRedirect("meals");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        System.out.println("doGet: " + action);

        switch (action == null ? "all" : action) {
            case "delete":
                int id = getId(request);
                log.info("Delete {}", id);
                mealRestController.delete(id);
//                repository.delete(id, SecurityUtil.authUserId());
                response.sendRedirect("meals");
                break;
            case "create":
            case "update":
                final Meal meal = "create".equals(action) ?
                        new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000) :
                        mealRestController.get(getId(request));
//                        repository.get(getId(request), SecurityUtil.authUserId());
                request.setAttribute("meal", meal);
                request.getRequestDispatcher("/mealForm.jsp").forward(request, response);
                break;
            case "filter":
                log.info("getByDateAndTime");
                LocalDate startDate = null;
                LocalDate endDate = null;
                LocalTime startTime = null;
                LocalTime endTime = null;
                if (request.getParameter("startDate") != null && !request.getParameter("startDate").isEmpty())
                    startDate = LocalDate.parse(request.getParameter("startDate"), dateFormatter);
                if (request.getParameter("endDate") != null && !request.getParameter("endDate").isEmpty())
                    endDate = LocalDate.parse(request.getParameter("endDate"), dateFormatter);
                if (request.getParameter("startTime") != null && !request.getParameter("startTime").isEmpty())
                    startTime = LocalTime.parse(request.getParameter("startTime"), timeFormatter);
                if (request.getParameter("endTime") != null && !request.getParameter("endTime").isEmpty())
                    endTime = LocalTime.parse(request.getParameter("endTime"), timeFormatter);
                if (startDate != null) request.setAttribute("startDate", startDate);
                if (endDate != null) request.setAttribute("endDate", endDate);
                if (startTime != null) request.setAttribute("startTime", startTime);
                if (endTime != null) request.setAttribute("endTime", endTime);
                request.setAttribute("meals",
//                        MealsUtil.getTos(repository.getAll(SecurityUtil.authUserId()), MealsUtil.DEFAULT_CALORIES_PER_DAY));
                        mealRestController.getByDateAndTime(startDate, endDate, startTime, endTime));
                request.getRequestDispatcher("/meals.jsp").forward(request, response);
                break;
            case "all":
            default:
                log.info("getAll");
                request.setAttribute("meals",
//                        MealsUtil.getTos(repository.getAll(SecurityUtil.authUserId()), MealsUtil.DEFAULT_CALORIES_PER_DAY));
                        mealRestController.getAll());
                request.getRequestDispatcher("/meals.jsp").forward(request, response);
                break;
        }
    }

    private int getId(HttpServletRequest request) {
        String paramId = Objects.requireNonNull(request.getParameter("id"));
        return Integer.parseInt(paramId);
    }
}
