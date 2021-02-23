package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class JdbcMealRepository implements MealRepository {
    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert insertEntity;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private static final BeanPropertyRowMapper<Meal> ROW_MAPPER = BeanPropertyRowMapper.newInstance(Meal.class);



    @Autowired
    JdbcMealRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.insertEntity = new SimpleJdbcInsert(jdbcTemplate).withTableName("meals").usingGeneratedKeyColumns("id");
    }


    @Override
    public Meal save(Meal meal, int userId) {
        meal = new Meal(meal);
        MapSqlParameterSource mapParameter = new MapSqlParameterSource();
        mapParameter.addValue("id", meal.getId()).
                addValue("user_id", userId).
                addValue("description", meal.getDescription()).
                addValue("dateTime", Timestamp.valueOf(meal.getDateTime())).
                addValue("calories", meal.getCalories());
        if (meal.isNew()) {
            Number id = insertEntity.executeAndReturnKey(mapParameter);
            meal.setId(id.intValue());
        } else {
            if (namedParameterJdbcTemplate.update("UPDATE meals SET description = :description, dateTime = :dateTime, calories = :calories, user_id = :user_id WHERE id = :id", mapParameter) == 0)
                return null;
        }
        return meal;
    }

    @Override
    public boolean delete(int id, int userId) {
        return jdbcTemplate.update("DELETE FROM meals WHERE id = ? AND user_id = ?", id, userId) != 0;
    }

    @Override
    public Meal get(int id, int userId) {
        return jdbcTemplate.queryForObject("SELECT * FROM meals WHERE id = ? AND user_id = ?", ROW_MAPPER, id, userId);
    }

    @Override
    public List<Meal> getAll(int userId) {
        RowMapper<Meal> mapper = new RowMapper<Meal>() {
            @Override
            public Meal mapRow(ResultSet rs, int rowNum) throws SQLException {
                Meal meal = new Meal(rs.getInt("id"),
                        rs.getTimestamp("dateTime")
//                                .toInstant().atZone(TimeZone.getTimeZone("UTC").toZoneId())
                                .toLocalDateTime(),
                        rs.getString("description"),
                        rs.getInt("calories"));
                return meal;
            }
        };
        return jdbcTemplate.query("SELECT * FROM meals WHERE user_id = ?" +
                " ORDER BY dateTime DESC", mapper, userId);
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
//        startDateTime = startDateTime.toLocalDate().atTime(LocalTime.MIN);
//        endDateTime = endDateTime.toLocalDate().plusDays(1).atTime(LocalTime.MIN);
        Timestamp startTimestamp = Timestamp.valueOf(startDateTime);
        Timestamp endTimestamp = Timestamp.valueOf(endDateTime);
        return jdbcTemplate.query("SELECT * FROM meals WHERE user_id = ?" +
                " AND dateTime >= ? AND dateTime < ?" +
                " ORDER BY dateTime DESC",
                ROW_MAPPER, userId, startTimestamp, endTimestamp);
    }
}
