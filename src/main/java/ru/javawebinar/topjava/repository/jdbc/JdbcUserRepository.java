package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;
import ru.javawebinar.topjava.util.ValidationUtil;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Transactional(readOnly = true)
public class JdbcUserRepository implements UserRepository {

    private static final BeanPropertyRowMapper<User> ROW_MAPPER = BeanPropertyRowMapper.newInstance(User.class);
    private static final BeanPropertyRowMapper<Role> ROLE_ROW_MAPPER = BeanPropertyRowMapper.newInstance(Role.class);

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertUser;

    private final Validator validator;

    @Autowired
    public JdbcUserRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.insertUser = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;

        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    public <T> String getTemplateString(String template, Collection<T> params, String delimiter) {
        String paramTemplate = "%?%";
        StringBuilder result = new StringBuilder();
        for (T param: params) {
            result.append(template.replace(paramTemplate, param.toString())).append(delimiter);
        }
        if (result.length() >= delimiter.length()) {
            result.setLength(result.length() - delimiter.length());
        }
        return result.toString();
    }



    @Override
    @Transactional
    public User save(User user) {

        ValidationUtil.checkConstraintViolation(user);

        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);

        if (user.isNew()) {
            Number newKey = insertUser.executeAndReturnKey(parameterSource);
            user.setId(newKey.intValue());
        } else if (namedParameterJdbcTemplate.update("UPDATE users SET name=:name, email=:email, password=:password, " +
                "registered=:registered, enabled=:enabled, calories_per_day=:caloriesPerDay WHERE id=:id"
                ,parameterSource) == 0) {
            return null;
        }

        String query = String.format("delete from user_roles where user_id = ? and not role in (%s)",
                getTemplateString("'%?%'", user.getRoles(), ", "));
        int rowCount = jdbcTemplate.update(query, user.getId());

        List<Role> existsRoles = getRolesList(user.getId());
        user.getRoles().removeAll(existsRoles);


        if (user.getRoles().size() > 0) {
            query = String.format("insert into user_roles (user_id, role) values %s",
                    getTemplateString("(:user_id, '%?%')", user.getRoles(), ", "));
            Map<String, Integer> parameterMap = new HashMap<>();
            parameterMap.put("user_id", user.getId());
            rowCount = namedParameterJdbcTemplate.update(query, parameterMap);
        }

        user.getRoles().addAll(existsRoles);
        return user;
    }

    @Override
    @Transactional
    public boolean delete(int id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id=?", id) != 0;
    }

    @Override
    public User get(int id) {
//        jdbcTemplate.batchUpdate()
        List<User> users = jdbcTemplate.query("SELECT * FROM users WHERE id=?", ROW_MAPPER, id);
        User result = DataAccessUtils.singleResult(users);
        if (result != null) {
            result.setRoles(getRolesList(id));
        }
        return result;
    }

    @Override
    public User getByEmail(String email) {
//        return jdbcTemplate.queryForObject("SELECT * FROM users WHERE email=?", ROW_MAPPER, email);
        List<User> users = jdbcTemplate.query("SELECT * FROM users WHERE email=?", ROW_MAPPER, email);
        User result = DataAccessUtils.singleResult(users);
        if (result != null) {
            result.setRoles(getRolesList(result.getId()));
        }
        return result;
    }

    @Override
    public List<User> getAll() {
        Map<Integer, User> userMap = new HashMap<>();
        List<User> users = jdbcTemplate.query("SELECT * FROM users ORDER BY name, email", ROW_MAPPER);
        users.forEach(u -> {
            u.setRoles(Set.of());
            userMap.put(u.getId(), u);
        });
        SqlRowSet rs = jdbcTemplate.queryForRowSet("SELECT * FROM user_roles");
        while (rs.next()) {
            userMap.get(rs.getInt(1)).getRoles().add(Role.valueOf(rs.getString(2)));
        }
        return users;
    }


    public List<Role> getRolesList(int userId) {
        RowMapper<Role> mapper = (rs, rowNum) -> Role.valueOf(rs.getString(1));
        return jdbcTemplate.query("SELECT role FROM user_roles WHERE user_id = ?", mapper, userId);
    }

    public Set<Role> getRoles(int userId) {
        return new HashSet<>(getRolesList(userId));
    }
}
