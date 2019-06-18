package br.com.jbd.user.info.tracer;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class TracerAspect {

    @Autowired
    private Tracer tracer;

    @Around("@annotation(br.com.jbd.user.info.tracer.Traced)")
    public Object trace(ProceedingJoinPoint jp) throws Throwable {
        String methodName = jp.getSignature().getDeclaringType().getSimpleName() + "." + jp.getSignature().getName();
        Traced annotation = getAnnotation(jp);

        String spanName = !"".equals(annotation.value().trim()) ? annotation.value() : methodName;

        Span span = tracer.buildSpan(spanName).start();
        span.setTag("method", methodName);

        try (Scope scope = tracer.activateSpan(span)) {
            for (TracedTag tag : annotation.tags()) {
                span.setTag(tag.name(), tag.value());
            }

            return jp.proceed();
        } catch (Exception e) {
            span.setTag("error", true);
            span.setTag("EXCEPTION", e.getClass().getName());
            span.setTag("EXCEPTION_MESSAGE", e.getMessage());
            throw e;
        } finally {
            span.finish();
        }
    }

    private Traced getAnnotation(ProceedingJoinPoint jp) {
        MethodSignature signature = (MethodSignature) jp.getSignature();
        Method method = signature.getMethod();

        return AnnotationUtils.findAnnotation(method, Traced.class);
    }

}
