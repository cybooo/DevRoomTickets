package dev.cybo.tickets.objects;

public class Ticket {

    private final String channelId;
    private final String authorId;

    public Ticket(String channelId, String authorId) {
        this.channelId = channelId;
        this.authorId = authorId;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getAuthorId() {
        return authorId;
    }
}
