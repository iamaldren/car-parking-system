package com.aldren.event.repository;

import com.aldren.event.entity.ExitEvent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExitEventRepository extends CrudRepository<ExitEvent, String> {

    ExitEvent findByPlateNumber(String plateNumber);

}
