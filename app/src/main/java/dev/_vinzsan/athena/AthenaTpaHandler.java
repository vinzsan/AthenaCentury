package dev._vinzsan.athena;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import net.kyori.adventure.text.Component;

import dev._vinzsan.athena.AthenaTpaStateHandler.TpaState;

import java.util.UUID;

public class AthenaTpaHandler {
    public final UUID senderUUID;
    public final UUID targetUUID;
    public final String targetName;
    public TpaState state;
    public int timePending = 0;
    public BukkitTask taskHandler;
    
    public AthenaTpaHandler(UUID senderID,UUID targetID,String targetName){
        this.senderUUID = senderID;
        this.targetUUID = targetID;
        this.targetName = targetName;
        this.state = TpaState.PENDING;
        this.timePending = 5;
    }

    public BukkitTask startTpaCountdown(Player sender,Player target){
        this.taskHandler = new BukkitRunnable() {
            @Override
            public void run(){
                if(!sender.isOnline() || !target.isOnline()){
                    cancel();
                    return;
                }

                if(state == TpaState.ACCEPTED){
                    sender.sendMessage(
                        Component.text("[!] kamu akan teleport ke " + targetName)
                    );
                    new BukkitRunnable() {
                        int countDown = 5;
                        @Override
                        public void run(){
                            if(!sender.isOnline() || !target.isOnline()){
                                AthenaPlugin.teleportHandle.remove(targetUUID);
                                cancel();
                                return;
                            }
                            if(countDown <= 0){
                                sender.playSound(sender.getLocation(),Sound.ENTITY_PLAYER_LEVELUP,1.0f,1.0f);
                                sender.teleport(target.getLocation());
                                AthenaPlugin.teleportHandle.remove(targetUUID);
                                cancel();
                                return;
                            }
                            sender.playSound(sender.getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1.0f,1.0f);
                            countDown--;
                        }
                    }.runTaskTimer(AthenaPlugin.instance,0L,20L);
                    cancel();
                    return;
                }

                if(state == TpaState.REJECTED){
                    sender.sendMessage(
                        Component.text("[!] kamu di tolak oleh " + targetName)
                    );
                    cancel();
                    return;
                }

                if(timePending <= 0){
                    sender.sendMessage(
                        Component.text("[!] tpa expired")
                    );
                    AthenaPlugin.teleportHandle.remove(targetUUID);
                    cancel();
                    return;
                }
                timePending--;
            }
        }.runTaskTimer(AthenaPlugin.instance, 0L,20L);
        return this.taskHandler;
    }
}
