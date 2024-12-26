package vn.hoidanit.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.userDTO.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.response.userDTO.ResUserDTO;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.anotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("api/v1")
public class UserController {
    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    @ApiMessage("create new user")
    public ResponseEntity<ResCreateUserDTO> createNewUser(@Valid @RequestBody User postManUser) throws IdInvalidException {
        boolean isEmailExist = userService.isEmailExited(postManUser.getEmail());
        if (isEmailExist) {
            throw new IdInvalidException("Email " + postManUser.getEmail() + " đã tồn tại");
        }
        String hashPassword = this.passwordEncoder.encode(postManUser.getPassword());
        postManUser.setPassword(hashPassword);
        User ericUser = this.userService.handleCreateUser(postManUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUserDTO(ericUser));
    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("delete user")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id)
            throws IdInvalidException {
        User currentUser = this.userService.fetchUserById(id);
        if (currentUser == null) {
            throw new IdInvalidException("Id không tồn tại");
        }

        this.userService.handleDeleteUser(id);
        return ResponseEntity.ok(null);
        // return ResponseEntity.status(HttpStatus.OK).body("ericUser");
    }

    // fetch user by id
    @GetMapping("/users/{id}")
    @ApiMessage("get a user")
    public ResponseEntity<ResUserDTO> getUserById(@PathVariable("id") long id) throws IdInvalidException {
        User fetchUser = this.userService.fetchUserById(id);
        if (fetchUser == null) {
            throw new IdInvalidException("Id không tồn tại");
        }

        return ResponseEntity.status(HttpStatus.OK).body(this.userService.convertToResUserDTO(fetchUser));
    }

    // fetch all users
    @GetMapping("/users")
    @ApiMessage("fetch all users")
    public ResponseEntity<ResultPaginationDTO> getAllUser(
            @Filter Specification<User> spec,
            Pageable pageable) {

        return ResponseEntity.status(HttpStatus.OK).body(
                this.userService.fetchAllUser(spec, pageable));
    }

    @PutMapping("/users")
    @ApiMessage("update user")
    public ResponseEntity<ResUserDTO> updateUser(@RequestBody User user) {
        User ericUser = this.userService.handleUpdateUser(user);
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.convertToResUserDTO(ericUser));
    }

}
