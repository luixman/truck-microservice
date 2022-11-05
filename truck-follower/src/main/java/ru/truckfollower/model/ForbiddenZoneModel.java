package ru.truckfollower.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.postgis.Geometry;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForbiddenZoneModel {
    Long id;
    String zoneName;
    Long companyId;
    Geometry geometry;
}
