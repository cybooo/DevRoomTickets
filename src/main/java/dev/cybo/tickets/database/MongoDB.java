package dev.cybo.tickets.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import dev.cybo.tickets.DevRoomTickets;
import dev.cybo.tickets.objects.Ticket;
import org.bson.Document;

public class MongoDB {

    private final DevRoomTickets ticketBot;
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;

    public MongoDB(DevRoomTickets ticketBot) {
        this.ticketBot = ticketBot;
    }

    public MongoDB initialize(String hostname, String username, String database, String password) {
        ConnectionString connectionString = new ConnectionString("mongodb+srv://" + username + ":" + password + "@" + hostname + "/?retryWrites=true&w=majority");

        MongoClientSettings.Builder mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString);

        mongoClient = MongoClients.create(mongoClientSettings.build());
        mongoDatabase = mongoClient.getDatabase(database);

        mongoDatabase.createCollection("tickets");
        
        mongoDatabase.getCollection("tickets")
                .insertOne(new Document("ticket_id", "0").append("ticket_author", "0"));

        return this;
    }

    public void loadTickets() {
        mongoDatabase.getCollection("tickets").find().forEach((Document document) -> {
            ticketBot.getStorage().getTicketList().put(document.getString("ticket_id"), new Ticket(
                    document.getString("ticket_id"),
                    document.getString("ticket_author")
            ));
        });
    }

    public void storeTicket(Ticket ticket) {
        mongoDatabase.getCollection("tickets")
                .insertOne(new Document("ticket_id", ticket.getChannelId())
                        .append("ticket_author", ticket.getAuthorId()));
    }

    public void deleteTicket(Ticket ticket) {
        mongoDatabase.getCollection("tickets")
                .deleteOne(new Document("ticket_id", ticket.getChannelId())
                        .append("ticket_author", ticket.getAuthorId()));
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    public MongoDatabase getMongoDatabase() {
        return mongoDatabase;
    }
}
