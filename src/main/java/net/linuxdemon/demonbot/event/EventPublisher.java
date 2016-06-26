package net.linuxdemon.demonbot.event;


import java.lang.reflect.Method;

public class EventPublisher {

    public static void raiseEvent(final Event event) {
        new Thread() {
            @Override
            public void run() {
                raise(event);
            }
        }.start();
    }

    public static void raise(final Event event) {
        for (Class handler : HandlerRegistry.getHandlers()) {
            Method[] methods = handler.getMethods();

            for (Method method : methods) {
                SubscribeEvent eventHandler = method.getAnnotation(SubscribeEvent.class);
                if (eventHandler != null) {
                    Class[] methodParams = method.getParameterTypes();

                    if (methodParams.length < 1) {
                        continue;
                    }

                    if (!event.getClass().getSimpleName()
                            .equals(methodParams[0].getSimpleName())) {
                        continue;
                    }

                    // defence from runtime exceptions:
                    try {
                        method.invoke(handler.newInstance(), event);
                    } catch (Exception e) {
                        e.printStackTrace();
                        //System.err.println( e );
                    }
                }
            }
        }
    }
}