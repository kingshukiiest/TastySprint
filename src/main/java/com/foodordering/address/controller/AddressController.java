package com.foodordering.address.controller;

import com.foodordering.address.dto.AddressRequest;
import com.foodordering.address.dto.AddressResponse;
import com.foodordering.address.service.AddressService;
import com.foodordering.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller exposing endpoints for creating, fetching, updating, and deleting user addresses.
 */
@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    public ResponseEntity<ApiResponse<AddressResponse>> createAddress(
            Authentication authentication,
            @Valid @RequestBody AddressRequest request) {
        String userEmail = authentication.getName();
        AddressResponse response = addressService.createAddress(request, userEmail);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Address added successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getUserAddresses(Authentication authentication) {
        String userEmail = authentication.getName();
        List<AddressResponse> addresses = addressService.getUserAddresses(userEmail);
        return ResponseEntity.ok(ApiResponse.success("Addresses retrieved successfully", addresses));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(
            @PathVariable Long id,
            Authentication authentication,
            @Valid @RequestBody AddressRequest request) {
        String userEmail = authentication.getName();
        AddressResponse response = addressService.updateAddress(id, request, userEmail);
        return ResponseEntity.ok(ApiResponse.success("Address updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @PathVariable Long id,
            Authentication authentication) {
        String userEmail = authentication.getName();
        addressService.deleteAddress(id, userEmail);
        return ResponseEntity.ok(ApiResponse.success("Address deleted successfully", null));
    }
}
