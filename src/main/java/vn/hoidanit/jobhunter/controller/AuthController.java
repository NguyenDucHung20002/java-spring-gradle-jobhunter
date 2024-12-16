package vn.hoidanit.jobhunter.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.LoginDTO;
import vn.hoidanit.jobhunter.domain.dto.ResLoginDTO;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.SecurityUtil;
import vn.hoidanit.jobhunter.util.anotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("api/v1")
public class AuthController {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;

    @Value("${jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder,
                          SecurityUtil securityUtil, UserService userService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody LoginDTO loginDto) {
        // Nạp input gồm username/password vào Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDto.getUsername(), loginDto.getPassword());

        // xác thực người dùng => cần viết hàm loadUserByUsername
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // create a token
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User currentUser = this.userService.handleGetUserByUsername(loginDto.getUsername());

        ResLoginDTO res = new ResLoginDTO();


        if (currentUser != null) {
            ResLoginDTO.UserLogin userLogin = res.new UserLogin(currentUser.getId(), currentUser.getEmail(), currentUser.getName());
            res.setUserLogin(userLogin);
        }

        String access_token = this.securityUtil.createAccessToken(authentication.getName(), res.getUserLogin());
        res.setAccessToken(access_token);

        //refresh token
        String refreshToken = this.securityUtil.createRefreshToken(loginDto.getUsername(), res);
        this.userService.updateUserToken(refreshToken, loginDto.getUsername());

        //set cookies
        ResponseCookie responseCookie = ResponseCookie
                .from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(res);
    }

    @GetMapping("/auth/account")
    @ApiMessage("fetch account")
    public ResponseEntity<ResLoginDTO.UserGetAccount> getAccount() {
        String email = securityUtil.getCurrentUserLogin().isPresent() ?
                securityUtil.getCurrentUserLogin().get() : "";

        User currentUser = this.userService.handleGetUserByUsername(email);

        ResLoginDTO resLogin = new ResLoginDTO();
        ResLoginDTO.UserLogin userLogin = resLogin.new UserLogin();
        ResLoginDTO.UserGetAccount userAccount = resLogin.new UserGetAccount();


        if (currentUser != null) {
            userLogin.setId(currentUser.getId());
            userLogin.setEmail(currentUser.getEmail());
            userLogin.setName(currentUser.getName());
            userAccount.setUser(userLogin);
        }

        return ResponseEntity.ok().body(userAccount);
    }

    @GetMapping("/auth/refresh")
    @ApiMessage("Get user by refresh token")
    public ResponseEntity<ResLoginDTO> getRefreshToken(
            @CookieValue(name = "refresh_token", defaultValue = "notFoundCookie") String refresh_token
    ) throws IdInvalidException {
        if (refresh_token.equals("notFoundCookie")) {
            throw new IdInvalidException("Không tìm thấy Cookie");
        }
        Jwt decodedToken = this.securityUtil.checkInvalidRefreshToken(refresh_token);
        String email = decodedToken.getSubject();

        User currentUser = this.userService.getUserByRefreshTokenAndEmail(refresh_token, email);
        if (currentUser == null) {
            throw new IdInvalidException("Token không hợp lệ");
        }

        ResLoginDTO res = new ResLoginDTO();


        if (currentUser != null) {
            ResLoginDTO.UserLogin userLogin = res.new UserLogin(currentUser.getId(), currentUser.getEmail(), currentUser.getName());
            res.setUserLogin(userLogin);
        }

        String access_token = this.securityUtil.createAccessToken(email, res.getUserLogin());
        res.setAccessToken(access_token);

        //refresh token
        String createRefreshToken = this.securityUtil.createRefreshToken(email, res);
        this.userService.updateUserToken(createRefreshToken, email);

        //set cookies
        ResponseCookie responseCookie = ResponseCookie
                .from("refresh_token", createRefreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(res);
    }

    @GetMapping("/auth/logout")
    @ApiMessage("Logout user")
    public ResponseEntity<Void> logout() throws IdInvalidException {
        String email = this.securityUtil.getCurrentUserLogin().isPresent() ? this.securityUtil.getCurrentUserLogin().get() : "";

        if (email.equals("")) {
            throw new IdInvalidException("Access không hợp lệ");
        }

        this.userService.updateUserToken(null, email);

        ResponseCookie deleteCookie = ResponseCookie
                .from("refresh_token", null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, deleteCookie.toString()).body(null);
    }
}

