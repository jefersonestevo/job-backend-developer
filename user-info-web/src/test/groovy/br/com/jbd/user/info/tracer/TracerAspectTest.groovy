package br.com.jbd.user.info.tracer

import io.opentracing.Span
import io.opentracing.Tracer
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.reflect.MethodSignature
import spock.lang.Specification

import java.lang.reflect.Method

class TracerAspectTest extends Specification {

    TracerAspect tracerAspect

    Tracer tracer
    Tracer.SpanBuilder spanBuilder
    Span span

    ProceedingJoinPoint jp

    def setup() {
        tracer = Mock(Tracer)
        spanBuilder = Mock(Tracer.SpanBuilder)
        span = Mock(Span)

        jp = Mock(ProceedingJoinPoint)

        tracerAspect = new TracerAspect()
        tracerAspect.tracer = tracer
    }

    def "trace aspect when annotation has no name and no tags"() {
        given: "A method with only the @Traced annotation"
        def methodName = "noName"
        def fullMethodName = "${TracedClass.class.simpleName}.${methodName}"
        def expectedResult = "result_${methodName}"

        MethodSignature signature = methodSignature(TracedClass.class.getMethod(methodName))
        jp.getSignature() >> signature
        1 * jp.proceed() >> expectedResult

        when: "We call the aspect"
        def result = tracerAspect.trace(jp)

        then: "It must return the result from the proceed method"
        result == expectedResult

        and: "Must build the span with the class.method name"
        1 * tracer.buildSpan(fullMethodName) >> spanBuilder

        and: "Must start the span"
        1 * spanBuilder.start() >> span

        and: "Must set the tag method on the span"
        1 * span.setTag("method", fullMethodName)

        and: "Must not set any other tag on the span"
        0 * span.setTag(_ as String, _ as String)

        and: "Must finish the span"
        1 * span.finish()
    }

    def "trace aspect when annotation has a name and no tags"() {
        given: "A method with the @Traced annotation with a name but no tags"
        def methodName = "onlyName"
        def fullMethodName = "${TracedClass.class.simpleName}.${methodName}"
        def expectedResult = "result_${methodName}"

        MethodSignature signature = methodSignature(TracedClass.class.getMethod(methodName))
        jp.getSignature() >> signature
        1 * jp.proceed() >> expectedResult

        when: "We call the aspect"
        def result = tracerAspect.trace(jp)

        then: "It must return the result from the proceed method"
        result == expectedResult

        and: "Must build the span with the annotation name"
        1 * tracer.buildSpan("MY_TRACE") >> spanBuilder

        and: "Must start the span"
        1 * spanBuilder.start() >> span

        and: "Must set the tag method on the span"
        1 * span.setTag("method", fullMethodName)

        and: "Must not set any other tag on the span"
        0 * span.setTag(_ as String, _ as String)

        and: "Must finish the span"
        1 * span.finish()
    }

    def "trace aspect when annotation has a name and multiple tags"() {
        given: "A method with the @Traced annotation with a name and multiple tags"
        def methodName = "nameAndTags"
        def fullMethodName = "${TracedClass.class.simpleName}.${methodName}"
        def expectedResult = "result_${methodName}"

        MethodSignature signature = methodSignature(TracedClass.class.getMethod(methodName))
        jp.getSignature() >> signature
        1 * jp.proceed() >> expectedResult

        when: "We call the aspect"
        def result = tracerAspect.trace(jp)

        then: "It must return the result from the proceed method"
        result == expectedResult

        and: "Must build the span with the annotation name"
        1 * tracer.buildSpan("MY_TRACE_2") >> spanBuilder

        and: "Must start the span"
        1 * spanBuilder.start() >> span

        and: "Must set the tag method on the span"
        1 * span.setTag("method", fullMethodName)

        and: "Must set all the tag from the method on the span"
        1 * span.setTag("TAG_1", "VALUE_1")
        1 * span.setTag("TAG_2", "VALUE_2")

        and: "Must not set any other tag on the span"
        0 * span.setTag(_ as String, _ as String)

        and: "Must finish the span"
        1 * span.finish()
    }

    def "trace aspect with exception on the proceed joinpoint"() {
        given: "A method with only the @Traced annotation"
        def methodName = "noName"
        def fullMethodName = "${TracedClass.class.simpleName}.${methodName}"
        def expectedException = new RuntimeException("An exception")

        MethodSignature signature = methodSignature(TracedClass.class.getMethod(methodName))
        jp.getSignature() >> signature
        1 * jp.proceed() >> {throw expectedException}

        when: "We call the aspect"
        tracerAspect.trace(jp)

        then: "It must throw the exception"
        def e = thrown(RuntimeException)
        expectedException == e

        and: "Must build the span with the class.method name"
        1 * tracer.buildSpan(fullMethodName) >> spanBuilder

        and: "Must start the span"
        1 * spanBuilder.start() >> span

        and: "Must set the tag method on the span"
        1 * span.setTag("method", fullMethodName)

        and: "Must set all the error tags on the span"
        1 * span.setTag("error", true)
        1 * span.setTag("EXCEPTION", expectedException.class.name)
        1 * span.setTag("EXCEPTION_MESSAGE", expectedException.message)

        and: "Must not set any other tag on the span"
        0 * span.setTag(_ as String, _ as String)

        and: "Must finish the span"
        1 * span.finish()
    }

    MethodSignature methodSignature(Method method) {
        MethodSignature signature = Mock(MethodSignature)
        signature.name >> method.name
        signature.returnType >> method.returnType
        signature.method >> method
        signature.declaringType >> method.declaringClass
        signature
    }

    static class TracedClass {

        @Traced
        String noName() {
            "noName"
        }

        @Traced("MY_TRACE")
        String onlyName() {
            "onlyName"
        }

        @Traced(value = "MY_TRACE_2", tags = [@TracedTag(name = "TAG_1", value = "VALUE_1"), @TracedTag(name = "TAG_2", value = "VALUE_2")])
        String nameAndTags() {
            "nameAndTags"
        }

    }
}
