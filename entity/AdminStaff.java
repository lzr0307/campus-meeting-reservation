package coursePractice.meetingMIS.entity;

import java.io.Serializable;

public class AdminStaff implements Serializable {
    private String staffNo;
    private String name;
    private String gender;
    private String departmentId;
    private String title;
    private String phone;
    private String password;
    private Role role;

    public AdminStaff(String staffNo, String name, String gender, String departmentId,
                      String title, String phone, String password, Role role) {
        this.staffNo = staffNo;
        this.name = name;
        this.gender = gender;
        this.departmentId = departmentId;
        this.title = title;
        this.phone = phone;
        this.password = password;
        this.role = role;
    }

    public String getStaffNo() {
        return staffNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return staffNo + " | " + name + " | " + gender + " | 部门:" + departmentId
                + " | " + title + " | " + phone + " | " + role.getLabel();
    }
}
