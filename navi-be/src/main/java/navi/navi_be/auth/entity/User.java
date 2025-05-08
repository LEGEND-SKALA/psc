package navi.navi_be.auth.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import navi.navi_be.auth.dto.SignupRequest;

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
    private String department;
    private String email;
    private String password;

    public User(SignupRequest request) {
        this.name = request.getName();
        this.employeeId = request.getEmployeeId();
        this.department = request.getDepartment();
        this.email = request.getEmail();
        this.password = request.getPassword();
    }
}
