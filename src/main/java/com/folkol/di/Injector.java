package com.folkol.di;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class Injector
{
    private Config cfg;

    public Injector()
    {
        cfg = new Config();
    }

    public Injector(Config cfg)
    {
        this.cfg = cfg;
    }

    public static class Config
    {
        @FunctionalInterface
        public interface Binder
        {
            void to(Object impl);
        }

        Map<Class, Object> impls = new HashMap<>();

        public Object get(Class<?> type)
        {
            return impls.getOrDefault(type, type);
        }

        public Binder bind(Class<?> klazz)
        {
            return impl -> impls.put(klazz, impl);
        }
    }

    public <T> T create(Class<T> klazz)
        throws IllegalAccessException, InstantiationException
    {
        T t = klazz.newInstance();
        Field[] fields = t.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Inject.class)) {
                inject(field, t, cfg.get(field.getType()));
            }
        }
        return t;
    }

    private void inject(Field field, Object target, Object type)
        throws InstantiationException, IllegalAccessException
    {
        if (type instanceof Class) { // Gief multiple dispatch!
            field.set(target, create((Class) type));
        } else {
            field.set(target, type);
        }
    }
}
