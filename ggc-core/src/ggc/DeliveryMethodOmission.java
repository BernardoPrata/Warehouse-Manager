package ggc;

/*Delivery Method created in case no DeliveryMethod is chosen */

public class DeliveryMethodOmission extends DeliveryMethod {

    public void visitPartner(Observer o, Notification notification) {
        o.addToInbox(notification); // in this case
    }
}
