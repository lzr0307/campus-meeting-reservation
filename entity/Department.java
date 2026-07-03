package coursePractice.meetingMIS.entity;

import java.io.Serializable;

public class Department implements Serializable {
    private String id;
    private String name;
    private String description;

    public Department(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return id + " | " + name + " | " + description;
    }
}
