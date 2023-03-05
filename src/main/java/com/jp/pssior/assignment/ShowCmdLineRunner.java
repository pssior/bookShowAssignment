package com.jp.pssior.assignment;

import com.jp.pssior.assignment.constant.ExecuteAction;
import com.jp.pssior.assignment.exception.ShowException;
import com.jp.pssior.assignment.model.dto.BookShow;
import com.jp.pssior.assignment.model.entity.ShowSetup;
import com.jp.pssior.assignment.service.AdminSetupShowService;
import com.jp.pssior.assignment.service.BuyerTicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShowCmdLineRunner implements CommandLineRunner {

    private final AdminSetupShowService adminSetupShowService;
    private final BuyerTicketService buyerTicketService;

    @Override
    public void run(String... args) throws Exception {

        List<String> commandLines = Arrays.asList(args);
        if (!CollectionUtils.isEmpty(commandLines)) {
            for (String cmd : commandLines) {

                String[] actions = cmd.split(" ");
                log.info("Execute Action: {}", actions[0]);
                try {
                    switch (ExecuteAction.valueOfAction(actions[0])) {
                        case SETUP:
                            ShowSetup showSetup = new ShowSetup(actions[1], actions[2], actions[3], actions[4]);
                            adminSetupShowService.setupShow(showSetup);
                            break;
                        case VIEW:
                            adminSetupShowService.viewShow(actions[1]);
                            break;
                        case AVAILABILITY:
                            buyerTicketService.getAvailableSeatByShowNo(actions[1]);
                            break;
                        case BOOK:
                            BookShow bookShow = new BookShow();
                            bookShow.setPhoneNo(actions[2]);
                            bookShow.setShowNo(actions[1]);
                            bookShow.setSeats(new HashSet<String>(Arrays.asList(actions[3].split(","))));
                            buyerTicketService.reserveShow(bookShow);
                            break;
                        case CANCEL:
                            buyerTicketService.cancelTicket(actions[2], actions[1]);
                            break;
                    }
                } catch (ShowException ex) {
                    log.error("Error : {} ", ex.getMessage());
                }

            }
        }
    }
}
