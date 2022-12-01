package dev.cybo.tickets.events;

import dev.cybo.tickets.DevRoomTickets;
import dev.cybo.tickets.objects.Ticket;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class Events extends ListenerAdapter {

    private final DevRoomTickets ticketBot;

    public Events(DevRoomTickets ticketBot) {
        this.ticketBot = ticketBot;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        super.onMessageReceived(event);

        if (event.getChannel().getType() == ChannelType.TEXT) {
            for (String ticketId : ticketBot.getStorage().getAwaitingResponse()) {
                if (event.getChannel().getId().equals(ticketId)) {
                    Ticket ticket = ticketBot.getStorage().getTicketList().get(ticketId);
                    if (ticket.getAuthorId().equals(event.getAuthor().getId())) {
                        ticketBot.getStorage().getAwaitingResponse().remove(ticketId);
                        event.getChannel().sendMessage("Thanks for the input! The team is gonna assist you soon.").queue();

                        Role role = event.getGuild().getRoleById(ticketBot.getConfig().getString("roleToPing"));
                        if (role != null) {
                            event.getChannel().sendMessage(role.getAsMention()).queue();
                        } else {
                            System.out.println("The staff role to mention does not exist!");
                        }
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void onChannelDelete(@NotNull ChannelDeleteEvent event) {
        super.onChannelDelete(event);

        if (event.getChannel().getType() == ChannelType.TEXT) {
            if (ticketBot.getStorage().getTicketList().containsKey(event.getChannel().getId())) {
                Ticket ticket = ticketBot.getStorage().getTicketList().get(event.getChannel().getId());
                ticketBot.getStorage().getTicketList().remove(event.getChannel().getId());
                ticketBot.getMongoDB().deleteTicket(ticket);
            }
        }
    }
}
