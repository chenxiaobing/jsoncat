package com.github.jsoncat.factory;

import com.github.jsoncat.annotation.aop.Aspect;
import com.github.jsoncat.annotation.ioc.Component;
import com.github.jsoncat.annotation.springmvc.RestController;
import com.github.jsoncat.common.util.ReflectionUtil;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author shuang.kou
 * @createTime 2020年09月30日 15:26:00
 **/
public class ClassFactory {
    public static final Map<Class<? extends Annotation>, Set<Class<?>>> CLASSES = new ConcurrentHashMap<>();

    public static void loadClass(String packageName) {
        Set<Class<?>> restControllerSets = ReflectionUtil.scanAnnotatedClass(packageName, RestController.class);
        Set<Class<?>> componentSets = ReflectionUtil.scanAnnotatedClass(packageName, Component.class);
        Set<Class<?>> aspectSets = ReflectionUtil.scanAnnotatedClass(packageName, Aspect.class);
        CLASSES.put(RestController.class, restControllerSets);
        CLASSES.put(Component.class, componentSets);
        CLASSES.put(Aspect.class, aspectSets);
    }

}
