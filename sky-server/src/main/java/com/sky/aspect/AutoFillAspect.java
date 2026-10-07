package com.sky.aspect;

import com.sky.anonotation.AutoFill;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    /**
     * 切入点
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.anonotation.AutoFill)")
    public void pointcut() {}

    @Before("pointcut()")
    public void before(JoinPoint joinPoint) {
        // 获取注解操作类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        OperationType operationType = autoFill.value();

        // 获取方法参数
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return;
        }
        Object entity = args[0];

        // 设置创建时间、更新时间、创建人、更新人
        LocalDateTime now = LocalDateTime.now();
        Long id = BaseContext.getCurrentId();
        if (operationType == OperationType.INSERT) {
            try {
                Method setCreateTime = entity.getClass().getMethod("setCreateTime", LocalDateTime.class);
                Method setUpdateTime = entity.getClass().getMethod("setUpdateTime", LocalDateTime.class);
                Method setCreateUser = entity.getClass().getMethod("setCreateUser", Long.class);
                Method setUpdateUser = entity.getClass().getMethod("setUpdateUser", Long.class);

                setCreateTime.invoke(entity, now);
                setUpdateTime.invoke(entity, now);
                setCreateUser.invoke(entity, id);
                setUpdateUser.invoke(entity, id);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (operationType == OperationType.UPDATE) {
            try {
                Method setUpdateTime = entity.getClass().getMethod("setUpdateTime", LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getMethod("setUpdateUser", Long.class);

                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, id);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
    }
}
