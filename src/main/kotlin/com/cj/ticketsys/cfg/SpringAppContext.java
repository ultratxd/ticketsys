package com.cj.ticketsys.cfg;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

/**
 * <p>Create Time: 2019年07月20日</p>
 * <p>@author tangxd</p>
 **/
@Component
public class SpringAppContext implements ApplicationContextAware {
    private static volatile ApplicationContext context;

    public static ConfigurableApplicationContext getContext() {
        return (ConfigurableApplicationContext) context;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public static <T> T getBean(Class<T> requestedType, Object... args) {
        if (context == null) {
            return null;
        }
        return context.getBean(requestedType, args);
    }

    public static Object getBean(String name, Object... args) {
        return context.getBean(name, args);
    }
}
