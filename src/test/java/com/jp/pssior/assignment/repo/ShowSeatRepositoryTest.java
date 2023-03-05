package com.jp.pssior.assignment.repo;

import com.jp.pssior.assignment.model.entity.ShowSeat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ShowSeatRepositoryTest extends RepositoryTest {

    @Autowired
    private ShowSeatRepository showSeatRepository;

    @Test
    @Sql("/availableSeat.sql")
    void findByShowSetup(){
        List<ShowSeat> showSeats = showSeatRepository.findByShowSetup_showNo("1");
        assertEquals(9, showSeats.size());
    }

}
