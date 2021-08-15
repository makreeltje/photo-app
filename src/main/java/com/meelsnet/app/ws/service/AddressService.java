package com.meelsnet.app.ws.service;

import com.meelsnet.app.ws.shared.dto.AddressDto;

import java.util.List;

public interface AddressService {
    List<AddressDto> getAddressList(String userId);
    AddressDto getAddress(String addressId);
}
