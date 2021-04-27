package ru.javawebinar.topjava.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextHolder {
    private static ApplicationContext ctx;

    @Autowired
    public ApplicationContextHolder(ApplicationContext ctx) {
        ApplicationContextHolder.ctx = ctx;
    }

    public static ApplicationContext getApplicationContext() {
        return ctx;
    }
}
