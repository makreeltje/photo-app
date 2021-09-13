package com.meelsnet.app.ws.service.impl;

import com.meelsnet.app.ws.exceptions.UserServiceException;
import com.meelsnet.app.ws.io.entity.AddressEntity;
import com.meelsnet.app.ws.io.entity.UserEntity;
import com.meelsnet.app.ws.io.repositories.UserRepository;
import com.meelsnet.app.ws.shared.AmazonSES;
import com.meelsnet.app.ws.shared.Utils;
import com.meelsnet.app.ws.shared.dto.AddressDto;
import com.meelsnet.app.ws.shared.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Mock
    Utils utils;

    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    AmazonSES amazonSES;

    String userId = "eg32fxq2r";
    String encryptedPassword = "f2324FE3212ffw";
    UserEntity userEntity;
    UserDto userDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setFirstName("Jan");
        userEntity.setLastName("Bas");
        userEntity.setUserId(userId);
        userEntity.setEncryptedPassword(encryptedPassword);
        userEntity.setEmail("test@test.com");
        userEntity.setEmailVerificationToken("f869342rmlnfi43u");
        userEntity.setAddresses(getAddressEntityList());

        userDto = new UserDto();
        userDto.setAddresses(getAddressDtoList());
        userDto.setFirstName("Jan");
        userDto.setLastName("Bas");
        userDto.setPassword("123456789");
        userDto.setEmail("test@test.com");
    }

    @Test
    void getUser() {
        when(userRepository.findByEmail(anyString())).thenReturn(userEntity);

        UserDto storedUserDetails = userService.getUser("test@test.com");

        assertNotNull(storedUserDetails);
        assertEquals(userEntity.getFirstName(), storedUserDetails.getFirstName());
    }

    @Test
    void getUser_UsernameNotFoundException() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);

        assertThrows(UsernameNotFoundException.class,
                () -> {
                    userService.getUser("test@test.com");
                }
        );
    }

    @Test
    void createUser() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        when(utils.generateAddressId(anyInt())).thenReturn("f43reh56qn");
        when(utils.generateUserId(anyInt())).thenReturn(userId);
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn(encryptedPassword);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        doNothing().when(amazonSES).verifyEmail(any(UserDto.class));

        UserDto storedUserDetails = userService.createUser(userDto);

        assertNotNull(storedUserDetails);
        assertEquals(userEntity.getFirstName(), storedUserDetails.getFirstName());
        assertEquals(userEntity.getLastName(), storedUserDetails.getLastName());
        assertEquals(userEntity.getEmail(), storedUserDetails.getEmail());
        assertNotNull(storedUserDetails.getUserId());
        assertEquals(storedUserDetails.getAddresses().size(), userEntity.getAddresses().size());
        verify(utils, times(storedUserDetails.getAddresses().size())).generateAddressId(30);
        verify(bCryptPasswordEncoder, times(1)).encode("123456789");
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void createUser_CreateUserServiceException() {
        when(userRepository.findByEmail(anyString())).thenReturn(userEntity);

        assertThrows(UserServiceException.class,
                () -> {
                    userService.createUser(userDto);
                }
        );
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

    private List<AddressEntity> getAddressEntityList() {
        List<AddressDto> addressDtoList = getAddressDtoList();

        Type listType = new TypeToken<List<AddressEntity>>() {
        }.getType();
        return new ModelMapper().map(addressDtoList, listType);
    }
}