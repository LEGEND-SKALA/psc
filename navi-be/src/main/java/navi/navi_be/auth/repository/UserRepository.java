package navi.navi_be.auth.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import navi.navi_be.auth.entity.User;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmployeeId(String employeeId);
    Optional<User> findByEmployeeId(String employeeId);
}
