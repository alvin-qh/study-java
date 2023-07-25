package alvin.study.domain.service;

import alvin.study.domain.model.User;
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
