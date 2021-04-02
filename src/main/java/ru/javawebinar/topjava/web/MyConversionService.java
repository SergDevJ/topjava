package ru.javawebinar.topjava.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.stereotype.Component;
import ru.javawebinar.topjava.util.converter.StringToLocalDateConverter;
import ru.javawebinar.topjava.util.converter.StringToLocalTimeConverter;


//@Component
public class MyConversionService extends GenericConversionService {
//    @Autowired
//    private StringToLocalDateConverter localDateConverter;
//
//    @Autowired
//    private StringToLocalTimeConverter localTimeConverter;

    public MyConversionService() {
        addConverter(new StringToLocalDateConverter());
        addConverter(new StringToLocalTimeConverter());
//        addConverter(localDateConverter);
//        addConverter(localTimeConverter);
    }
}
