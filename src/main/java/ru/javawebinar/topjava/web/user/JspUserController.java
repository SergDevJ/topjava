package ru.javawebinar.topjava.web.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.UserService;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.meal.JspMealController;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequestMapping("users")
public class JspUserController {
    private static final Logger log = LoggerFactory.getLogger(JspMealController.class);

    @Autowired
    UserService service;

    @GetMapping("/update")
    public String updateUser(@RequestParam("id") Integer id,
                             Model model) {
        log.info("enter updateUser(): id={}", id);

        User user = service.get(id);
        Objects.requireNonNull(id, "user not found with id=" + id + " for update");
        model.addAttribute("user", user);
        model.addAttribute("action", "update");
        return "user";
    }

    @GetMapping("/create")
    String createUser(Model model) {
        log.info("enter createUser()");

        model.addAttribute("user", new User(null, "", "", "", MealsUtil.DEFAULT_CALORIES_PER_DAY,true, null, Set.of()));
        model.addAttribute("action", "create");
        return "user";
    }

    @GetMapping("/delete")
    public String deleteUser(@RequestParam("id") Integer id) {
        log.info("enter deleteUser(): id={}", id);

        service.delete(id);
        return "redirect:/users";

    }

    @PostMapping("/save")
    public String saveUser(HttpServletRequest request) throws UnsupportedEncodingException {
        request.setCharacterEncoding("UTF-8");
        log.info("enter saveUser(), id={}", request.getParameter("id"));

        Integer id = request.getParameter("id") != null && !request.getParameter("id").isEmpty() ? Integer.parseInt(request.getParameter("id")) : null;
        String name = request.getParameter("name");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        Date registered = null;
        if (request.getParameter("registered") != null) {
            try {
                registered = new SimpleDateFormat("yyyy-MM-dd").parse(request.getParameter("registered"));
            } catch (ParseException e) {
                log.warn("saveUser(): Error converting date '{}'", request.getParameter("registered"));
                registered = new Date();
            }
        } else {
            registered = new Date();
        }

//        Date registered = request.getParameter("registered") != null ? new Date(request.getParameter("registered")) : new Date();

        boolean enabled = request.getParameter("enabled") == null || request.getParameter("enabled").equals("true");
        int caloriesPerDay = request.getParameter("caloriesPerDay") != null &&
                    !request.getParameter("caloriesPerDay").isEmpty()
                ? Integer.parseInt(request.getParameter("caloriesPerDay"))
                : MealsUtil.DEFAULT_CALORIES_PER_DAY;
        String[] roles = request.getParameterValues("roles");
//        Set<Role> roleSet = new HashSet<>(Set.of(Role.USER));
        Set<Role> roleSet = new HashSet<>();
        if (roles != null && roles.length > 0) {
            roleSet = Stream.of(roles).map(Role::valueOf).collect(Collectors.toSet());
        }
        if (roleSet.isEmpty()) {
            roleSet.add(Role.USER);
        }

        User user = new User(id, name, email, password, caloriesPerDay, enabled, registered, roleSet);
        if (id == null) {
            service.create(user);
        } else {
            service.update(user);
        }
        return "redirect:/users";
    }


}
