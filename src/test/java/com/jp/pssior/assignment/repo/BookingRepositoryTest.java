package com.jp.pssior.assignment.repo;

import com.jp.pssior.assignment.model.entity.Booking;
import com.jp.pssior.assignment.model.entity.ShowSetup;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class BookingRepositoryTest extends RepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void findByPhoneNoAndShowNo() {
        ShowSetup showSetup = ShowSetup.builder()
                .rowPerSeat(1)
                .rowPerShow(2)
                .version(1)
                .cancelWindowMinutes(2)
                .showNo("2")
                .build();
        Booking booking = Booking.builder()
                .bookedNo(UUID.randomUUID())
                .phoneNo("100")
                .showSetup(showSetup)
                .build();

        testEntityManager.persist(showSetup);
        testEntityManager.persistAndFlush(booking);
        testEntityManager.clear();
        List<Booking> bookings = bookingRepository.findByPhoneNoAndShowNo("100", "2");
        assertFalse(CollectionUtils.isEmpty(bookings));
    }
}
