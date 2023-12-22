package com.example.bookshop.security.jwt.blacklist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Objects;

@Service
public class JWTBlackList implements LogoutHandler {

    private final JWTBlackListRepository jwtBlackListRepository;

    @Autowired
    public JWTBlackList(JWTBlackListRepository jwtBlackListRepository) {
        this.jwtBlackListRepository = jwtBlackListRepository;
    }

    @Override
    public void logout(HttpServletRequest httpServletRequest,
                       HttpServletResponse httpServletResponse, Authentication authentication) {

        if (Objects.nonNull(httpServletRequest.getCookies())) {
            JWTBlackListEntity jwtBlacklist = new JWTBlackListEntity();
            jwtBlacklist.setJwtToken(Arrays.stream(httpServletRequest.getCookies()).filter(c -> c.getName().equals("token"))
                    .findFirst().map(Cookie::getValue).orElse(null));
            if (jwtBlacklist.getJwtToken() != null) {
                jwtBlackListRepository.save(jwtBlacklist);
            }
        }

    }
}
