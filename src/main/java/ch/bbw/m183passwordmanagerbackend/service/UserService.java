package ch.bbw.m183passwordmanagerbackend.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserService{

    private UserRepository userRepository;

    @Autowired
    public UserService (UserRepository userRepository) {this.userRepository = userRepository;}

}
