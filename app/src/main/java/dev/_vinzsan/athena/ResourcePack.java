package dev._vinzsan.athena;

import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import io.papermc.paper.connection.PlayerConfigurationConnection;
import io.papermc.paper.event.connection.configuration.AsyncPlayerConnectionConfigureEvent;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.resource.ResourcePackStatus;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;


public class ResourcePack implements Listener {
    private static final ResourcePackInfo PACK_INFO = ResourcePackInfo.resourcePackInfo()
        .uri(URI.create("https://github.com/vinzsan/Weistern/raw/main/athena-pack.zip"))
        .hash("eae08139c9455d49513122cd8bfbe8f5bda4f51e")
        .build();
    private static final ResourcePackInfo PACK_GRAPHICS = ResourcePackInfo.resourcePackInfo()
        .uri(URI.create("https://github.com/vinzsan/Weistern/raw/main/Dramatic%20Skys%20Demo%201.5.3.36.5.zip"))
        .hash("c7d3a0911135f2c1f873283faaa34879df46326c")
        .build();
        
    private static final boolean REQUIRED = true;
    private static final int TIMEOUT_SECS = 60;

    @EventHandler
    public void onConfigure(AsyncPlayerConnectionConfigureEvent event){
        PlayerConfigurationConnection connection = event.getConnection();
        
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        future.completeOnTimeout(false, TIMEOUT_SECS, TimeUnit.SECONDS);

        var request = ResourcePackRequest.resourcePackRequest()
            .packs(PACK_INFO,PACK_GRAPHICS)
            .prompt(Component.text("[!] Server ini membutuhkan resourcepack custom",NamedTextColor.GOLD,TextDecoration.UNDERLINED))
            .required(REQUIRED)
            .callback((uuid,status,audience) -> {
                if(status.intermediate()){
                    return;
                }

                boolean success = status == ResourcePackStatus.SUCCESSFULLY_LOADED;
                future.complete(success);
            })
            .build();
            
        connection.getAudience().sendResourcePacks(request);
        
        boolean success = future.join();

        if(!success && REQUIRED){
            connection.disconnect(
                Component.text("[!] sepertinya gagal :(",NamedTextColor.RED)
                .append(Component.newline())
                .append(Component.text("IP: " + connection.getAddress()))
            );
        }
    }
}
