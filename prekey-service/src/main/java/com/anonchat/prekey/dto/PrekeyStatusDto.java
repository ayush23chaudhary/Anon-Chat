package com.anonchat.prekey.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for prekey availability status.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrekeyStatusDto {
    private String userId;
    private Long availablePrekeyCount;
    private Long availableOneTimePrekeyCount;
}
