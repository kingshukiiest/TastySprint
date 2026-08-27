package com.foodordering.address.service;

import com.foodordering.address.dto.AddressRequest;
import com.foodordering.address.dto.AddressResponse;
import com.foodordering.address.entity.Address;

import java.util.List;

/**
 * Service interface for managing user delivery addresses.
 */
public interface AddressService {

    AddressResponse createAddress(AddressRequest request, String userEmail);

    List<AddressResponse> getUserAddresses(String userEmail);

    AddressResponse updateAddress(Long id, AddressRequest request, String userEmail);

    void deleteAddress(Long id, String userEmail);

    Address findEntityById(Long id);
}
