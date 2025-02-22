package alvin.study.springboot.autoconf;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import alvin.study.springboot.autoconf.domain.model.User;
import alvin.study.springboot.autoconf.domain.service.UserService;
import alvin.study.springboot.autoconf.util.TimeUtil;

@SpringBootTest
class AutoConfigTest {

    @Autowired
    private TimeUtil timeUtil;

    @Autowired
    private User user;

    @Autowired
    private UserService userService;

    @Test
    void timeZone_shouldInjectTimeZoneIntoTimeUtilObject() {
        then(timeUtil).isNotNull()
                .extracting(TimeUtil::getZoneId)
                .hasToString("Asia/Shanghai")
                .isNotNull();
    }

    @Test
    void user_shouldInjectUserNameIntoUserObject() {
        then(user).isNotNull()
                .extracting(User::getName)
                .hasToString("Emma")
                .isNotNull();
    }

    @Test
    void userService_shouldInjectUserService() {
        var user = userService.loadUser();
        then(user).isNotNull()
                .extracting(User::getName)
                .hasToString("Emma")
                .isNotNull();
    }

}
