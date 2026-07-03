package coursePractice.meetingMIS.entity;

import java.io.Serializable;

public class MeetingRoom implements Serializable {
    private String roomNo;
    private String name;
    private String location;
    private int capacity;
    private String equipment;

    public MeetingRoom(String roomNo, String name, String location, int capacity, String equipment) {
        this.roomNo = roomNo;
        this.name = name;
        this.location = location;
        this.capacity = capacity;
        this.equipment = equipment;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    @Override
    public String toString() {
        return roomNo + " | " + name + " | " + location + " | 容量:" + capacity + " | " + equipment;
    }
}
