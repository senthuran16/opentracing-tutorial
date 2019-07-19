package eimock;

import io.jaegertracing.internal.JaegerTracer;
import io.opentracing.Span;
import lib.Tracing;

import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import java.lang.Thread;

public class EIMock {
    static List<MessageJump> messageJumps;
    static List<SpanEvent> spanEvents = new ArrayList<>();
    static Map<Integer, Span> startedSpans = new HashMap<>();

    static Map<Integer, List<Integer>> children = new HashMap<>();

    static boolean shouldConsiderParent;
    static String serviceName;
    static String messageFlowId;

    public static void main(String[] args) throws Exception {

        shouldConsiderParent = true;

        // ===== [SAMPLE 800 DEFAULT] ===================================

        // Non-Faulty
//        serviceName = "sample-800-test";
//        messageFlowId = "urn_uuid_a0893f25-6ead-4716-af65-6f07425ca47414976266353038";

        // Faulty
//        serviceName = "sample-800-test";
//        messageFlowId = "urn_uuid_837f584f-995e-455f-862c-6b95abd6296814059632336018";

        // ===== [SAMPLE WITH CLONE MEDIATOR] ============================

        // Non-Faulty
//        serviceName = "sample-with-clone-test";
//        messageFlowId = "urn_uuid_3b977eeb-1d8a-4fcb-af77-12257e27aa3f15852614409891";

        // ===============================================================

        // ===== [SAMPLE WITH ITERATE MEDIATOR] ============================

        // Non-Faulty
        serviceName = "sample-with-iterator-test";
        messageFlowId = "urn_uuid_56f3863c-df64-4d5c-8622-2c494646c46613757638670223";

        // ===============================================================

        if (!shouldConsiderParent) {
            serviceName = serviceName + "-without-parent";
        }

        List<MessageJump> messageJumps = DatabaseReader.getMessageJumps(messageFlowId);
        updateChildren(messageJumps);

        // Method 1
        simulateMethod1(messageJumps);

        // Method 2
        // simulateMethod2(messageJumps);
    }

    private static void simulateMethod1(List<MessageJump> messageJumps) {
        for (int i = 0; i < messageJumps.size(); i++) {
            addSpanEventsOfMessage(messageJumps.get(i));
        }
        Collections.sort(spanEvents);
        setDelaysBeforeEvents();
        simulateSpans();
    }

    public static void addSpanEventsOfMessage(MessageJump messageJump) {
        spanEvents.add(new SpanEvent(SpanEventType.START, messageJump.startTime, messageJump));
        spanEvents.add(new SpanEvent(SpanEventType.END, messageJump.endTime, messageJump));
    }

    private static void setDelaysBeforeEvents() {
        spanEvents.get(0).setDelayBeforeEvent(0);
        for (int i = 1; i < spanEvents.size(); i++) {
            long delay = Long.parseLong(spanEvents.get(i).eventTime) - Long.parseLong(spanEvents.get(i - 1).eventTime);
            spanEvents.get(i).setDelayBeforeEvent(delay);
        }
    }

    private static void simulateSpans() {
        try (JaegerTracer tracer = Tracing.init(serviceName)) {
            final Span rootSpan = tracer.buildSpan(serviceName + "-rootspan").start();

            for (int i = 0; i < spanEvents.size(); i++) {
                SpanEvent spanEvent = spanEvents.get(i);

                // Prevent sleep times due to 'IgnoreElement's
                if (i != 0) {
                    SpanEvent previousSpanEvent = spanEvents.get(i - 1);
                    if (Long.parseLong(previousSpanEvent.eventTime) != 0) {
                        // Some valid timestamp, not 0
                        Thread.sleep(spanEvent.delayBeforeEvent);
                    }
                }

                // Start or stop Span
                if (spanEvent.eventType == SpanEventType.START) {
                    if (shouldConsiderParent) {
                        // Immediate parent concepts are applicable
                        Span parentSpan = getParentSpan(spanEvent.messageJump);
                        if (parentSpan == null) {
                            // No parent. Most root span is the parent
                            Span startedSpan = SpanProducer.startSpan(tracer, rootSpan, spanEvent.messageJump);
                            startedSpans.put(spanEvent.messageJump.messageJumpId, startedSpan);
                        } else {
                            // There is another parent
                            Span startedSpan = SpanProducer.startSpan(tracer, parentSpan, spanEvent.messageJump);
                            startedSpans.put(spanEvent.messageJump.messageJumpId, startedSpan);
                        }
                    } else {
                        // No need to worry about immediate parents
                        Span startedSpan = SpanProducer.startSpan(tracer, rootSpan, spanEvent.messageJump);
                        startedSpans.put(spanEvent.messageJump.messageJumpId, startedSpan);
                    }
                } else {
                    SpanProducer.stopSpan(startedSpans.get(spanEvent.messageJump.messageJumpId));
                }
            }

            rootSpan.finish();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void updateChildren(List<MessageJump> messageJumps) {
        for (MessageJump messageJump : messageJumps) {
            children.put(messageJump.messageJumpId, Util.deriveChildren(messageJump.children));
        }
    }

    private static Span getParentSpan(MessageJump messageJump) {
        int parentMessageJumpId = getParentMessageJumpId(messageJump.messageJumpId);
        if (parentMessageJumpId == -1) {
            return null;
        }
        return startedSpans.get(parentMessageJumpId); // When root span is not already started, null is returned
    }

    private static int getParentMessageJumpId(int childMessageJumpId) {
        for (Map.Entry<Integer, List<Integer>> parentAndChildren : children.entrySet()) {
            if (parentAndChildren.getValue().contains(childMessageJumpId)) {
                return parentAndChildren.getKey();
            }
        }
        // Child message jump id doesn't exist under any parent
        return -1;
    }

    @Deprecated
    private static void simulateMethod2(List<MessageJump> messageJumps) throws Exception {
        Collections.sort(messageJumps);
        setDelaysFromInitialPoint(messageJumps);
        simulate(messageJumps);
    }

    private static void setDelaysFromInitialPoint(List<MessageJump> messageJumps) {
        String initialTimestamp = messageJumps.get(0).startTime;
        for (int i = 1; i < messageJumps.size(); i++) {
            messageJumps.get(i).setDelayFromTimestamp(initialTimestamp);
        }
    }

    private static void simulate(List<MessageJump> messageJumps) throws BrokenBarrierException, InterruptedException {
        final CyclicBarrier gate = new CyclicBarrier(messageJumps.size() + 1);
        Thread[] threads = new Thread[messageJumps.size()];

        try (JaegerTracer tracer = Tracing.init("stock-quote-api")) {
            final Span rootSpan = tracer.buildSpan("stock-quote-api").start();
            for (int i = 0; i < messageJumps.size(); i++) {
                final int j = i;
                threads[i] = new Thread(() -> {
                    try {
                        gate.await();
                        Thread.sleep(j);
                        SpanProducer.produceSpan(tracer, rootSpan, messageJumps.get(j));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                });
            }
            for (Thread thread : threads) {
                thread.start();
            }

            gate.await();
            System.out.println("Simulations started");

            for (Thread thread : threads) {
                thread.join();
            }

            rootSpan.finish();
        }
    }
}