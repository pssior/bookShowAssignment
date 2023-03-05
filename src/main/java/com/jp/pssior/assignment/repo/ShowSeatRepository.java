package com.jp.pssior.assignment.repo;

import com.jp.pssior.assignment.model.entity.ShowSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShowSeatRepository extends JpaRepository<ShowSeat, Integer> {

    @Query(nativeQuery = true, value = "select seat.* from show_seat seat " +
            " left join booking bk on seat.show_no = bk.show_no " +
            " left join ticket tk on seat.seat_id = tk.seat_id and tk.cancelled_dt is null " +
            " where tk.id is null and seat.show_no=:show_no")
    List<ShowSeat> findByShowSetup_showNo(@Param("show_no") String showNo);
}
