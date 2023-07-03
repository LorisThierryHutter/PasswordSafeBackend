package ch.bbw.m183passwordmanagerbackend.service;

import ch.bbw.m183passwordmanagerbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
