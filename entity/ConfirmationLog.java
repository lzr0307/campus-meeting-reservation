package coursePractice.meetingMIS.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ConfirmationLog implements Serializable {
    private String confirmationId;
    private String reservationId;
    private String confirmerNo;
    private ConfirmationStatus status;
    private String opinion;
    private LocalDateTime confirmationTime;

    public ConfirmationLog(String confirmationId, String reservationId, String confirmerNo,
                           ConfirmationStatus status, String opinion, LocalDateTime confirmationTime) {
        this.confirmationId = confirmationId;
        this.reservationId = reservationId;
        this.confirmerNo = confirmerNo;
        this.status = status;
        this.opinion = opinion;
        this.confirmationTime = confirmationTime;
    }

    @Override
    public String toString() {
        return confirmationId + " | 预约:" + reservationId + " | 确认人:" + confirmerNo
                + " | " + status.getLabel() + " | " + opinion + " | " + confirmationTime;
    }
}
