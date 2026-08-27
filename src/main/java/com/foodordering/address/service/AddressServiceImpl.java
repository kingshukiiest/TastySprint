package com.foodordering.address.service;

import com.foodordering.address.dto.AddressRequest;
import com.foodordering.address.dto.AddressResponse;
import com.foodordering.address.entity.Address;
import com.foodordering.address.repository.AddressRepository;
import com.foodordering.exception.ResourceNotFoundException;
import com.foodordering.exception.UnauthorizedException;
import com.foodordering.user.entity.User;
import com.foodordering.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation managing user address operations with user ownership verification.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserService userService;

    @Override
    public AddressResponse createAddress(AddressRequest request, String userEmail) {
        User user = userService.findEntityByEmail(userEmail);

        Address address = Address.builder()
                .user(user)
                .houseNumber(request.getHouseNumber())
                .street(request.getStreet())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .type(request.getType() != null ? request.getType() : "HOME")
                .build();

        Address savedAddress = addressRepository.save(address);
        return mapToResponse(savedAddress);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> getUserAddresses(String userEmail) {
        User user = userService.findEntityByEmail(userEmail);
        return addressRepository.findByUserId(user.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AddressResponse updateAddress(Long id, AddressRequest request, String userEmail) {
        Address address = findEntityById(id);
        User user = userService.findEntityByEmail(userEmail);

        if (!address.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not authorized to update this address");
        }

        address.setHouseNumber(request.getHouseNumber());
        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPincode(request.getPincode());
        if (request.getType() != null) {
            address.setType(request.getType());
        }

        Address updated = addressRepository.save(address);
        return mapToResponse(updated);
    }

    @Override
    public void deleteAddress(Long id, String userEmail) {
        Address address = findEntityById(id);
        User user = userService.findEntityByEmail(userEmail);

        if (!address.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not authorized to delete this address");
        }

        addressRepository.delete(address);
    }

    @Override
    @Transactional(readOnly = true)
    public Address findEntityById(Long id) {
        return addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + id));
    }

    private AddressResponse mapToResponse(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .houseNumber(address.getHouseNumber())
                .street(address.getStreet())
                .city(address.getCity())
                .state(address.getState())
                .pincode(address.getPincode())
                .type(address.getType())
                .userId(address.getUser().getId())
                .build();
    }
}
