package ru.javawebinar.topjava.web.json;

import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;

public class JsonContentNegotiatingViewResolver extends ContentNegotiatingViewResolver {
    private static final JsonContentNegotiatingViewResolver RESOLVER = new JsonContentNegotiatingViewResolver();

    private JsonContentNegotiatingViewResolver() {
//        final ContentNegotiatingViewResolver contentNegotiatingViewResolver = new ContentNegotiatingViewResolver();
//        contentNegotiatingViewResolver.setDefaultContentType(MediaType.APPLICATION_JSON);
//        final ArrayList defaultViews = new ArrayList();
//        defaultViews.add(mappingJacksonJsonView());
//        contentNegotiatingViewResolver.setDefaultViews(defaultViews);
//        return contentNegotiatingViewResolver;

    }

    public static JsonContentNegotiatingViewResolver getResolver() {
        return RESOLVER;
    }
}
