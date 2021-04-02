package ru.javawebinar.topjava.util.converter;

import org.springframework.core.convert.converter.Converter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class StringToLocalTimeConverter implements Converter<String, LocalTime> {

    @Override
    public LocalTime convert(String s) {
        return LocalTime.parse(s, DateTimeFormatter.ofPattern("HH:mm"));
    }
}
