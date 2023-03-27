package ggc;

public interface Observer {

    String getId();

    public default void addToInbox(Notification notification) {
    }

    public void accept(Notification notification); // deve receber notificação
}
