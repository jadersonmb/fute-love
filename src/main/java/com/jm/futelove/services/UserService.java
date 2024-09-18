package com.jm.futelove.services;

import com.jm.futelove.dto.UserDTO;
import com.jm.futelove.entity.User;
import com.jm.futelove.execption.FuteLoveException;
import com.jm.futelove.execption.ProblemType;
import com.jm.futelove.mappers.UserMapper;
import com.jm.futelove.repository.UserRepository;
import com.jm.futelove.speciation.UserSpeciation;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository repository;
    private final UserMapper mapper;
    private final MessageSource messageSource;

    public UserService(UserRepository repository, UserMapper mapper, MessageSource messageSource) {
        this.repository = repository;
        this.mapper = mapper;
        this.messageSource = messageSource;
    }

    public Page<UserDTO> findAll(Pageable pageable, UserDTO filter) throws FuteLoveException {
        return repository.findAll(UserSpeciation.search(filter), pageable).map(mapper::toDTO);
    }

    public UserDTO createUser(UserDTO dto) {
        return mapper.toDTO(repository.save(mapper.toEntity(dto)));
    }

    public User findByEntityId(UUID id) throws FuteLoveException {
        ProblemType problemType = ProblemType.USER_NOT_EXISTS;
        Optional<User> obj = repository.findById(id);
        String messageDetails = messageSource.getMessage(problemType.getMessageSource(), new Object[]{""}, LocaleContextHolder.getLocale());
        return obj.orElseThrow(() -> new FuteLoveException(HttpStatus.BAD_REQUEST.value(),
                problemType.getTitle(), problemType.getUri(), messageDetails));
    }

    public UserDTO findById(UUID id) throws FuteLoveException {
        ProblemType problemType = ProblemType.USER_NOT_EXISTS;
        Optional<User> obj = repository.findById(id);
        String messageDetails = messageSource.getMessage(problemType.getMessageSource(), new Object[]{""}, LocaleContextHolder.getLocale());
        return mapper
                .toDTO(obj.orElseThrow(() -> new FuteLoveException(HttpStatus.BAD_REQUEST.value(),
                        problemType.getTitle(), problemType.getUri(), messageDetails)));
    }

    public User findEntityById(UUID id) throws FuteLoveException {
        ProblemType problemType = ProblemType.USER_NOT_EXISTS;
        Optional<User> obj = repository.findById(id);
        String messageDetails = messageSource.getMessage(problemType.getMessageSource(), new Object[]{""}, LocaleContextHolder.getLocale());
        return obj.orElseThrow(() -> new FuteLoveException(HttpStatus.BAD_REQUEST.value(),
                problemType.getTitle(), problemType.getUri(), messageDetails));
    }

    public UserDTO updateUser(UserDTO dto) {
        return mapper.toDTO(repository.save(mapper.toEntity(dto)));
    }

    public UserDTO updateUserEntity(User entity) {
        return mapper.toDTO(repository.save(mapper.toUpdate(entity)));
    }

    public User getUserFromLabel(int hasCode) {
        return repository.findByHashCode(hasCode);
    }
}
