package alvin.study.springboot.autoconf.domain.service;

import alvin.study.springboot.autoconf.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceLibImpl implements UserService {
    private final User user;

    @Override
    public User loadUser() {
        return user;
    }
}
