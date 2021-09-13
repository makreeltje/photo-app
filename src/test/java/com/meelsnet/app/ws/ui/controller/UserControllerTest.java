package com.meelsnet.app.ws.ui.controller;

import com.meelsnet.app.ws.service.impl.UserServiceImpl;
import com.meelsnet.app.ws.shared.dto.AddressDto;
import com.meelsnet.app.ws.shared.dto.UserDto;
import com.meelsnet.app.ws.ui.model.response.UserRest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class UserControllerTest {

    @InjectMocks
    UserController userController;

    @Mock
    UserServiceImpl userService;

    UserDto userDto;

    final String USER_ID = "23oijf23tds";

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        userDto = new UserDto();
        userDto.setFirstName("Jan");
        userDto.setLastName("Bas");
        userDto.setEmail("test@test.com");
        userDto.setEmailVerificationStatus(false);
        userDto.setEmailVerificationToken(null);
        userDto.setUserId(USER_ID);
        userDto.setAddresses(getAddressDtoList());
        userDto.setEncryptedPassword("23io4hfsdf");
    }

    @Test
    void getUser() {
        when(userService.getUserByUserId(anyString())).thenReturn(userDto);

        UserRest userRest = userController.getUser(USER_ID);

        assertNotNull(userRest);
        assertEquals(USER_ID, userRest.getUserId());
        assertEquals(userDto.getFirstName(), userRest.getFirstName());
        assertEquals(userDto.getLastName(), userRest.getLastName());
        assertTrue(userDto.getAddresses().size() == userRest.getAddresses().size());
    }

    private List<AddressDto> getAddressDtoList() {
        AddressDto shippingAddressDto = new AddressDto();
        shippingAddressDto.setType("shipping");
        shippingAddressDto.setCity("Vancouver");
        shippingAddressDto.setCountry("Canada");
        shippingAddressDto.setPostalCode("ABC123");
        shippingAddressDto.setStreetName("Street name");
        shippingAddressDto.setHouseNumber(123);

        AddressDto billingAddressDto = new AddressDto();
        billingAddressDto.setType("billing");
        billingAddressDto.setCity("Vancouver");
        billingAddressDto.setCountry("Canada");
        billingAddressDto.setPostalCode("ABC123");
        billingAddressDto.setStreetName("Street name");
        billingAddressDto.setHouseNumber(123);

        List<AddressDto> addressDtoList = new ArrayList<>();
        addressDtoList.add(billingAddressDto);
        addressDtoList.add(shippingAddressDto);

        return addressDtoList;
    }
}