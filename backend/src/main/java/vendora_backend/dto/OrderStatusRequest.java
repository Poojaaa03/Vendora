package vendora_backend.dto;

public class OrderStatusRequest {

    private String status;

    public OrderStatusRequest() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}