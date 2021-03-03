package ru.javawebinar.topjava.model;

import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "meals")
@NamedQueries({
        @NamedQuery(name = Meal.DELETE, query = "DELETE FROM Meal m WHERE m.user.id = :user_id " +
                "AND m.id = :id"),
        @NamedQuery(name = Meal.ALL, query = "SELECT m FROM Meal m WHERE m.user = :user ORDER BY m.dateTime DESC"),
        @NamedQuery(name = Meal.FILTER_BY_DATE, query = "SELECT m FROM Meal m WHERE m.user = :user " +
                "AND m.dateTime >= :startDate AND m.dateTime < :endDate " +
                "ORDER BY m.dateTime DESC"),
        @NamedQuery(name = Meal.GET_BY_ID, query = "SELECT m FROM Meal m WHERE m.user.id = :user_id AND m.id = :id")
})
public class Meal extends AbstractBaseEntity {

    public static final String DELETE = "meal.DELETE";
    public static final String ALL = "meal.ALL";
    public static final String FILTER_BY_DATE = "meal.BY_DATE";
    public static final String GET_BY_ID = "meal.GET_BY_ID";

    @Access(AccessType.FIELD)
    @Column(name = "date_time", nullable = false, columnDefinition = "timestamp")
//    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private LocalDateTime dateTime;


//    @Access(AccessType.PROPERTY)
//    @Column(name = "date_time", nullable = false, columnDefinition = "timestamp")
//    @Temporal(TemporalType.TIMESTAMP)
//    @NotNull
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    private String description;

    private int calories;



    public Meal() {
    }

    public Meal(LocalDateTime dateTime, String description, int calories) {
        this(null, dateTime, description, calories);
    }

    public Meal(Integer id, LocalDateTime dateTime, String description, int calories) {
        super(id);
        this.dateTime = dateTime;
        this.description = description;
        this.calories = calories;
    }


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }


    @Column(name = "description", nullable = false)
    @NotBlank
    public String getDescription() {
        return description;
    }

    @Column(name = "calories", nullable = false)
    @NotNull
    @Range(min = 10, max = 10000)
    public int getCalories() {
        return calories;
    }


    @Transient
    public LocalDate getDate() {
        return dateTime.toLocalDate();
    }

    @Transient
    public LocalTime getTime() {
        return dateTime.toLocalTime();
    }


    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }


    @Override
    public String toString() {
        return "Meal{" +
                "id=" + id +
                ", dateTime=" + dateTime +
                ", description='" + description + '\'' +
                ", calories=" + calories +
                '}';
    }
}
