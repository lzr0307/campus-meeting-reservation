package coursePractice.meetingMIS.entity;

public enum ConfirmationStatus {
    PENDING("待确认"),
    APPROVED("已确认"),
    REJECTED("已驳回"),
    CANCELLED("已撤销");

    private final String label;

    ConfirmationStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
