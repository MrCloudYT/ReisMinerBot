package de.mrcloud.listeners;

import de.mrcloud.SQL.DataBaseTest;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class FileListener extends ListenerAdapter {

    public long ReisMinerChannelId = 647754397409083412L;
    public TextChannel console;
    public TextChannel configDownload;



    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent e) {
        if (e.getChannel().getName().equalsIgnoreCase("public-configs")) {



            if (!e.getAuthor().isBot()) {

                super.onGuildMessageReceived(e);
                Message message = e.getMessage();
                String rawMsg = message.getContentRaw();
                TextChannel TxtChannel = e.getChannel();
                Guild server = e.getGuild();
                User user = e.getAuthor();

                if (!e.getMessage().getAttachments().isEmpty()) {
                    if (!e.getMessage().getAttachments().get(0).getFileName().split("\\.")[1].equals("exe")) {





                        List<Permission> deny = Arrays.asList(Permission.MESSAGE_READ, Permission.MESSAGE_HISTORY);


                        if (!server.getTextChannelsByName("Console", true).isEmpty()) {
                            console = server.getTextChannelsByName("Console", true).get(0);

                        } else {
                            server.createTextChannel("Console").addRolePermissionOverride(358287893225013248L, null, deny).complete();
                            console = server.getTextChannelsByName("Console", true).get(0);

                        }

                        if (!server.getTextChannelsByName("📝│config-download", true).isEmpty()) {
                            configDownload = server.getTextChannelsByName("📝│config-download", true).get(0);

                        } else {
                            server.createTextChannel("📝│config-download").complete();
                            configDownload = server.getTextChannelsByName("📝│config-download", true).get(0);

                        }
                        if (!e.getMessage().getAttachments().isEmpty()) {
                            if (!e.getAuthor().isBot()) {
                                Statement myStmt = null;
                                try {
                                    myStmt = Objects.requireNonNull(DataBaseTest.mariaDB()).createStatement();
                                } catch (SQLException e1) {
                                    e1.printStackTrace();
                                }


                                //Holt die Anhänge der Datei
                                List<Message.Attachment> attachment = e.getMessage().getAttachments();

                                message.addReaction("\uD83D\uDC4D").queue();
                                message.addReaction("\uD83D\uDC4E").queue();


                                //Läd die Datei runter
                                attachment.get(0).downloadToFile();

                                //Gibt aus, welcher User die Datei hochgeladen hat + den Namen der Datei
                                System.out.println("User " + e.getAuthor().getName() + " hat die Datei " + attachment.get(0).getFileName() + " hochgeladen.");
                                ResultSet myRes = null;
                                int configsUploaded = 0;
                                try {
                                    assert myStmt != null;
                                    myRes = myStmt.executeQuery("SELECT configCount FROM Users WHERE user = " + user.getId() + ";");
                                    while (myRes.next()) {
                                        configsUploaded  = Integer.parseInt(myRes.getString("configCount"));
                                        configsUploaded ++;
                                    }

                                    myStmt.executeQuery("UPDATE Users SET configCount = " + configsUploaded  + " WHERE user = " + user.getId() + ";");

                                } catch (SQLException e1) {
                                    e1.printStackTrace();
                                    System.out.println(e1.getSQLState());
                                    System.out.println(e1.getLocalizedMessage());
                                }


                                user.openPrivateChannel().queue((chan) ->
                                {
                                    PrivGenerell(e,chan,"#4cd137","Sucess","Your file has been uploaded! You can view the file via &configs or &config " + attachment.get(0).getFileName(),false, 0);
                                });

                                //Sended die Nachricht in den Console Channel dass die Datei heruntergeladen wurde
                                downloaded(e, console);

                                //Pfad zur Detei in die die Namen der Dateien geschreiben werden
                                File files = new File("Files.txt");

                                String read = null;
                                String line = null;

                                try (FileWriter FileWriterName = new FileWriter(files, true);
                                     BufferedWriter Writer = new BufferedWriter(FileWriterName)) {


                                    Writer.newLine();
                                    Writer.write(attachment.get(0).getFileName());


                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        } else if (!message.getAuthor().isBot()) {
                            //Löscht die Nachricht wenn es keine Datei ist
                            message.delete().queue();

                        }

                    } else {
                        ErrorBuilder(e, TxtChannel, "This Bot doesnt download .exe Files because of security concerns.", 10);
                    }

                } else if(!e.getAuthor().getId().equals("215136536260378624")){
                    message.delete().queue();
                }
            }
        }
    }

    //Embeded Builder Stuff
    //
    //
    //
    public void ErrorBuilder(GuildMessageReceivedEvent e, TextChannel txtChannel, String ErrorText, int deleteAfter) {
        EmbedBuilder embBuilder = new EmbedBuilder();
        embBuilder.setTitle("Error");
        embBuilder.setAuthor(e.getAuthor().getName(), e.getAuthor().getAvatarUrl(), e.getAuthor().getAvatarUrl());
        embBuilder.setColor(Color.decode("#d63031"));
        embBuilder.setDescription(ErrorText);
        txtChannel.sendMessage(embBuilder.build()).complete().delete().queueAfter(deleteAfter, TimeUnit.SECONDS);

    }public void Info(GuildMessageReceivedEvent e, TextChannel txtChannel, String InfoText, int deleteAfter) {
        EmbedBuilder embBuilder = new EmbedBuilder();
        embBuilder.setTitle("Incorrect use");
        embBuilder.setAuthor(e.getAuthor().getName(), e.getAuthor().getAvatarUrl(), e.getAuthor().getAvatarUrl());
        embBuilder.setColor(Color.decode("#f9ca24"));
        embBuilder.setDescription(InfoText);
        txtChannel.sendMessage(embBuilder.build()).complete().delete().queueAfter(deleteAfter, TimeUnit.SECONDS);

    } public void Success(GuildMessageReceivedEvent e,String Title, TextChannel txtChannel, String InfoText, int deleteAfter) {
        EmbedBuilder embBuilder = new EmbedBuilder();
        embBuilder.setTitle(Title);
        embBuilder.setAuthor(e.getAuthor().getName(), e.getAuthor().getAvatarUrl(), e.getAuthor().getAvatarUrl());
        embBuilder.setColor(Color.decode("#2ecc71"));
        embBuilder.setDescription(InfoText);
        txtChannel.sendMessage(embBuilder.build()).complete().delete().queueAfter(deleteAfter, TimeUnit.SECONDS);

    }


    public void Generell(GuildMessageReceivedEvent e, TextChannel txtChannel, String MessageColor, String Title, String Desc, boolean delete, int toDeleteAfter) {
        EmbedBuilder embBuilder = new EmbedBuilder();
        embBuilder.setTitle(Title);
        embBuilder.setAuthor(e.getAuthor().getName(), e.getAuthor().getAvatarUrl(), e.getAuthor().getAvatarUrl());
        embBuilder.setColor(Color.decode(MessageColor));
        embBuilder.setDescription(Desc);
        if (delete) {
            txtChannel.sendMessage(embBuilder.build()).complete().delete().queueAfter(toDeleteAfter, TimeUnit.SECONDS);
        } else {
            txtChannel.sendMessage(embBuilder.build()).queue();
        }

    }
    public void PrivGenerell(GuildMessageReceivedEvent e, PrivateChannel txtChannel, String MessageColor, String Title, String Desc, boolean delete, int toDeleteAfter) {
        EmbedBuilder embBuilder = new EmbedBuilder();
        embBuilder.setTitle(Title);
        embBuilder.setAuthor(e.getAuthor().getName(), e.getAuthor().getAvatarUrl(), e.getAuthor().getAvatarUrl());
        embBuilder.setColor(Color.decode(MessageColor));
        embBuilder.setDescription(Desc);
        if (delete) {
            txtChannel.sendMessage(embBuilder.build()).complete().delete().queueAfter(toDeleteAfter, TimeUnit.SECONDS);
        } else {
            txtChannel.sendMessage(embBuilder.build()).queue();
        }

    }

    public void downloaded(GuildMessageReceivedEvent e, TextChannel txtChannel) {
        EmbedBuilder embBuilder = new EmbedBuilder();
        embBuilder.setTitle("Success");
        embBuilder.setAuthor(e.getAuthor().getName() + " uploaded a File", e.getAuthor().getAvatarUrl(), e.getAuthor().getAvatarUrl());
        embBuilder.setColor(Color.decode("#2ecc71"));
        embBuilder.setDescription("Downloaded File " + e.getMessage().getAttachments().get(0).getFileName());
        txtChannel.sendMessage(embBuilder.build()).complete();
    }

}





