// Copyright (c) 2023 Joseph Hale
// 
// This Source Code Form is subject to the terms of the Mozilla Public
// License, v. 2.0. If a copy of the MPL was not distributed with this
// file, You can obtain one at http://mozilla.org/MPL/2.0/.

package dev._vinzsan.athena;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.jar.Attributes.Name;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.checkerframework.checker.units.qual.C;
import org.joml.Vector2i;
import org.jspecify.annotations.Nullable;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import dev._vinzsan.athena.AthenaTpaStateHandler.TpaState;
import dev._vinzsan.athena.bstats.Metrics;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.math.Rotation;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEventType;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import it.unimi.dsi.fastutil.Hash;
import net.kyori.adventure.text.format.NamedTextColor;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.NamespacedKey;

import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class AthenaPlugin extends JavaPlugin {

    public static AthenaPlugin instance;
    public static Logger log;
    public final static String NAME = "Athena";
    public final static int BSTATS_PLUGIN_ID = 20765;  // Optional: Replace with your own bStats plugin ID
    
    private static final Set<Vector2i> chunkUnloaded = new HashSet<>();

    public static final HashMap<UUID,AthenaTpaHandler> teleportHandle = new HashMap<>();

    /**
     * Default constructor.
     * 
     * <p>Used solely by MockBukkit during unit tests.
     */

    /**
     * Parameterized constructor.
     * 
     * <p>Used solely by MockBukkit during unit tests.
     */

    @Override
    public void onEnable() {
        instance = this;
        log = getLogger();
        logTemplateAttribution();
        setup();
        log.info("[ATHENA PLUGINS] Status Loaded");
        log.info("[ATHENA PLUGINS] Athena plugins, semua custom commands bisa di append");

        LegalChar.register(instance);

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS,events -> {
            events.registrar().register(getCoordinatPlayer(),"command untuk ambil koordinat current player");
            events.registrar().register(ModeratorPlayerAction(),"dispatcher khusus untuk moderator");
            events.registrar().register(TeleportPlayer(),"teleport ke player");
            events.registrar().register(ChunkloadChunk(),"chunk load");
            events.registrar().register(ChunkunloadChunk(),"unload chunk");
            events.registrar().register(giveOrbitalStick(),"membuat orbital strike stick");
            events.registrar().register(walkingEffect(),"membuat effect ketika berjalan");
            events.registrar().register(destroyUnder(),"merusak block di bawah player");
            events.registrar().register(capek(),"capek yaudah");
            events.registrar().register(giveFireballStick(),"membuat stick fireball");
            events.registrar().register(giveHealStick(),"buat stick heal");
            events.registrar().register(giveArmorCancelable(),"armor cancel event");
            events.registrar().register(giveBlackSword(),"black sword");
            events.registrar().register(ridePlayer(),"ride player");
            events.registrar().register(giveDisc(),"buat disc");
            events.registrar().register(rolesHandler(),"command untuk handler roles");
            events.registrar().register(tmpBanned(),"banned temporary player");
            events.registrar().register(givePoseStick(),"pose stick");
            events.registrar().register(giveNecromancerStick(),"necromancer");
            events.registrar().register(yuvyStrikePlayer(),"yuvy power only");
            events.registrar().register(giveElderStick(),"memberi elder stick");
        });
        
        new BukkitRunnable() {
            @Override
            public void run(){
                for(Player player : Bukkit.getOnlinePlayers()){
                    ItemStack armor = player.getInventory().getHelmet();
                    if(armor == null || armor.getType() != Material.GOLDEN_HELMET) continue;

                    ItemMeta meta = armor.getItemMeta();
                    if(meta == null) continue;

                    Boolean armorBoolean = meta.getPersistentDataContainer()
                        .get(AthenaListener.ARMOR_CANCELING,PersistentDataType.BOOLEAN);
                        
                    if(armorBoolean == null || !armorBoolean) continue;

                    World world = player.getWorld();
                    
                    Location particleLocation = player.getLocation();
                    double radius = 2.0;
                    for(double i = 0.0;i <= Math.PI * 2;i += 0.4){
                        double x = Math.cos(i) * radius;
                        double z = Math.sin(i) * radius;

                        world.spawnParticle(Particle.SMOKE, particleLocation,2);
                    }
                }
            }
        }.runTaskTimer(instance,0L,20L);

        new BukkitRunnable() {
            @Override
            public void run(){
                for(Player player : Bukkit.getOnlinePlayers()){
                    ItemStack item = player.getInventory().getItemInMainHand();
                    if(item == null || item.getType() != Material.WOODEN_HOE) continue;
                    
                    ItemMeta meta = item.getItemMeta();
                    if(meta == null) continue;

                    Boolean yuvyStick = meta.getPersistentDataContainer()
                        .get(AthenaListener.ORBITAL_STICK_KEY,PersistentDataType.BOOLEAN);
                        
                    if(yuvyStick == null || !yuvyStick) continue;
                        
                    var world = player.getWorld();

                    Location center = player.getLocation().add(0,player.getHeight() * 0.5,0);
                    double radius = 2.0;
                    for(double angle = 0.0;angle <= Math.PI * 2;angle += 0.2){
                        double x = radius * Math.cos(angle);
                        double z = radius * Math.sin(angle);
                        Location particleLocation = center.clone().add(x,0,z);
                        world.spawnParticle(Particle.ENCHANT, particleLocation, 2);
                    }
                }
            }
        }.runTaskTimer(instance,0L,20L);
        
        new BukkitRunnable() {
            @Override
            public void run(){
                for(var p : Bukkit.getOnlinePlayers()){
                    ItemStack sword = p.getInventory().getItemInMainHand();
                    if(sword == null || sword.getType() != Material.NETHERITE_SWORD) continue;
                    
                    ItemMeta meta = sword.getItemMeta();
                    if(meta == null) continue;

                    Boolean swordBoolean = meta.getPersistentDataContainer()
                        .get(AthenaListener.BLACK_SWORD,PersistentDataType.BOOLEAN);
                        
                    if(swordBoolean == null || !swordBoolean) continue;
                    
                    World world = p.getWorld();
                    double radius = 1.5;
                    Location player = p.getLocation().clone().add(0,p.getHeight() * 0.5,0);
                    for(double i = 0.0;i <= Math.PI * 2;i += 0.2f){
                        double x = radius * Math.cos(i);
                        double z = radius * Math.sin(i);

                        Location spawLocation = player.clone().add(x,0.0,z);
                        world.spawnParticle(Particle.CRIMSON_SPORE, spawLocation, 2);
                    }
                }
            }
        }.runTaskTimer(instance,0L,20L);
    }
    
    private com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> getCoordinatPlayer() {
        return Commands.literal("coordinat")
            .then(Commands.argument("<player_name>",StringArgumentType.string())
                .suggests((context,builder) -> {
                    for(var name : this.getServer().getOnlinePlayers()){
                        builder.suggest(name.getName());
                    }
                    return builder.buildFuture();
                })
                .executes(context -> {
                    if(context.getSource().getSender() instanceof Player sender) {
                        String player_name = StringArgumentType.getString(context,"<player_name>");
                        var player = this.getServer().getPlayer(player_name);
                        if(player  == null){
                            sender.sendMessage(
                                Component.text("ERROR: player tidak di temukan",NamedTextColor.RED)
                            );
                            return 0;
                        }

                        var position = player.getLocation();
                        sender.sendMessage(
                            Component.text("INFO: player position (" + player.getName() + ")",NamedTextColor.GREEN).append(Component.newline())
                            .append(Component.text("Location : XYZ " + position.x() + "," + position.y() + "," + position.z(),NamedTextColor.BLUE))
                            .append(Component.newline())
                            .append(Component.text("Dimension: " + player.getWorld().getKey(),NamedTextColor.LIGHT_PURPLE,TextDecoration.UNDERLINED))
                        );
                        return Command.SINGLE_SUCCESS;
                    }
                    return 0;
                })).build();
    }
    
    private com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> TeleportPlayer() {
        return Commands.literal("tpa")
            .then(Commands.argument("player_name",StringArgumentType.string())
                .suggests((context,builder) -> {
                    for(var p : getServer().getOnlinePlayers()){
                        builder.suggest(p.getName());
                    }
                    return builder.buildFuture();
                })
                .executes(context -> {
                    if(context.getSource().getSender() instanceof Player sender){
                        String targetName = StringArgumentType.getString(context,"player_name");
                        var target = getServer().getPlayer(targetName);
                        if(target == null){
                            sender.sendMessage(
                                Component.text("INFO: player tidak di temukan",NamedTextColor.RED)
                            );
                            sender.playSound(sender.getLocation(),Sound.ENTITY_ITEM_BREAK,1.0f,1.0f);
                            return 1;
                        }

                        if(target.getUniqueId().equals(sender.getUniqueId())){
                            sender.sendMessage(
                                Component.text("INFO: kamu tidak bisa teleport ke diri kamu sendiri.",NamedTextColor.RED,
                                    TextDecoration.ITALIC
                                )
                            );
                            sender.playSound(sender.getLocation(),Sound.ENTITY_ITEM_BREAK,1.0f,1.0f);
                            return 1;
                        }
                        
                        if(teleportHandle.containsKey(target.getUniqueId())){
                            sender.sendMessage(
                                Component.text("[!] kamu tidak bisa teleport, kamu masih pending")
                            );
                            sender.playSound(sender.getLocation(),Sound.AMBIENT_CAVE,1.0f,1.0f);
                            return 1;
                        }
                        
                        AthenaTpaHandler request = new AthenaTpaHandler(sender.getUniqueId(),target.getUniqueId(),targetName);
                        teleportHandle.put(target.getUniqueId(),request);
                        
                        target.sendMessage(
                            Component.text("[!] " + sender.getName() + " ingin tpa ke kamu, accept jika mengizinkan",NamedTextColor.AQUA)
                        );
                        
                        request.startTpaCountdown(sender,target);   
                        return Command.SINGLE_SUCCESS;
                    }
                    return 0;
                })
            )
            .then(Commands.literal("accept")
                .executes(context -> {
                    if(context.getSource().getSender() instanceof Player player) {
                        AthenaTpaHandler request = teleportHandle.get(player.getUniqueId());
                        if(request == null){
                            player.sendMessage(
                                Component.text("[!] tidak ada permintaan tpa",NamedTextColor.RED)
                            );
                            return 1;
                        }

                        request.state = TpaState.ACCEPTED;
                        player.sendMessage(
                            Component.text("[!] kamu menerima tpa",NamedTextColor.GOLD)
                        );
                        player.playSound(player.getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1.0f,1.0f);
                    }
                    return Command.SINGLE_SUCCESS;
                })
            )
            .then(Commands.literal("reject")
                .executes(context -> {
                    if(context.getSource().getSender() instanceof Player player){
                        AthenaTpaHandler request = teleportHandle.get(player.getUniqueId());
                        if(request == null){
                            player.sendMessage(
                                Component.text("[!] tidak ada permintaan tpa",NamedTextColor.RED)
                            );
                            return 1;
                        }
                        request.state = TpaState.REJECTED;
                        teleportHandle.remove(player.getUniqueId());
                        player.sendMessage(
                            Component.text("[!] kamu telah menolak permintaan tpa",NamedTextColor.DARK_PURPLE)
                        );
                    }
                    return Command.SINGLE_SUCCESS;
                })
        )
        .build();
    }
    
    private com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> giveOrbitalStick(){
        return Commands.literal("give-yuvy-stick")
            .requires(sender -> sender.getSender().isOp())
            .executes(context -> {
                if(context.getSource().getSender() instanceof Player player){
                    player.getInventory().addItem(AthenaListener.createOrbitalStick());
                    player.sendMessage(Component.text("[!] Yuvy Stick telah di buat,hati hati jangan gunakan di sekitar base"));
                }
                return Command.SINGLE_SUCCESS;
            }).build();
    }

    private com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> giveFireballStick(){
        return Commands.literal("give-fireball-stick")
            .requires(sender -> sender.getSender().isOp())
            .executes(context -> {
                if(context.getSource().getSender() instanceof Player player){
                    player.getInventory().addItem(AthenaListener.createFireballStick());
                    player.sendMessage(
                        Component.text("[!] Fireball stick di buat")
                    );
                }
                return Command.SINGLE_SUCCESS;
            }).build();
    }
    

    private com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> giveHealStick(){
        return Commands.literal("give-heal-stick")
            .requires(sender -> sender.getSender().isOp())
            .executes(context -> {
                if(context.getSource().getSender() instanceof Player player){
                    player.getInventory().addItem(AthenaListener.createHealStick());
                    player.sendMessage(
                        Component.text("[!] Heal stick di buat")
                    );
                }
                return Command.SINGLE_SUCCESS;
            }).build();
    }
    
    private com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> giveArmorCancelable(){
        return Commands.literal("give-armor-cancelable")
            .requires(sender -> sender.getSender().isOp())
            .executes(context -> {
                if(context.getSource().getSender() instanceof Player player){
                    player.getInventory().addItem(AthenaListener.createArmor());
                    player.sendMessage(
                        Component.text("[!] armor telah di buat")
                    );
                }
                return Command.SINGLE_SUCCESS;
            }).build();
    }

    private com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> giveBlackSword(){
        return Commands.literal("give-black-sword")
            .requires(sender -> sender.getSender().isOp())
            .executes(context -> {
                if(context.getSource().getSender() instanceof Player player){
                    player.getInventory().addItem(AthenaListener.createBlackSword());
                    player.sendMessage(
                        Component.text("[!] pedang telah di buat")
                    );
                }
                return Command.SINGLE_SUCCESS;
            }).build();
    }

    private com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> givePoseStick(){
        return Commands.literal("give-pose-stick")
            .requires(sender -> sender.getSender().isOp())
            .executes(context -> {
                if(context.getSource().getSender() instanceof Player player){
                    player.getInventory().addItem(AthenaListener.createPoseChanger());
                    player.sendMessage(
                        Component.text("[!] stick telah di buat")
                    );
                }
                return Command.SINGLE_SUCCESS;
            }).build();
    }

    private com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> giveNecromancerStick(){
        return Commands.literal("give-necromancer-stick")
            .requires(sender -> sender.getSender().isOp())
            .executes(context -> {
                if(context.getSource().getSender() instanceof Player player){
                    player.getInventory().addItem(AthenaListener.createNecromancerStick());
                    player.sendMessage(
                        Component.text("[!] stick telah di buat")
                    );
                }
                return Command.SINGLE_SUCCESS;
            }).build();
    }
    
    private com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> giveElderStick(){
        return Commands.literal("give-elder-stick")
            .requires(sender -> sender.getSender().isOp())
            .executes(context -> {
                if(context.getSource().getSender() instanceof Player player){
                    player.getInventory().addItem(AthenaListener.createElderStick());
                    player.sendMessage(
                        Component.text("[!] elder stick telah di buat")
                    );
                }
                return Command.SINGLE_SUCCESS;
            }).build();
    }
    
    private com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> ridePlayer(){
        return Commands.literal("ride_player")
            .then(Commands.literal("mount")
            .then(Commands.argument("player_name", StringArgumentType.string())
            .suggests((context,builder) -> {
                for(var p : Bukkit.getServer().getOnlinePlayers()){
                    builder.suggest(p.getName());
                }
                return builder.buildFuture();
            })
            .executes(context -> {
                if(context.getSource().getSender() instanceof Player player){
                    String targetName = StringArgumentType.getString(context,"player_name");
                    Player target = Bukkit.getPlayer(targetName);
                    if(target == null){
                        player.sendMessage(
                            Component.text("[!] player tidak ada")
                        );
                        return 1;
                    }
                    target.addPassenger(player);
                }
                return Command.SINGLE_SUCCESS;
            })))
            .then(Commands.literal("unmount")
            .executes(context -> {
                if(context.getSource().getSender() instanceof Player player){
                    var passengerList = player.getPassengers();
                    if(!passengerList.isEmpty()){
                        for(var list : passengerList){
                            player.removePassenger(list);
                        }
                        player.sendMessage(
                            Component.text("[!] passenger sudah di bersihkan")
                        );
                    } else {
                        player.sendMessage(
                            Component.text("[!] passenger tidak di temukan")
                        );
                    }
                }
                return Command.SINGLE_SUCCESS;
            })
        ).build();
    }

    private com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> walkingEffect(){
        return Commands.literal("walking-effect")
            .requires(sender -> sender.getSender().isOp())
            .then(Commands.argument("toggle[enable/disable]",StringArgumentType.string())
            .executes(context -> {
                if(context.getSource().getSender() instanceof Player player){
                    String argument = StringArgumentType.getString(context,"toggle[enable/disable]");
                    if(argument.equals("enable")){
                        if(AthenaListener.walkingEffectPlayerList.containsKey(player.getUniqueId())){
                            player.sendMessage(
                                Component.text("[!] kamu sudah mengaktifkan toggle ini")
                            );
                            return 1;
                        } else {
                            AthenaListener.walkingEffectPlayerList.put(player.getUniqueId(),true);
                            player.sendMessage(
                                Component.text("[?] walking effect telah di aktifkan")
                            );
                        }
                    } else if(argument.equals("disable")){
                        if(!AthenaListener.walkingEffectPlayerList.containsKey(player.getUniqueId())){
                            player.sendMessage(
                                Component.text("[!] kamu sudah menonaktifkan toggle ini")
                            );
                            return 1;
                        } else {
                            AthenaListener.walkingEffectPlayerList.remove(player.getUniqueId());
                            player.sendMessage(
                                Component.text("[?] walkin effect telah di nonaktifkan")
                            );
                        }
                    } else {
                        player.sendMessage(
                            Component.text("[?] mohon pakai 'enable' atau 'disable'")
                        );
                    }
                }
                return Command.SINGLE_SUCCESS;
            })
        ).build();
    }

    private com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> destroyUnder(){
        return Commands.literal("destroy-under")
            .requires(sender -> sender.getSender().isOp())
            .then(Commands.argument("length",IntegerArgumentType.integer(1,100))
            .executes(context -> {
                if(context.getSource().getSender() instanceof Player player){
                    int level = IntegerArgumentType.getInteger(context,"length");
                    World world = player.getWorld();
                    
                    org.bukkit.Location currentLocation = player.getLocation();
                    for(int i = 0;i < level;i++){
                        world.createExplosion(currentLocation,5.0f,false,true);
                        currentLocation.setY(currentLocation.getY() - i);
                    }
                }
                return Command.SINGLE_SUCCESS;
            })
        ).build();
    }
    

    private com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> ChunkunloadChunk(){
        return Commands.literal("unload-chunk")
            .requires(sender -> sender.getSender().isOp())
            .then(Commands.argument("x",IntegerArgumentType.integer())
            .then(Commands.argument("z",IntegerArgumentType.integer())
            .executes(context -> {
                int blockX = Math.floorDiv(IntegerArgumentType.getInteger(context,"x"),16);
                int blockZ = Math.floorDiv(IntegerArgumentType.getInteger(context,"z"),16);
            
                var vecpos = new Vector2i(blockX,blockZ);
                if(chunkUnloaded.contains(vecpos)){
                    context.getSource().getSender().sendMessage(Component.text("[!] chunk tidak bisa di unload karena sudah di unload sebelumnya"));
                    return 1;
                }
            
                World w = Bukkit.getWorlds().get(0);
                w.removePluginChunkTicket(blockX, blockZ, instance);
            
                if(!w.unloadChunk(blockX,blockZ)){
                    context.getSource().getSender().sendMessage(
                        Component.text("[!] chunk tidak bisa di unload")
                    );
                    return 1;
                }
                chunkUnloaded.add(vecpos);
            
                context.getSource().getSender().sendMessage(Component.text("[!] : info chunk di " + blockX + "," + blockZ + " telah di unload"));
                return Command.SINGLE_SUCCESS;
            })
        )
        ).build();
    }
    
    private com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> ChunkloadChunk(){
        return Commands.literal("load-chunk")
            .requires(source -> source.getSender().isOp())
            .then(Commands.argument("x",IntegerArgumentType.integer())
            .then(Commands.argument("z",IntegerArgumentType.integer())
            .executes(context -> {
                int blockX = Math.floorDiv(IntegerArgumentType.getInteger(context,"x"),16);
                int blockZ = Math.floorDiv(IntegerArgumentType.getInteger(context,"z"),16);
            
                var vecpos = new Vector2i(blockX,blockZ);
                if(!chunkUnloaded.contains(vecpos)){
                    context.getSource().getSender().sendMessage(Component.text("[!] chunk tidak bisa di load karena sudah di load sebelumnya"));
                    return 1;
                }
                World w = Bukkit.getWorlds().get(0);
                w.loadChunk(blockX,blockZ);
                w.addPluginChunkTicket(blockX, blockZ, instance);
                chunkUnloaded.remove(vecpos);
                context.getSource().getSender().sendMessage(Component.text("[!] : info chunk di " + blockX + "," + blockZ + " telah di load"));
                return Command.SINGLE_SUCCESS;
            }))).build();
    }
    
    private com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> capek(){
        return Commands.literal("capek")
            .executes(context -> {
                if(context.getSource().getSender() instanceof Player p){
                    p.sendMessage(
                        Component.text("Yaudah")
                    );
                    p.kill();
                }
                return Command.SINGLE_SUCCESS;
            }).build();
    }
    
    private LiteralArgumentBuilder<CommandSourceStack> ScanAreaCommand() {
            return Commands.literal("scan-area")
                .requires(source -> source.getSender().isOp())
                .then(Commands.argument("x", com.mojang.brigadier.arguments.IntegerArgumentType.integer())
                .then(Commands.argument("z", com.mojang.brigadier.arguments.IntegerArgumentType.integer())
                .then(Commands.argument("width", com.mojang.brigadier.arguments.IntegerArgumentType.integer(1, 32))
                .then(Commands.argument("length", com.mojang.brigadier.arguments.IntegerArgumentType.integer(1, 32))
                .executes(context -> {
                    var sender = context.getSource().getSender();
                
                    int blockX = com.mojang.brigadier.arguments.IntegerArgumentType.getInteger(context, "x");
                    int blockZ = com.mojang.brigadier.arguments.IntegerArgumentType.getInteger(context, "z");
                    int width  = com.mojang.brigadier.arguments.IntegerArgumentType.getInteger(context, "width");
                    int length = com.mojang.brigadier.arguments.IntegerArgumentType.getInteger(context, "length");
                
                    World world;
                    if (sender instanceof Player p) {
                        world = p.getWorld();
                    } else {
                        world = Bukkit.getWorlds().get(0);
                    }
                
                    // convert block coordinate ke chunk coordinate
                    int centerCx = blockX >> 4;
                    int centerCz = blockZ >> 4;
                
                    // half-extent, biar centering-nya presisi ke tengah
                    int halfW = (width - 1) / 2;
                    int halfL = (length - 1) / 2;
                
                    int minCx = centerCx - halfW;
                    int maxCx = centerCx + (width - 1 - halfW);
                    int minCz = centerCz - halfL;
                    int maxCz = centerCz + (length - 1 - halfL);
                
                    List<Player> foundPlayers = new java.util.ArrayList<>();
                    List<org.bukkit.entity.LivingEntity> foundMobs = new java.util.ArrayList<>();
                    int scannedChunks = 0;
                    int skippedChunks = 0;
                
                    for (int cx = minCx; cx <= maxCx; cx++) {
                        for (int cz = minCz; cz <= maxCz; cz++) {
                        
                            // JANGAN pakai getChunkAt() langsung, itu bisa force-load
                            if (!world.isChunkLoaded(cx, cz)) {
                                skippedChunks++;
                                continue;
                            }
                        
                            Chunk chunk = world.getChunkAt(cx, cz); // aman, sudah dipastikan loaded
                            scannedChunks++;
                        
                            for (org.bukkit.entity.Entity entity : chunk.getEntities()) {
                                if (entity instanceof Player player) {
                                    foundPlayers.add(player);
                                } else if (entity instanceof org.bukkit.entity.LivingEntity living) {
                                    foundMobs.add(living);
                                }
                            }
                        }
                    }
                
                    // ==== kirim hasil ke sender ====
                    sender.sendMessage(
                        Component.text("=== SCAN AREA RESULT ===", NamedTextColor.GOLD, TextDecoration.BOLD)
                    );
                    sender.sendMessage(
                        Component.text("Center chunk: (" + centerCx + ", " + centerCz + ") | Area: " + width + "x" + length, NamedTextColor.GRAY)
                    );
                    sender.sendMessage(
                        Component.text("Chunk di-scan: " + scannedChunks + " | Chunk di-skip (belum loaded): " + skippedChunks, NamedTextColor.YELLOW)
                    );
                    sender.sendMessage(
                        Component.text("Player ditemukan: " + foundPlayers.size(), NamedTextColor.AQUA)
                    );
                    for (Player pl : foundPlayers) {
                        sender.sendMessage(Component.text(" - " + pl.getName(), NamedTextColor.WHITE));
                    }
                    sender.sendMessage(
                        Component.text("Mob ditemukan: " + foundMobs.size(), NamedTextColor.LIGHT_PURPLE)
                    );
                
                    // rekap mob per jenis biar nggak spam chat kalau banyak
                    java.util.Map<String, Integer> mobCount = new java.util.HashMap<>();
                    for (var mob : foundMobs) {
                        mobCount.merge(mob.getType().name(), 1, Integer::sum);
                    }
                    for (var entry : mobCount.entrySet()) {
                        sender.sendMessage(Component.text(" - " + entry.getKey() + " x" + entry.getValue(), NamedTextColor.WHITE));
                    }
                
                    return Command.SINGLE_SUCCESS;
                })
            ))));
    }   

    private com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> ModeratorPlayerAction() {
        return Commands.literal("athena-moderator")
            .requires(source -> source.getSender().isOp())
            .then(Commands.literal("freeze-player")
            .then(Commands.argument("[target_name]",StringArgumentType.string())
            .suggests((context,builder) -> {
                for(var player_name : this.getServer().getOnlinePlayers()){
                    builder.suggest(player_name.getName());
                }
                return builder.buildFuture();
            })
            .executes(context -> {
                if(context.getSource().getSender() instanceof Player sender){
                    if(!(context.getSource().getSender() instanceof Player player)){
                        return 0;
                    }

                    String player_name = StringArgumentType.getString(context,"[target_name]");
                    var target = getServer().getPlayer(player_name);
                    if(target == null){
                        sender.sendMessage(Component.text("player tidak di temukan"));
                        return 0;
                    }
                    
                    boolean isFrooze = AthenaListener.toggleFreeze(target.getUniqueId());
                    if(isFrooze){
                        sender.sendMessage(
                            Component.text("[!] : player (" + player_name + ") telah di bekukan",NamedTextColor.BLUE)
                        );
                        target.sendMessage(
                            Component.text("[!] : Kamu telah di bekukan, silahkan appeal ke moderator atau admin",NamedTextColor.DARK_BLUE,TextDecoration.UNDERLINED)
                                .append(Component.newline())
                                .append(Component.text("pergerakan dan juga aktivitas kamu di batasi, (command di izinkan)",NamedTextColor.YELLOW))
                                .append(Component.newline())
                                .append(Component.text("TIPS: silahkan teleport ke teman dekat atau keluar dari server sesaat, agar tidak terkena grief dari player lainya",NamedTextColor.GOLD,TextDecoration.UNDERLINED))
                        );
                        target.playSound(target.getLocation(),Sound.ENTITY_VEX_AMBIENT,1.0f,1.0f);
                        target.spawnParticle(Particle.SCRAPE,target.getLocation(),100,0.5,1.0,0.5,1.0);
                        sender.playSound(sender.getLocation(),Sound.ENTITY_ITEM_BREAK,1.0f,1.0f);
                    } else {
                        sender.sendMessage(
                            Component.text("INFO: player (" + player_name + ") tidak di bekukan")
                        );
                        target.sendMessage(
                            Component.text("INFO: " + sender.getName() + " telah membebaskan anda",NamedTextColor.AQUA)
                        );
                    }
                }
                return Command.SINGLE_SUCCESS;
            })
        )
        ).build();
    }

    private com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> tmpBanned(){
        return Commands.literal("athena_banned")
            .requires(sender -> sender.getSender().isOp())
            .then(Commands.literal("banned")
            .then(Commands.argument("player_name",StringArgumentType.string())
            .suggests((context,builder) -> {
                for(var p : Bukkit.getOnlinePlayers()){
                    builder.suggest(p.getName());
                }
                return builder.buildFuture();
            })
            .then(Commands.argument("reason",StringArgumentType.string())
            .executes(context -> {
                if(context.getSource().getSender() instanceof Player player){
                    Player ptarget = Bukkit.getPlayer(StringArgumentType.getString(context,"player_name"));
                    String reason = StringArgumentType.getString(context,"reason");
                    if(ptarget == null) {
                        player.sendMessage(
                            Component.text("[!] player tidak di temukan",NamedTextColor.RED)
                        );
                        return 1;
                    }
                    
                    AthenaListener.playerTemporaryBannedList.put(ptarget.getUniqueId(),new AthenaTempBan(
                        ptarget,reason,ptarget.getConnection().getClientAddress().getAddress()));
                    ptarget.showDialog(AthenaListener.playerTemporaryBannedList.get(ptarget.getUniqueId()).buildBannedDialog());
                }
                return Command.SINGLE_SUCCESS;
            }))
            ))        
            .then(Commands.literal("unbanned")
            .then(Commands.argument("player_name",StringArgumentType.string())
            .suggests((context,builder) -> {
                for(var p : Bukkit.getOnlinePlayers()){
                    builder.suggest(p.getName());
                }
                return builder.buildFuture();
            })
            .executes(context -> {
                if(context.getSource().getSender() instanceof Player player){
                    Player target = Bukkit.getPlayer(StringArgumentType.getString(context, "player_name"));
                    if(target == null) {
                        player.sendMessage(
                            Component.text("[!] player tidak dikenali",NamedTextColor.RED)
                        );
                        return 1;
                    }
                    
                    AthenaListener.playerTemporaryBannedList.remove(target.getUniqueId());
                    if(AthenaListener.playerTemporaryBannedList.containsKey(target.getUniqueId())){
                        player.sendMessage(
                            Component.text("[!] error, player masih ada di dalam list",NamedTextColor.RED)
                        );
                        return 1;
                    }
                    player.sendMessage(
                        Component.text("[!] player " + target.getName() + " sudah di unbanned")
                    );
                }
                return Command.SINGLE_SUCCESS;
            })
        )
        )
        .build();
    }
    
    private com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> rolesHandler(){
        return Commands.literal("athena_roles")
        .requires(sender -> sender.getSender().isOp())
        .then(Commands.literal("check")
        .then(Commands.argument("player_name",StringArgumentType.string())
        .suggests((context,builder) -> {
            for(var player : Bukkit.getOnlinePlayers()){
                builder.suggest(player.getName());
            }
            return builder.buildFuture();
        })
        .executes(context -> {
            if(context.getSource().getSender() instanceof Player player){
                Player ptarget = Bukkit.getPlayer(StringArgumentType.getString(context,"player_name"));
                if(ptarget == null){
                    player.sendMessage(
                        Component.text("[!] unexpectable error, player not found",NamedTextColor.RED)
                    );
                    return 1;
                }
                var target = AthenaListener.ATHENA_ROLES_PLAYER_ID_LIST.get(ptarget.getUniqueId());
                if(target == null){
                    player.sendMessage(
                        Component.text("[!] player ini tidak assign role")
                    );
                    return 1;
                }
                switch (target.roles) {
                    case ADVENTURE -> player.sendMessage(Component.text("[!] player " + ptarget.getName() + " roles Adventure - " + target.getSubRolesName()));
                    case MAGE -> player.sendMessage(Component.text("[!] player " + ptarget.getName() + " roles Mage - " + target.getSubRolesName()));
                    case KNIGHT -> player.sendMessage(Component.text("[!] player " + ptarget.getName() + " roles Knight - " + target.getSubRolesName()));
                    case ARCHERY -> player.sendMessage(Component.text("[!] player " + ptarget.getName() + " roles Archery - " + target.getSubRolesName()));
                    default -> player.sendMessage(Component.text("[!] player ini tidak punya roles"));
                }
            }
            return Command.SINGLE_SUCCESS;
        })
        ))
        .then(Commands.literal("delete")
        .then(Commands.argument("player_name",StringArgumentType.string())
        .suggests((context,builder) -> {
            for(var p : Bukkit.getOfflinePlayers()){
                builder.suggest(p.getName());
            }
            return builder.buildFuture();
        })
        .executes(context -> {
            if(context.getSource().getSender() instanceof Player player){
                Player ptarget = Bukkit.getPlayer(StringArgumentType.getString(context,"player_name"));
                if(ptarget == null){
                    player.sendMessage(
                        Component.text("[!] player tidak di temukan",NamedTextColor.RED)
                    );
                    return 1;
                }
                if(AthenaListener.ATHENA_ROLES_PLAYER_ID_LIST.get(ptarget.getUniqueId()) != null){
                    AthenaListener.ATHENA_ROLES_PLAYER_ID_LIST.remove(ptarget.getUniqueId());
                    player.sendMessage(
                        Component.text("[!] player telah di hapus dari daftar roles " + ptarget.getName())
                    );
                }
            }
            return Command.SINGLE_SUCCESS;
        })
        ))
        .build();
    }
    
    private com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> yuvyStrikePlayer(){
        return Commands.literal("yuvy-strike")
            .then(Commands.argument("player_name", StringArgumentType.string())
            .suggests((context,builder) -> {
                for(var player_list : Bukkit.getOnlinePlayers()){
                    builder.suggest(player_list.getName());
                }
                return builder.buildFuture();
            })
            .executes(context -> {
                if(context.getSource().getSender() instanceof Player p){
                    Player target = Bukkit.getPlayer(StringArgumentType.getString(context,"player_name"));
                    if(target != null){
                        for(int i = 0;i < 10;i++){
                            target.getWorld().strikeLightning(target.getLocation());
                            Location particleLocation = target.getLocation().add(0.0,target.getHeight() * 0.5,0.0);
                            target.getWorld().spawnParticle(Particle.RAID_OMEN,particleLocation,2);
                        }
                    }
                }
                return Command.SINGLE_SUCCESS;
            })
        ).build();
    }

    private com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> giveDisc() {
        return Commands.literal("give_disc")
            .then(Commands.argument("disc_index",IntegerArgumentType.integer(1,10))
            .executes(context -> {
                if(context.getSource().getSender() instanceof Player player){
                    int index = IntegerArgumentType.getInteger(context,"disc_index");
                    switch(index) {
                        case 1 -> {
                            player.getInventory().addItem(AthenaListener.createCustomDiscAboutYou());
                            player.sendMessage(
                                Component.text("[!] disc About You sudah di buat")
                            );
                        }
                        case 2 -> {
                            player.getInventory().addItem(AthenaListener.createCustomDiscHide());
                            player.sendMessage(
                                Component.text("[!] disc Hide sudah di buat")
                            );
                        }
                        case 3 -> {
                            player.getInventory().addItem(AthenaListener.createCustomDiscTheStranglers());
                            player.sendMessage(
                                Component.text("[!] disc The Stranglers sudah di buat")
                            );
                        }
                        default -> player.sendMessage(
                            Component.text("[!] Error failed to find disc")
                        );
                    }
                    player.sendMessage(
                        Component.text("[!] disc sudah di buat")
                    );
                }
                return Command.SINGLE_SUCCESS;
            })
        ).build();
    }

    private static void logTemplateAttribution() {
        // Please do *not* remove this attribution. It helps other hopeful plugin developers find this template.
        log.info("[ATTRIBUTION] Powered by the PaperMC Plugin Template created by Joseph Hale (https://jhale.dev)");
        log.info("[ATTRIBUTION] Copyright (c) 2021-2024. All rights reserved.");
        log.info("[ATTRIBUTION] Licensed under the terms of the MPL-2.0 license.");
        log.info("[ATTRIBUTION] Issues with the template can be reported at https://github.com/thehale/papermc-plugin-template/issues");
        log.info("[ATTRIBUTION]");
        log.info("[ATTRIBUTION] CREATE YOUR OWN PLUGIN: https://github.com/thehale/papermc-plugin-template");
        log.info("[ATTRIBUTION : _vinzsan] if you have suggest please poll issue in my github https://github.com/vinzsan");
    }

    private void setup() {
        getServer().getPluginManager().registerEvents(new AthenaListener(), this);
        getServer().getPluginManager().registerEvents(new ResourcePack(), this);
        new Metrics(this, BSTATS_PLUGIN_ID);  // Enable bStats metrics
    }

    @Override
    public void onDisable() {
        log.info("Thanks for using " + NAME + "!");
    }
}
