package dev.cybo.tickets.commands;

import com.freya02.botcommands.api.application.ApplicationCommand;
import com.freya02.botcommands.api.application.slash.GuildSlashEvent;
import com.freya02.botcommands.api.application.slash.annotations.JDASlashCommand;
import com.freya02.botcommands.api.components.Components;
import dev.cybo.tickets.DevRoomTickets;
import dev.cybo.tickets.objects.Ticket;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class TicketOptionsCommand extends ApplicationCommand {

    private final DevRoomTickets ticketBot;

    public TicketOptionsCommand(DevRoomTickets ticketBot) {
        this.ticketBot = ticketBot;
    }

    @JDASlashCommand(
            name = "ticketoptions",
            description = "Displays a embed with all the available ticket options"
    )
    public void onSlashTicketOptions(GuildSlashEvent event) {

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Tickets");
        embedBuilder.setColor(Color.BLUE);
        embedBuilder.setDescription("What do you need help with?");

        var replyCallbackAction = event.replyEmbeds(embedBuilder.build());

        ticketBot.getConfig().getArray("ticketOptions").forEach(option -> {
            String finalOption = option.toString();
            replyCallbackAction.addActionRow(Components.primaryButton(e -> {
                e.getMessage().delete().queue();
                event.getGuild().createTextChannel("ticket-" + event.getUser().getName()).queue(textChannel -> {

                    textChannel.sendMessage("Hello, " + event.getUser().getAsMention() +
                            "!\nSpecify your problem, please!\nTo close this ticket, use **/close**.\nCategory: **" + option + "**").queue();

                    Ticket ticket = new Ticket(textChannel.getId(), event.getUser().getId());

                    ticketBot.getStorage().getAwaitingResponse().add(textChannel.getId());
                    ticketBot.getStorage().getTicketList().put(textChannel.getId(), ticket);

                    new Timer().schedule(
                            new TimerTask() {
                                @Override
                                public void run() {
                                    if (ticketBot.getStorage().getAwaitingResponse().contains(textChannel.getId())) {
                                        textChannel.delete().queue();
                                    }
                                }
                            }, 180000);

                    ticketBot.getMongoDB().storeTicket(ticket);

                });
            }).build(finalOption));
        });

        replyCallbackAction.queue();
    }

}
