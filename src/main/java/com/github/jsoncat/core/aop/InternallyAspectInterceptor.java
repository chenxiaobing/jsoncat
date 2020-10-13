package com.github.jsoncat.core.aop;

import com.github.jsoncat.annotation.aop.After;
import com.github.jsoncat.annotation.aop.Before;
import com.github.jsoncat.annotation.aop.Pointcut;
import com.github.jsoncat.common.util.ReflectionUtil;
import com.github.jsoncat.core.aop.lang.JoinPoint;
import com.github.jsoncat.core.aop.lang.JoinPointImpl;
import com.github.jsoncat.core.aop.util.PatternMatchUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class InternallyAspectInterceptor extends Interceptor {

    private Object adviceBean;

    private final HashSet<String> expressionUrls = new HashSet<>();

    private final List<Method> beforeMethod = new ArrayList<>();

    private final List<Method> afterMethod = new ArrayList<>();

    public InternallyAspectInterceptor(Object adviceBean) {
        this.adviceBean = adviceBean;
        init();
    }

    private void init() {
        for (Method method : adviceBean.getClass().getMethods()) {
            Pointcut pointcut = method.getAnnotation(Pointcut.class);
            if (!Objects.isNull(pointcut)) {
                expressionUrls.add(pointcut.value());
            }
            Before before = method.getAnnotation(Before.class);
            if (!Objects.isNull(before)) {
                beforeMethod.add(method);
            }
            After after = method.getAnnotation(After.class);
            if (!Objects.isNull(after)) {
                afterMethod.add(method);
            }
        }
    }

    @Override
    public boolean supports(Object bean) {
        return expressionUrls.stream().anyMatch(url -> PatternMatchUtils.simpleMatch(url, bean.getClass().getName())) && (!beforeMethod.isEmpty() || !afterMethod.isEmpty());
    }

    /**
     * @param methodInvocation
     * @return
     */
    @Override
    public Object intercept(MethodInvocation methodInvocation) {
        JoinPoint joinPoint = new JoinPointImpl(adviceBean, methodInvocation.getTargetObject(),
                methodInvocation.getArgs());
        beforeMethod.forEach(method -> ReflectionUtil.executeTargetMethodNoResult(adviceBean, method, joinPoint));
        Object result = methodInvocation.proceed();
        afterMethod.forEach(method -> ReflectionUtil.executeTargetMethodNoResult(adviceBean, method, result, joinPoint));
        return result;
    }
}
