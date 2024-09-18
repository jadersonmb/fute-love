package com.jm.futelove.controllers;

import com.jm.futelove.dto.UserDTO;
import com.jm.futelove.execption.FuteLoveException;
import com.jm.futelove.execption.Problem;
import com.jm.futelove.services.UserService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserDTO userDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userDTO));
    }

    @GetMapping
    public ResponseEntity<?> listAll(Pageable pageable, UserDTO filter) {
        logger.debug("REST request to get all users");

        Page<UserDTO> listAllAccountDTO = userService.findAll(pageable, filter);
        return ResponseEntity.ok().body(listAllAccountDTO);
    }

    @PutMapping
    public ResponseEntity<?> update(@RequestBody UserDTO userDTO) {
        logger.debug("REST request to update User : {}", userDTO);

        UserDTO userSaveDTO = userService.findById(userDTO.getId());
        if(Objects.nonNull(userSaveDTO.getId())) {
            BeanUtils.copyProperties(userDTO, userSaveDTO, "id");
            userService.createUser(userSaveDTO);
        }
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler({FuteLoveException.class})
    public ResponseEntity<?> FuteLoveException(FuteLoveException ex) {
        Problem problem = createProblemBuild(ex.getStatus(), ex.getDetails(), ex.getType(), ex.getTitle())
                .build();
        return ResponseEntity.badRequest().body(problem);
    }

    private Problem.ProblemBuilder createProblemBuild(Integer status, String detail, String type, String title) {
        return Problem.builder()
                .status(status)
                .details(detail)
                .type(type)
                .title(title);
    }
}
