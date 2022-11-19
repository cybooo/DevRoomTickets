package dev.cybo.tickets;

import com.freya02.botcommands.api.CommandsBuilder;
import com.freya02.botcommands.api.components.DefaultComponentManager;
import dev.cybo.tickets.database.MongoDB;
import dev.cybo.tickets.database.PostgreSQL;
import dev.cybo.tickets.events.Events;
import dev.cybo.tickets.storage.Storage;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.utils.data.DataObject;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DevRoomTickets {

    private final DataObject config;
    private final JDA jda;
    private final PostgreSQL postgreSQL;
    private final MongoDB mongoDB;
    private final Storage storage;

    public DevRoomTickets() throws IOException, InterruptedException {

        config = loadConfig();

        JDABuilder builder = JDABuilder.createDefault(config.getString("token"));
        builder.setBulkDeleteSplittingEnabled(false);
        builder.addEventListeners(new Events(this));
        jda = builder.build().awaitReady();

        // PostgreSQL is required by the BotCommands library as an interaction db
        postgreSQL = new PostgreSQL(this).initialize(
                config.getString("postgresql-hostname"),
                config.getInt("postgresql-port"),
                config.getString("postgresql-username"),
                config.getString("postgresql-database"),
                config.getString("postgresql-password")
        );
        mongoDB = new MongoDB(this).initialize(
                config.getString("mongodb-hostname"),
                config.getInt("mongodb-port"),
                config.getString("mongodb-username"),
                config.getString("mongodb-database"),
                config.getString("mongodb-password")
        );
        storage = new Storage(this);

        CommandsBuilder.newBuilder(485434705903222805L)
                .setComponentManager(new DefaultComponentManager(postgreSQL::getConnection))
                .extensionsBuilder(extensionsBuilder ->
                        extensionsBuilder.registerConstructorParameter(DevRoomTickets.class, ignored -> this)).
                build(jda,
                        "dev.cybo.tickets.commands"
                );

    }

    private DataObject loadConfig() {
        File configFile = new File("config.json");
        if (!configFile.exists()) {
            String jarLoc;
            try {
                jarLoc = new File(DevRoomTickets.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                        .getPath();
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
            InputStream inputStream = DevRoomTickets.class.getResourceAsStream(jarLoc + "config.json");
            if (inputStream == null) {
                return DataObject.empty();
            }
            Path path = Paths.get("config.json");
            try {
                Files.copy(inputStream, path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            return DataObject.fromJson(new FileInputStream(configFile));
        } catch (FileNotFoundException ignored) {
        }
        return DataObject.empty();
    }

    public static void main(String[] args) {
        try {
            new DevRoomTickets();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public DataObject getConfig() {
        return config;
    }

    public JDA getJDA() {
        return jda;
    }

    public PostgreSQL getPostgreSQL() {
        return postgreSQL;
    }

    public MongoDB getMongoDB() {
        return mongoDB;
    }

    public Storage getStorage() {
        return storage;
    }
}