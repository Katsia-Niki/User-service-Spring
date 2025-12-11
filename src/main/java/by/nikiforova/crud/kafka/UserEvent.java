package by.nikiforova.crud.kafka;

public class UserEvent {
    private String operation;
    private String userEmail;

    public UserEvent() {
    }

    public UserEvent(String operation, String userEmail) {
        this.operation = operation;
        this.userEmail = userEmail;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
