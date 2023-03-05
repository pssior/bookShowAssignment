package com.jp.pssior.assignment;

import com.jp.pssior.assignment.model.dto.BookShow;
import com.jp.pssior.assignment.model.entity.ShowSetup;
import com.jp.pssior.assignment.service.AdminSetupShowService;
import com.jp.pssior.assignment.service.BuyerTicketService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShowCmdLineRunnerTest {

    @Mock
    private AdminSetupShowService adminSetupShowService;
    @Mock
    private BuyerTicketService buyerTicketService;
    private ShowCmdLineRunner showCmdLineRunner;
    @Captor
    private ArgumentCaptor<ShowSetup> showSetupArgumentCaptor;
    @Captor
    private ArgumentCaptor<BookShow> bookShowArgumentCaptor;

    @BeforeEach
    void setup() {
        showCmdLineRunner = new ShowCmdLineRunner(adminSetupShowService, buyerTicketService);
    }

    @Test
    @SneakyThrows
    void setupShowCmd() {
        showCmdLineRunner.run("setup S1 20 10 2");
        verify(adminSetupShowService, times(1)).setupShow(showSetupArgumentCaptor.capture());
        ShowSetup showSetup = showSetupArgumentCaptor.getValue();
        assertEquals("S1", showSetup.getShowNo());
    }

    @Test
    @SneakyThrows
    void viewShowCmd() {
        showCmdLineRunner.run("view S1");
        verify(adminSetupShowService, times(1)).viewShow(eq("S1"));
    }

    @Test
    @SneakyThrows
    void availabilityCmd() {
        showCmdLineRunner.run("availability S1");
        verify(buyerTicketService, times(1)).getAvailableSeatByShowNo(eq("S1"));
    }

    @Test
    @SneakyThrows
    void bookCmd() {
        showCmdLineRunner.run("Book S1 999999 A1,A2,A3");
        verify(buyerTicketService, times(1)).reserveShow(bookShowArgumentCaptor.capture());
        BookShow bookShow = bookShowArgumentCaptor.getValue();
        assertEquals("S1", bookShow.getShowNo());
        assertEquals("999999", bookShow.getPhoneNo());
        assertEquals(3, bookShow.getSeats().size());
    }

    @Test
    @SneakyThrows
    void cancelCmd() {
        showCmdLineRunner.run("CANCEL S120230305A1 999999");
        verify(buyerTicketService, times(1)).cancelTicket(eq("999999"), eq("S120230305A1"));
    }
}
