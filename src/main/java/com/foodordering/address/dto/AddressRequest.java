package com.foodordering.address.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Request payload for creating or updating delivery address details.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressRequest {

    private String houseNumber;

    @NotBlank(message = "Street is required")
    private String street;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Pincode is required")
    private String pincode;

    private String type; // e.g. HOME, WORK, OTHER
}
