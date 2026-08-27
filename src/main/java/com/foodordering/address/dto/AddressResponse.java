package com.foodordering.address.dto;

import lombok.*;

/**
 * Response payload returning user delivery address details.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressResponse {

    private Long id;
    private String houseNumber;
    private String street;
    private String city;
    private String state;
    private String pincode;
    private String type;
    private Long userId;
}
