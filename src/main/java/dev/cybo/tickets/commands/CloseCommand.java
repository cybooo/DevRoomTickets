package dev.cybo.tickets.commands;

import com.freya02.botcommands.api.application.ApplicationCommand;
import com.freya02.botcommands.api.application.slash.GuildSlashEvent;
import com.freya02.botcommands.api.application.slash.annotations.JDASlashCommand;
import dev.cybo.tickets.DevRoomTickets;
import dev.cybo.tickets.objects.Ticket;

public class CloseCommand extends ApplicationCommand {

    private final DevRoomTickets ticketBot;

    public CloseCommand(DevRoomTickets ticketBot) {
        this.ticketBot = ticketBot;
    }

    @JDASlashCommand(
            name = "close",
            description = "Closes a ticket."
    )
    public void onSlashClose(GuildSlashEvent event) {
        if (ticketBot.getStorage().getTicketList().containsKey(event.getChannel().getId())) {
            Ticket ticket = ticketBot.getStorage().getTicketList().get(event.getChannel().getId());
            ticketBot.getStorage().getTicketList().remove(event.getChannel().getId());
            ticketBot.getStorage().getAwaitingResponse().remove(event.getChannel().getId());
            ticketBot.getMongoDB().deleteTicket(ticket);
            event.getChannel().delete().queue();
        } else {
            event.reply("This channel is not a ticket.").setEphemeral(true).queue();
        }
    }

}
