package lt.ivl.components.domain;

public enum RepairStatus {
    PENDING("Laukiama", "Laukiama"),
    CONFIRMED("Patvirtinta", "Vykdoma"),
    DIAGNOSTIC_WAITING("Laukiama diagnostikos", "Vykdoma"),
    DIAGNOSTIC("Diagnostika", "Vykdoma"),
    PAYMENT_CONFIRM_WAITING("Laukiama mok. patv.", "Laukiama patvirtinimo"),
    PAYMENT_CONFIRMED("Mokėjimas patvirtintas", "Vykdoma"),
    PAYMENT_CANCELED("Mokėjimas atmestas", "Vykdoma"),
    REPAIR_WAITING("Laukiama remonto", "Vykdoma"),
    REPAIR("Remontas", "Vykdoma"),
    RETURN("Grąžinti", "Laukiame atvykstant"),
    COMPLETED("Užbaigtas", "Užbaigtas");

    private String messageForCustomer;
    private String messageForEmployee;

    RepairStatus() {
    }

    RepairStatus(String messageForEmployee, String messageForCustomer) {
        this.messageForCustomer = messageForCustomer;
        this.messageForEmployee = messageForEmployee;
    }

    public String getMessageForCustomer() {
        return messageForCustomer;
    }

    public String getMessageForEmployee() {
        return messageForEmployee;
    }
}
