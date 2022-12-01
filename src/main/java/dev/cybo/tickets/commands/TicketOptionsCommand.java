package dev.cybo.tickets.commands;

import com.freya02.botcommands.api.application.ApplicationCommand;
import com.freya02.botcommands.api.application.slash.GuildSlashEvent;
import com.freya02.botcommands.api.application.slash.annotations.JDASlashCommand;
import com.freya02.botcommands.api.components.Components;
import dev.cybo.tickets.DevRoomTickets;
import dev.cybo.tickets.objects.Ticket;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
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

        for (Ticket ticket : ticketBot.getStorage().getTicketList().values()) {
            if (ticket.getAuthorId().equals(event.getUser().getId())) {
                event.reply("You already have a ticket open!").setEphemeral(true).queue();
                return;
            }
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Tickets");
        embedBuilder.setColor(Color.BLUE);
        embedBuilder.setDescription("What do you need help with?");

        var replyCallbackAction = event.replyEmbeds(embedBuilder.build()).setEphemeral(true);

        List<Button> buttonList = new ArrayList<>();
        for (Object option : ticketBot.getConfig().getArray("ticketOptions")) {
            String finalOption = option.toString();
            buttonList.add(Components.primaryButton(e -> {
                event.getHook().deleteOriginal().queue();
                e.getHook().deleteOriginal().queue();
                for (Ticket ticket : ticketBot.getStorage().getTicketList().values()) {
                    if (ticket.getAuthorId().equals(event.getUser().getId())) {
                        e.reply("You already have a ticket open!").setEphemeral(true).queue();
                        return;
                    }
                }

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
                                        ticketBot.getStorage().getAwaitingResponse().remove(textChannel.getId());
                                        if (event.getGuild().getTextChannelById(textChannel.getId()) != null) {
                                            textChannel.delete().queue();
                                        }
                                    }
                                }
                            }, 180000);

                    ticketBot.getMongoDB().storeTicket(ticket);

                });
            }).build(finalOption));
        }

        for (int i = 0; i < buttonList.size(); i += 5) {
            replyCallbackAction.addActionRow(buttonList.subList(i, Math.min(i + 5, buttonList.size())));
        }

        replyCallbackAction.queue();
    }

}
