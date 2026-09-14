package dev._vinzsan.athena;

import java.net.InetAddress;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.ban.IpBanList;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class AthenaTempBan {
    private final UUID playerUUID;
    private final InetAddress ipBan;
    private final String reason;
    
    public Dialog buildBannedDialog(){
        return Dialog.create(builder -> builder.empty()
            .base(DialogBase.builder(Component.text("Player Guility"))
            .externalTitle(Component.text("Kamu telah di banned",NamedTextColor.GRAY))
            .canCloseWithEscape(false)
            .body(
                List.of(
                    DialogBody.plainMessage(Component.text("Kamu telah di banned",NamedTextColor.GRAY)),
                    DialogBody.plainMessage(Component.text("NOTE: banned kamu bersifat temporary",NamedTextColor.GRAY)),
                    DialogBody.plainMessage(Component.text("REASON:",NamedTextColor.RED,TextDecoration.BOLD)),
                    DialogBody.plainMessage(Component.text("(" + getReason() + ")",NamedTextColor.GOLD)),
                    DialogBody.plainMessage(Component.text("Ip : " + ipBan.toString(),NamedTextColor.AQUA)),
                    DialogBody.item(new ItemStack(Material.BARRIER)).build()
                )
            )
            .build())
            .type(DialogType.multiAction(
                List.of(
                    ActionButton.builder(Component.text("Ok"))
                        .action(DialogAction.customClick(Key.key("athena:banned_acc"), null))
                        .tooltip(Component.text("Silahkan hubungi admin jika salah"))
                        .build()
                ),null,2))
        );
    }

    public void showBannedDialog(Player player){
        Bukkit.getScheduler().runTaskLater(AthenaPlugin.instance,() -> {
            if(!player.isOnline()) return;
            player.showDialog(buildBannedDialog());
            AthenaPlugin.log.info("[Athena Temporary Banned] player " + player.getName() + " telah di banned (" + playerUUID + ")");
        },20L);
    }

    AthenaTempBan(Player player,String reason,InetAddress ip){
        this.playerUUID = player.getUniqueId();
        this.reason = reason;
        this.ipBan = ip;
        
        showBannedDialog(player);
    }
    
    public String getReason(){
        return this.reason != null ? this.reason : "Player have no reason";
    }
}
