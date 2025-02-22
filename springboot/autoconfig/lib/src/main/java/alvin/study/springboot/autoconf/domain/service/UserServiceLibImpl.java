package alvin.study.springboot.autoconf.domain.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import alvin.study.springboot.autoconf.domain.model.User;

@Service
@RequiredArgsConstructor
public class UserServiceLibImpl implements UserService {
    private final User user;

    @Override
    public User loadUser() {
        return user;
    }
}
