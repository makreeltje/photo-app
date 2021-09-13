package com.meelsnet.app.ws.ui.controller;

import com.meelsnet.app.ws.exceptions.UserServiceException;
import com.meelsnet.app.ws.service.AddressService;
import com.meelsnet.app.ws.service.UserService;
import com.meelsnet.app.ws.shared.dto.AddressDto;
import com.meelsnet.app.ws.shared.dto.UserDto;
import com.meelsnet.app.ws.ui.model.request.PasswordResetModel;
import com.meelsnet.app.ws.ui.model.request.PasswordResetRequestModel;
import com.meelsnet.app.ws.ui.model.request.UserDetailsRequestModel;
import com.meelsnet.app.ws.ui.model.response.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("api/v1/users")
public class UserController {

    final UserService userService;
    final AddressService addressService;

    @Autowired
    public UserController(UserService userService, AddressService addressService) {
        this.userService = userService;
        this.addressService = addressService;
    }

    @GetMapping(path = "/{id}")
    public UserRest getUser(@PathVariable String id) {
        UserRest returnValue = new UserRest();

        UserDto userDto = userService.getUserByUserId(id);
        BeanUtils.copyProperties(userDto, returnValue);

        return returnValue;
    }

    @PostMapping
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception {

        if (userDetails.getFirstName().isEmpty())
            throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());

        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = modelMapper.map(userDetails, UserDto.class);

        UserDto createdUser = userService.createUser(userDto);
        UserRest returnValue = modelMapper.map(createdUser, UserRest.class);

        return returnValue;
    }

    @PutMapping(path = "/{id}")
    public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails) {
        UserRest returnValue = new UserRest();

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetails, userDto);

        UserDto updatedUser = userService.updateUser(id, userDto);
        BeanUtils.copyProperties(updatedUser, returnValue);

        return returnValue;
    }

    @DeleteMapping(path = "/{id}")
    public OperationStatusModel deleteUser(@PathVariable String id) {
        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(RequestOperationName.DELETE.name());

        userService.deleteUser(id);

        returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());

        return returnValue;
    }

    @GetMapping()
    public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "1") int page,
                                   @RequestParam(value = "limit", defaultValue = "25") int limit) {
        List<UserRest> returnValue = new ArrayList<>();

        List<UserDto> users = userService.getUsers(page, limit);

        for (UserDto userDto : users) {
            UserRest userModel = new UserRest();
            BeanUtils.copyProperties(userDto, userModel);
            returnValue.add(userModel);
        }

        return returnValue;
    }

    @GetMapping(path = "/{id}/addresses")
    public CollectionModel<AddressRest> getUserAddresses(@PathVariable String id) {
        List<AddressRest> returnValue = new ArrayList<>();

        List<AddressDto> addressDtoList = addressService.getAddressList(id);

        if (addressDtoList != null && !addressDtoList.isEmpty()) {
            Type listType = new TypeToken<List<AddressRest>>() {}.getType();
            returnValue = new ModelMapper().map(addressDtoList, listType);

            for (AddressRest addressRest :
                    returnValue) {
                // http://localhost:8080/mobile-app-ws/users/{userId}/addresses/{addressId}
                Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddress(id, addressRest.getAddressId())).withSelfRel();
                addressRest.add(selfLink);
            }
        }

        // http://localhost:8080/mobile-app-ws/users/{userId}
        Link userLink = WebMvcLinkBuilder.linkTo(UserController.class).slash(id).withRel("user");
        // http://localhost:8080/mobile-app-ws/users/{userId}/addresses
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddresses(id)).withSelfRel();

        return CollectionModel.of(returnValue, userLink, selfLink);
    }

    @GetMapping(path = "/{userId}/addresses/{addressId}")
    public EntityModel<AddressRest> getUserAddress(@PathVariable String userId, @PathVariable String addressId) {
        AddressDto addressDto = addressService.getAddress(addressId);

        ModelMapper modelMapper = new ModelMapper();
        AddressRest returnValue = modelMapper.map(addressDto, AddressRest.class);

        // http://localhost:8080/mobile-app-ws/users/{userId}
        Link userLink = WebMvcLinkBuilder.linkTo(UserController.class).slash(userId).withRel("user");
        // http://localhost:8080/mobile-app-ws/users/{userId}/addresses
        Link userAddressesLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddresses(userId)).withRel("addresses");
        // http://localhost:8080/mobile-app-ws/users/{userId}/addresses/{addressId}
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddress(userId, addressId)).withSelfRel();

//        returnValue.add(userLink);
//        returnValue.add(userAddressesLink);
//        returnValue.add(selfLink);

        return EntityModel.of(returnValue, Arrays.asList(userLink, userAddressesLink, selfLink));
    }

    @GetMapping(path = "/email-verification")
    public OperationStatusModel verifyEmailToken(@RequestParam(value = "token") String token) {
        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(RequestOperationName.VERIFY_EMAIL.name());

        boolean isVerified = userService.verifyEmailToken(token);

        returnValue.setOperationResult((isVerified ? RequestOperationStatus.SUCCESS : RequestOperationStatus.ERROR).name());

        return returnValue;
    }

    @PostMapping(path = "/password-reset-request")
    public OperationStatusModel requestReset(@RequestBody PasswordResetRequestModel passwordResetRequestModel) {
        OperationStatusModel returnValue = new OperationStatusModel();

        boolean operationResult = userService.requestPasswordReset(passwordResetRequestModel.getEmail());

        returnValue.setOperationName(RequestOperationName.REQUEST_PASSWORD_RESET.name());
        returnValue.setOperationResult(RequestOperationStatus.ERROR.name());

        if (operationResult) returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());

        return returnValue;
    }

    @PostMapping(path = "/password-reset")
    public OperationStatusModel resetPassword(@RequestBody PasswordResetModel passwordResetModel) {
        OperationStatusModel returnValue = new OperationStatusModel();

        boolean operationResult = userService.resetPassword(
                passwordResetModel.getToken(),
                passwordResetModel.getPassword()
        );

        returnValue.setOperationName(RequestOperationName.PASSWORD_RESET.name());
        returnValue.setOperationResult(RequestOperationStatus.ERROR.name());

        if(operationResult) {
            returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        }

        return returnValue;
    }
}
