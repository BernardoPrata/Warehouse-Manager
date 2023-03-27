package ggc;

import java.io.Serializable;

public abstract class DeliveryMethod implements Serializable {
    public abstract void visitPartner(Observer o, Notification notification);
}
