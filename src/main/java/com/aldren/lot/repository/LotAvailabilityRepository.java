package com.aldren.lot.repository;

import com.aldren.lot.entity.LotAvailability;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LotAvailabilityRepository extends CrudRepository<LotAvailability, String> {

    LotAvailability findByVehicleType(String vehicleType);

}
