package com.jp.pssior.assignment.repo;

import com.jp.pssior.assignment.model.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {

    @Query("from Booking bk where phoneNo=:phone_no and bk.showSetup.showNo=:show_no")
    List<Booking> findByPhoneNoAndShowNo(@Param("phone_no") String phoneNo,
                                         @Param("show_no") String showNo);
}
