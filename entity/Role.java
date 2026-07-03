package coursePractice.meetingMIS.entity;

public enum Role {
    SYSTEM_ADMIN("系统管理员"),
    ROOM_MANAGER("会议室管理员"),
    STAFF("行政人员");

    private final String label;

    Role(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
