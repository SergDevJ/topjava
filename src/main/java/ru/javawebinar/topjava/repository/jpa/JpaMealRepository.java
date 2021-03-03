package ru.javawebinar.topjava.repository.jpa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.MealRepository;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class JpaMealRepository implements MealRepository {

    private static final Logger log = LoggerFactory.getLogger(JpaMealRepository.class);

    @PersistenceContext
    private EntityManager em;


    @Override
    @Transactional
    public Meal save(Meal meal, int userId) {
        User user = em.getReference(User.class, userId);
        if (meal.isNew()) {
            meal.setUser(user);
            em.persist(meal);
        } else {
            Meal find = em.find(Meal.class, meal.getId());
            if (find == null) {
                log.warn("save(): can't find meal with id {}", meal.getId());
                return null;
            }
            if (find.getUser().getId() != userId) {
                log.warn("save(): can't save meal with id {} for user id {}", meal.getId(), userId);
                return null;
            }
            meal.setUser(user);
            meal = em.merge(meal);
        }
        return meal;
    }

    @Override
    @Transactional
    public boolean delete(int id, int userId) {
        return em.createNamedQuery(Meal.DELETE)
                .setParameter("user_id", userId)
                .setParameter("id", id)
                .executeUpdate() != 0;
    }

    @Override
    @Transactional(readOnly = true)
    public Meal get(int id, int userId) {
        return em.createNamedQuery(Meal.GET_BY_ID, Meal.class)
                .setParameter("user_id", userId)
                .setParameter("id", id)
                .getResultList().stream().findAny().orElse(null);
//        Meal meal = em.find(Meal.class, id);
//        return meal.getUser().getId() == userId ? meal : null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Meal> getAll(int userId) {
        User user = em.getReference(User.class, userId);
        if (user == null) {
            log.warn("getBetweenHalfOpen: can't find user with id {}", userId);
            return Collections.emptyList();
        }
        return em.createNamedQuery(Meal.ALL, Meal.class).setParameter("user", user).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
       User user = em.getReference(User.class, userId);
       if (user == null) {
           log.warn("getBetweenHalfOpen: can't find user with id {}", userId);
           return Collections.emptyList();
       }
       return em.createNamedQuery(Meal.FILTER_BY_DATE, Meal.class)
               .setParameter("user", user)
               .setParameter("startDate", startDateTime)
               .setParameter("endDate", endDateTime)
               .getResultList();
    }
}