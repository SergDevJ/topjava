package ru.javawebinar.topjava.util;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import ru.javawebinar.topjava.HasId;
import ru.javawebinar.topjava.util.exception.IllegalRequestDataException;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import javax.validation.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

//@Service
public class ValidationUtil {

//    @Autowired
    private static ApplicationContext ctx;

//    private static ReloadableResourceBundleMessageSource messageSource = ctx.getBean(ReloadableResourceBundleMessageSource.class);

//    @Autowired
    private static ReloadableResourceBundleMessageSource messageSource;


    private static final Validator validator;
//    public static final Map<String, RuntimeException> SQL_EXCEPTIONS = new HashMap<>();
//    public static final Map<String, String> SQL_EXCEPTIONS = new HashMap<>();
    public static final Properties SQL_EXCEPTIONS = new Properties();

    static {
        //  From Javadoc: implementations are thread-safe and instances are typically cached and reused.
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        //  From Javadoc: implementations of this interface must be thread-safe
        validator = factory.getValidator();

        ctx = ApplicationContextHolder.getApplicationContext();
        messageSource = ctx.getBean(ReloadableResourceBundleMessageSource.class);

//        SQL_EXCEPTIONS.put("users_unique_email_idx", new DataIntegrityViolationException("User with this email already exists"));
        String myEnv = System.getenv("TOPJAVA_ROOT");
        Properties sqlExceptionMsg = new Properties();
        String fn = myEnv + "/config/messages/sql.properties";
        try (InputStream is = new FileInputStream(fn)) {
            SQL_EXCEPTIONS.load(is);
        } catch (IOException ignored) { }

//        SQL_EXCEPTIONS.put("users_unique_email_idx", "error.db.duplicateEmail");
//        SQL_EXCEPTIONS.put("meals_unique_user_datetime_idx", "error.db.duplicateMealDatetime");
    }

//    @Autowired
//    public ValidationUtil(ReloadableResourceBundleMessageSource messageSource) {
//        ValidationUtil.messageSource = messageSource;
//    }

    public static <T> void validate(T bean) {
        // https://alexkosarev.name/2018/07/30/bean-validation-api/
        Set<ConstraintViolation<T>> violations = validator.validate(bean);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    public static <T> T checkNotFoundWithId(T object, int id) {
        checkNotFoundWithId(object != null, id);
        return object;
    }

    public static void checkNotFoundWithId(boolean found, int id) {
        checkNotFound(found, "id=" + id);
    }

    public static <T> T checkNotFound(T object, String msg) {
        checkNotFound(object != null, msg);
        return object;
    }

    public static void checkNotFound(boolean found, String msg) {
        if (!found) {
            throw new NotFoundException("Not found entity with " + msg);
        }
    }

    public static void checkNew(HasId bean) {
        if (!bean.isNew()) {
            throw new IllegalRequestDataException(bean + " must be new (id=null)");
        }
    }

    public static void assureIdConsistent(HasId bean, int id) {
//      conservative when you reply, but accept liberally (http://stackoverflow.com/a/32728226/548473)
        if (bean.isNew()) {
            bean.setId(id);
        } else if (bean.id() != id) {
            throw new IllegalRequestDataException(bean + " must be with id=" + id);
        }
    }

    //  https://stackoverflow.com/a/65442410/548473
    @NonNull
    public static Throwable getRootCause(@NonNull Throwable t) {
        Throwable rootCause = NestedExceptionUtils.getRootCause(t);
        return rootCause != null ? rootCause : t;
    }

    public static String getBindingErrorResultString(BindingResult result) {
        return result.getFieldErrors().stream()
                .map(fe -> String.format("[%s] %s", fe.getField(), fe.getDefaultMessage()))
                .collect(Collectors.joining("<br>"));
    }

    public static ResponseEntity<String> getErrorResponse(BindingResult result) {
        return ResponseEntity.unprocessableEntity().body(
                result.getFieldErrors().stream()
                        .map(fe -> String.format("[%s] %s", fe.getField(), fe.getDefaultMessage()))
                        .collect(Collectors.joining("<br>"))
        );
    }


    public static RuntimeException getSqlErrorException(RuntimeException e, Locale locale) {
        Throwable root = getRootCause(e);
        String messageKey = "";
        for (Map.Entry<Object, Object> entry: SQL_EXCEPTIONS.entrySet()) {
            if (root.getMessage().toLowerCase().contains(((String)entry.getKey()).toLowerCase())) {
                messageKey = (String) entry.getValue();
                String msg;
                try {
                    msg = messageSource.getMessage(messageKey, null, locale);
                } catch (NoSuchMessageException me) {
                    return e;
                }
                return new DataIntegrityViolationException(msg, e);
            }
        }
        return e;
    }


//    public static RuntimeException getSqlErrorException(RuntimeException e, Locale locale) {
//        Throwable root = getRootCause(e);
//        String messageKey = "";
//        for (Map.Entry<String, String> entry: SQL_EXCEPTIONS.entrySet()) {
//            if (root.getMessage().toLowerCase().contains(entry.getKey().toLowerCase())) {
//                messageKey = entry.getValue();
//                String msg;
//                try {
//                    msg = messageSource.getMessage(messageKey, null, locale);
//                } catch (NoSuchMessageException me) {
//                    return e;
//                }
//                return new DataIntegrityViolationException(msg, e);
//            }
//        }
//        return e;
//    }


//    private RuntimeException getDetailSqlErrorException(Throwable e) {
//        Throwable root = ValidationUtil.getRootCause(e);
//        for (Map.Entry<String, RuntimeException> exEntry: SQL_EXCEPTIONS.entrySet()) {
//            if (root.getMessage().toLowerCase().contains(exEntry.getKey().toLowerCase())) {
//                RuntimeException result = exEntry.getValue();
//                result.initCause(e);
//                return result;
//            }
//        }
//        return new RuntimeException(e.getMessage(), e);
//    }
}