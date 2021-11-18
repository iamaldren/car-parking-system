package com.aldren.event.repository;

import com.aldren.event.entity.EnterEvent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnterEventRepository extends CrudRepository<EnterEvent, String> {

    EnterEvent findByPlateNumber(String plateNumber);

}
