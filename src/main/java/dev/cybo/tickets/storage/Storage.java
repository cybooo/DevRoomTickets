package dev.cybo.tickets.storage;

import dev.cybo.tickets.DevRoomTickets;
import dev.cybo.tickets.objects.Ticket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Storage {

    private final DevRoomTickets ticketBot;
    private final Map<String, Ticket> ticketList;
    private final List<String> awaitingResponse;

    public Storage(DevRoomTickets ticketBot) {
        this.ticketBot = ticketBot;
        this.ticketList = new HashMap<>();
        this.awaitingResponse = new ArrayList<>();
    }

    public Map<String, Ticket> getTicketList() {
        return ticketList;
    }

    public List<String> getAwaitingResponse() {
        return awaitingResponse;
    }
}
