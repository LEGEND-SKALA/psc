package navi.navi_be.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import navi.navi_be.auth.dto.SignupRequest;
import navi.navi_be.auth.model.UserDepartment;
import navi.navi_be.auth.model.UserRole;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String employeeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserDepartment department;

    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role; 

    public User(SignupRequest request) {
        this.name = request.getName();
        this.employeeId = request.getEmployeeId();
        this.department = UserDepartment.valueOf(request.getDepartment());  // 문자열 → enum
        this.email = request.getEmail();
        this.password = request.getPassword();
        this.role = UserRole.valueOf(request.getRole());
    }

    public void setRole(UserRole roleEnum) {
        this.role = roleEnum;
    }
}
