package alvin.study.springboot.graphql.app.api.rest;

import jakarta.validation.Valid;

import org.hibernate.validator.constraints.Length;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import alvin.study.springboot.graphql.app.service.UserService;
import alvin.study.springboot.graphql.core.exception.UnauthorizedException;

@Slf4j
@Validated
@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {
    static record LoginForm(
            long orgId,
            @Length(min = 3, max = 50) String account,
            @Length(min = 6, max = 20) String password) {}

    static record LoginDto(String token) {}

    private final UserService userService;

    @PostMapping
    @ResponseBody
    LoginDto login(@Valid @RequestBody LoginForm form) {
        try {
            var token = userService.login(form.orgId(), form.account(), form.password());
            return new LoginDto(token);
        } catch (UnauthorizedException e) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}
