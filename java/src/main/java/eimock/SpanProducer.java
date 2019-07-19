package eimock;

import io.opentracing.Span;
import io.opentracing.Tracer;

public class SpanProducer {
    public static Span startSpan(Tracer tracer, Span rootSpan, MessageJump messageJump) {
        Span span = tracer.buildSpan(messageJump.componentName).asChildOf(rootSpan).start();
        span.setTag("metaTenantId", messageJump.metaTenantId);
        span.setTag("messageFlowId", messageJump.messageFlowId);
        span.setTag("host", messageJump.host);
        span.setTag("hashcode", messageJump.hashcode);
        span.setTag("componentName", messageJump.componentName);
        span.setTag("componentType", messageJump.componentType);
        span.setTag("componentIndex", messageJump.componentIndex);
        span.setTag("componentId", messageJump.componentId);
        span.setTag("startTime", messageJump.startTime);
        span.setTag("endTime", messageJump.endTime);
        span.setTag("duration", messageJump.duration);
        span.setTag("beforePayload", messageJump.beforePayload);
        span.setTag("afterPayload", messageJump.afterPayload);
        span.setTag("contextPropertyMap", messageJump.contextPropertyMap);
        span.setTag("transportPropertyMap", messageJump.transportPropertyMap);
        span.setTag("children", messageJump.children);
        span.setTag("entryPoint", messageJump.entryPoint);
        span.setTag("entryPointHashcode", messageJump.entryPointHashcode);
        span.setTag("faultCount", messageJump.faultCount);
        span.setTag("eventTimestamp", messageJump.eventTimestamp);
        return span;
    }

    public static void stopSpan(Span span) {
        span.finish();
    }

    @Deprecated
    public static void produceSpan(Tracer tracer, Span rootSpan, MessageJump messageJump) {
        Span span = tracer.buildSpan(messageJump.componentName).asChildOf(rootSpan).start();
        try {
            span.setTag("metaTenantId", messageJump.metaTenantId);
            span.setTag("messageFlowId", messageJump.messageFlowId);
            span.setTag("host", messageJump.host);
            span.setTag("hashcode", messageJump.hashcode);
            span.setTag("componentName", messageJump.componentName);
            span.setTag("componentType", messageJump.componentType);
            span.setTag("componentIndex", messageJump.componentIndex);
            span.setTag("componentId", messageJump.componentId);
            span.setTag("startTime", messageJump.startTime);
            span.setTag("endTime", messageJump.endTime);
            span.setTag("duration", messageJump.duration);
            span.setTag("beforePayload", messageJump.beforePayload);
            span.setTag("afterPayload", messageJump.afterPayload);
            span.setTag("contextPropertyMap", messageJump.contextPropertyMap);
            span.setTag("transportPropertyMap", messageJump.transportPropertyMap);
            span.setTag("children", messageJump.children);
            span.setTag("entryPoint", messageJump.entryPoint);
            span.setTag("entryPointHashcode", messageJump.entryPointHashcode);
            span.setTag("faultCount", messageJump.faultCount);
            span.setTag("eventTimestamp", messageJump.eventTimestamp);

            Thread.sleep(messageJump.duration);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            span.finish();
        }
    }
}
