// Copyright (c) 2023 Joseph Hale
// 
// This Source Code Form is subject to the terms of the Mozilla Public
// License, v. 2.0. If a copy of the MPL was not distributed with this
// file, You can obtain one at http://mozilla.org/MPL/2.0/.

package dev._vinzsan.athena;

import java.net.URI;
import java.rmi.registry.Registry;
import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.random.RandomGenerator;

import javax.swing.text.PlainDocument;

import org.bukkit.BlockChangeDelegate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.ServerLinks;
import org.bukkit.Sound;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.DragonFireball;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.JukeboxPlayableComponent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.checkerframework.checker.units.qual.A;

import dev._vinzsan.athena.AthenaRole.PickRolesTypes;
import dev._vinzsan.athena.AthenaRole.PickSubRolesTypes;
import io.papermc.paper.connection.PlayerCommonConnection;
import io.papermc.paper.connection.PlayerGameConnection;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.JukeboxPlayable;
import io.papermc.paper.datacomponent.item.UseCooldown;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.event.player.AsyncChatEvent;
import io.papermc.paper.event.player.PlayerClientLoadedWorldEvent;
import io.papermc.paper.event.player.PlayerCustomClickEvent;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import it.unimi.dsi.fastutil.Hash;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.inventory.meta.Damageable; // BENAR

/**
 * You can add methods to this class to react to different events in the
 * Minecraft world.
 * 
 * To react to an event, add a method with the @EventHandler annotation, then
 * add a parameter to the method with the type of event you want to react to.
 * 
 * For example, if you want to react to a player joining the server, add a
 * method like this:
 * 
 * ```
 * @EventHandler
 * public void onPlayerJoin(PlayerJoinEvent event) {
 *    // Your code here
 * }
 * ```
 * 
 * Or if you want to react to a player breaking a block, add a method like this:
 * ```
 * @EventHandler
 * public void onBlockBreak(BlockBreakEvent event) {
 *   // Your code here
 * }
 * ```
 * 
 * For each type of event, you'll also have to add an `import` statement at the
 * top of this file (e.g. `import org.bukkit.event.player.PlayerJoinEvent;` or
 * `import org.bukkit.event.block.BlockBreakEvent`).
 * 
 * Check out the list of available events in the Spigot API documentation:
 * https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/event/package-summary.html
 * 
 * Learn more about event handling in the PaperMC documentation:
 * https://docs.papermc.io/paper/dev/event-listeners
 */
public class AthenaListener implements Listener {

    public static final Set<UUID> frozenPlayerList = new HashSet<>();
    public static final HashMap<UUID,Boolean> fitur = new HashMap<>();
    
    public static final NamespacedKey ORBITAL_STICK_KEY = new NamespacedKey(AthenaPlugin.instance,"orbital_stick");
    public static final NamespacedKey FIREBALL_STICK = new NamespacedKey(AthenaPlugin.instance,"fireball_stick");
    public static final NamespacedKey HEAL_STICK = new NamespacedKey(AthenaPlugin.instance,"heal_stick");
    public static final NamespacedKey ARMOR_CANCELING = new NamespacedKey(AthenaPlugin.instance,"armor_canceling");
    public static final NamespacedKey BLACK_SWORD = new NamespacedKey(AthenaPlugin.instance,"black_sword");
    public static final NamespacedKey POSE_CHANGER = new NamespacedKey(AthenaPlugin.instance,"pose_changer");
    public static final NamespacedKey NECROMANCER_STICK = new NamespacedKey(AthenaPlugin.instance,"necromancer_stick");
    public static final NamespacedKey NECROMANCER_OWNER = new NamespacedKey(AthenaPlugin.instance,"necromancer_owner");
    public static final NamespacedKey ELDER_STICK = new NamespacedKey(AthenaPlugin.instance,"elder_stick");

    public static final HashMap<UUID, Set<UUID>> necromancerMobs = new HashMap<>();
    private static final int MAX_NECROMANCER_MOBS = 3;
    
    public static final HashMap<UUID,Boolean> walkingEffectPlayerList = new HashMap<>();
    
    public static final Set<UUID> playerJoinHandlerList = new HashSet<>();
    public static final Set<UUID> playerAfkList = new HashSet<>();

    public static final HashMap<UUID,AthenaTempBan> playerTemporaryBannedList = new HashMap<>();
    
    public static final HashMap<UUID,AthenaRolesPick> ATHENA_ROLES_PLAYER_ID_LIST = new HashMap<>();

    private static final Set<String> bannedWords = Set.of(
        "Yuvy"
    );

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.joinMessage(null);
            Bukkit.getServer().broadcast(
                Component.text("[!] Player " + player.getName() + " has join the server",NamedTextColor.GOLD)
            );
        
    }
    
    public static boolean getPlayerFrooze(UUID uuid){
        return frozenPlayerList.contains(uuid);
    }

    public static boolean toggleFreeze(UUID uuid){
        if(frozenPlayerList.contains(uuid)){
            frozenPlayerList.remove(uuid);
            return false;
        } else {
            frozenPlayerList.add(uuid);
            return true;
        }
    }
    
    @EventHandler
    public void onPlayerLoadedWorld(PlayerClientLoadedWorldEvent event){
        var player = event.getPlayer();

        if(playerJoinHandlerList.contains(player.getUniqueId())) return;
        if(playerTemporaryBannedList.containsKey(player.getUniqueId())){
            var banned = playerTemporaryBannedList.getOrDefault(player.getUniqueId(), null);
            if(banned != null) banned.showBannedDialog(player);
            return;
        }
        Dialog dialog = RegistryAccess.registryAccess()
            .getRegistry(RegistryKey.DIALOG)
            .get(Key.key("athena:login_page"));

        player.showDialog(dialog);
    }

    @EventHandler 
    public void onPlayerDeath(PlayerDeathEvent event){
        if(event.getEntity() instanceof Player player){
            if(ATHENA_ROLES_PLAYER_ID_LIST.containsKey(player.getUniqueId())){
                ATHENA_ROLES_PLAYER_ID_LIST.remove(player.getUniqueId());
                AthenaPlugin.log.info("[Athena Yuvy]: player state di reset " + player.getName());
            }
        }
    }

    /*NOTE: necromancer stick */
    public static ItemStack createNecromancerStick(){
        ItemStack stick = new ItemStack(Material.BONE);
        stick.setData(DataComponentTypes.USE_COOLDOWN,UseCooldown.useCooldown(10.0f).cooldownGroup(NECROMANCER_STICK).build());
        ItemMeta meta = stick.getItemMeta();

        meta.displayName(Component.text("Necromancer Stick",NamedTextColor.DARK_GREEN,TextDecoration.UNDERLINED));
        meta.lore(List.of(
            Component.text("Klik kanan untuk memanggil mob yang setia padamu",NamedTextColor.GRAY),
            Component.text("SHIFT + Klik kanan untuk memanggil Warden (ulti)",NamedTextColor.DARK_AQUA),
            Component.text("Mob akan menyerang siapapun yang kamu hit / yang menghitmu",NamedTextColor.GRAY),
            Component.text("Memanggil mob baru akan menghapus mob lamamu",NamedTextColor.DARK_GRAY)
        ));
        meta.setEnchantmentGlintOverride(true);
        meta.getPersistentDataContainer().set(NECROMANCER_STICK,PersistentDataType.BOOLEAN,true);
        stick.setItemMeta(meta);
        return stick;
    }

    @EventHandler
    public void onNecromancerStickInteract(PlayerInteractEvent event){
        ItemStack item = event.getItem();
        if(item == null || item.getType() != Material.BONE) return;

        ItemMeta meta = item.getItemMeta();
        if(meta == null) return;

        Boolean isNecroStick = meta.getPersistentDataContainer()
            .get(NECROMANCER_STICK,PersistentDataType.BOOLEAN);
        if(isNecroStick == null || !isNecroStick) return;

        if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        event.setCancelled(true);

        Player player = event.getPlayer();

        if(player.getCooldown(NECROMANCER_STICK) > 0) return;

        UUID ownerId = player.getUniqueId();

        // Apapun trigger-nya (ulti atau biasa), mob lama punya player ini dihapus dulu
        removeOwnedMobs(ownerId);

        Set<UUID> owned = necromancerMobs.computeIfAbsent(ownerId, k -> new HashSet<>());   

        World world = player.getWorld();
        Location spawnLoc = player.getLocation()
            .add(player.getLocation().getDirection().normalize().multiply(2.0));

        if(player.isSneaking()){
            // ULTI: sneak + klik kanan -> spawn Warden
            Entity spawned = world.spawnEntity(spawnLoc, EntityType.WARDEN);
            tagAsOwnedMob(spawned, player, owned);

            world.spawnParticle(Particle.SONIC_BOOM, spawnLoc, 1);
            world.playSound(spawnLoc, Sound.ENTITY_WARDEN_EMERGE, 1.0f, 1.0f);
            player.sendMessage(Component.text("[!] Warden setia berhasil dipanggil",NamedTextColor.DARK_AQUA));

            player.setCooldown(NECROMANCER_STICK, 600);
        } else {
            EntityType[] pool = { EntityType.ZOMBIE, EntityType.SKELETON, EntityType.VINDICATOR,EntityType.ENDERMAN };
            EntityType chosen = pool[new Random().nextInt(pool.length)];

            Entity spawned = world.spawnEntity(spawnLoc, chosen);
            tagAsOwnedMob(spawned, player, owned);

            world.spawnParticle(Particle.SOUL, spawnLoc, 30, 0.5, 1.0, 0.5);
            world.playSound(spawnLoc, Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 1.0f, 0.6f);
            player.sendMessage(Component.text("[!] Mob setia berhasil dipanggil",NamedTextColor.DARK_GREEN));

            player.setCooldown(NECROMANCER_STICK, 200);
        }
    }

    private void tagAsOwnedMob(Entity spawned, Player player, Set<UUID> owned){
        if(spawned instanceof LivingEntity mob){
            mob.getPersistentDataContainer().set(NECROMANCER_OWNER,PersistentDataType.STRING, player.getUniqueId().toString());
            mob.customName(Component.text(player.getName() + "Necromancer",NamedTextColor.DARK_GREEN));
            mob.setCustomNameVisible(true);
            owned.add(mob.getUniqueId());
        }
    }

    private void removeOwnedMobs(UUID ownerId){
        Set<UUID> owned = necromancerMobs.get(ownerId);
        if(owned == null || owned.isEmpty()) return;

        for(UUID mobId : new HashSet<>(owned)){
            Entity entity = Bukkit.getEntity(mobId);
            if(entity != null && !entity.isDead()){
                entity.remove(); // langsung remove, bukan kill, biar nggak drop item/exp
            }
        }
        owned.clear();
    }

    /* NOTE: logic supaya mob necromancer nyerang siapa yang kena hit owner
       atau siapa yang ngehit owner. Ini ditambahkan di dalam onDamage yang
       sudah ada, jadi jangan buat @EventHandler baru untuk ini -- gabungkan
       dengan method onDamage yang existing supaya event yang sama tidak
       diproses dua kali. */
    private void handleNecromancerAggro(EntityDamageByEntityEvent event){
        Entity victim = event.getEntity();
        Entity rawDamager = event.getDamager();

        Player attacker = resolveAttackingPlayer(rawDamager);

        // Kasus 1: owner nge-hit entity lain -> mob owner ikut nyerang target itu
        if(attacker != null && victim instanceof LivingEntity victimLiving){
            setOwnedMobsTarget(attacker.getUniqueId(), victimLiving);
        }

        // Kasus 2: owner di-hit sesuatu -> mob owner nyerang balik yang ngehit
        if(victim instanceof Player victimPlayer && rawDamager instanceof LivingEntity attackerLiving){
            setOwnedMobsTarget(victimPlayer.getUniqueId(), attackerLiving);
        }
    }
    
    @EventHandler
    public void onNecromancerMobTarget(EntityTargetLivingEntityEvent event){
        Entity entity = event.getEntity();
        String ownerStr = entity.getPersistentDataContainer().get(NECROMANCER_OWNER,PersistentDataType.STRING);
        if(ownerStr == null) return; // bukan mob necromancer, biarkan AI normal

        LivingEntity target = event.getTarget();
        if(target == null) return; // target di-clear, biarkan aja

        UUID ownerId = UUID.fromString(ownerStr);

        // AI vanilla (nearest-target goal) nyoba nge-target owner sendiri -> blokir
        if(target.getUniqueId().equals(ownerId)){
            event.setCancelled(true);
        }
    }

    private Player resolveAttackingPlayer(Entity damager){
        if(damager instanceof Player player) return player;
        if(damager instanceof Projectile projectile && projectile.getShooter() instanceof Player shooter){
            return shooter;
        }
        return null;
    }

    private void setOwnedMobsTarget(UUID ownerId, LivingEntity target){
        Set<UUID> owned = necromancerMobs.get(ownerId);
        if(owned == null || owned.isEmpty()) return;

        for(UUID mobId : owned){
            Entity entity = Bukkit.getEntity(mobId);
            if(entity instanceof Mob mob && !mob.isDead() && !mob.getUniqueId().equals(target.getUniqueId())){
                mob.setTarget(target);
            }
        }
    }

    @EventHandler
    public void onNecromancerMobDeath(EntityDeathEvent event){
        Entity entity = event.getEntity();
        String ownerStr = entity.getPersistentDataContainer().get(NECROMANCER_OWNER,PersistentDataType.STRING);
        if(ownerStr == null) return;

        UUID ownerId = UUID.fromString(ownerStr);
        Set<UUID> owned = necromancerMobs.get(ownerId);
        if(owned != null) owned.remove(entity.getUniqueId());
    }
    /*END: necromancer stick */
    
    public static ItemStack createOrbitalStick(){
        ItemStack stick = new ItemStack(Material.WOODEN_HOE);
        ItemMeta meta = stick.getItemMeta();

        meta.displayName(Component.text("Yuvy Strike",NamedTextColor.GOLD,TextDecoration.UNDERLINED));
        meta.lore(List.of(
            Component.text("Klik untuk strike ledakan di mana saja",NamedTextColor.RED)
        ));
        meta.setEnchantmentGlintOverride(true);

        meta.getPersistentDataContainer().set(ORBITAL_STICK_KEY,PersistentDataType.BOOLEAN,true);
        stick.setItemMeta(meta);
        return stick;
    }

    public static ItemStack createFireballStick(){
        ItemStack stick = new ItemStack(Material.STICK);
        stick.setData(DataComponentTypes.USE_COOLDOWN,UseCooldown.useCooldown(5.0f).cooldownGroup(FIREBALL_STICK).build());
        ItemMeta meta = stick.getItemMeta();

        meta.displayName(Component.text("Fireball stick",NamedTextColor.YELLOW));
        meta.lore(List.of(
            Component.text("Klik untuk menembakan bola api")
        ));
        meta.getPersistentDataContainer().set(FIREBALL_STICK,PersistentDataType.BOOLEAN,true);
        stick.setItemMeta(meta);
        return stick;
    }
    
    public static ItemStack createHealStick(){
        ItemStack stick = new ItemStack(Material.WOODEN_SPEAR);
        ItemMeta meta = stick.getItemMeta();

        meta.displayName(Component.text("Heal stick",NamedTextColor.LIGHT_PURPLE));
        meta.lore(List.of(
            Component.text("Klik ke arah player atau heal diri sendiri dengah shift")
        ));
        
        meta.getPersistentDataContainer().set(HEAL_STICK,PersistentDataType.BOOLEAN,true);
        stick.setItemMeta(meta);
        return stick;
    }
    
    public static ItemStack createBlackSword() {
        ItemStack sword = new ItemStack(Material.NETHERITE_SWORD);
        sword.setData(DataComponentTypes.USE_COOLDOWN,UseCooldown.useCooldown(2.0f).cooldownGroup(BLACK_SWORD).build());
        ItemMeta meta = sword.getItemMeta();

        meta.displayName(Component.text("Black Sword",NamedTextColor.DARK_RED,TextDecoration.UNDERLINED));
        meta.lore(List.of( 
            Component.text("Serang biasa untuk fatal"),
            Component.text("SHIFT untuk ulti")
        ));

        meta.getPersistentDataContainer().set(BLACK_SWORD,PersistentDataType.BOOLEAN,true);
        sword.setItemMeta(meta);
        return sword;
    }
    
    public static ItemStack createPoseChanger(){
        ItemStack stick = new ItemStack(Material.STICK);
        ItemMeta meta = stick.getItemMeta();

        meta.displayName(Component.text("Yuvy Pose Changer",NamedTextColor.DARK_AQUA));
        meta.lore(
            List.of(
                Component.text("stick yang bisa mengubah pose player")
            )
        );
        
        meta.getPersistentDataContainer().set(POSE_CHANGER,PersistentDataType.BOOLEAN,true);
        stick.setItemMeta(meta);
        return stick;
    }
    
    public static ItemStack createElderStick(){
        ItemStack item = new ItemStack(Material.STICK);
        ItemMeta meta = item.getItemMeta();

        meta.setEnchantmentGlintOverride(true);
        meta.displayName(Component.text("Elder Stick"));
        meta.lore(
            List.of(
                Component.text("stick yang bisa menumbuhkan tumbuhan")
            )
        );
        
        meta.getPersistentDataContainer().set(ELDER_STICK,PersistentDataType.BOOLEAN,true);
        item.setItemMeta(meta);
        return item;
    }
    
    public static ItemStack createArmor(){
        ItemStack shield = new ItemStack(Material.GOLDEN_HELMET);
        ItemMeta meta = shield.getItemMeta();

        meta.displayName(Component.text("Yuvy Shield",NamedTextColor.AQUA));
        meta.lore(List.of(
            Component.text("Hold untuk berlindung",NamedTextColor.LIGHT_PURPLE)
        ));
        meta.getPersistentDataContainer().set(ARMOR_CANCELING,PersistentDataType.BOOLEAN,true);
        shield.setItemMeta(meta);
        return shield;
    }

    public static ItemStack createCustomDiscAboutYou(){
        ItemStack item = new ItemStack(Material.MUSIC_DISC_CREATOR);
        ItemMeta meta = item.getItemMeta();

        JukeboxPlayableComponent jukebox = meta.getJukeboxPlayable();
        jukebox.setSongKey(new NamespacedKey("athena","about_you"));
        meta.setJukeboxPlayable(jukebox);

        meta.displayName(Component.text("About You - The 1975",NamedTextColor.DARK_PURPLE));
        meta.lore(List.of( 
            Component.text("I Know Place.. Somewhere i go when i need to remembe your face..")
        ));

        item.setItemMeta(meta);
        return item;
    }
    
    public  static ItemStack createCustomDiscHide(){
        ItemStack item = new ItemStack(Material.MUSIC_DISC_11);
        ItemMeta meta = item.getItemMeta();

        JukeboxPlayableComponent jukebox = meta.getJukeboxPlayable();
        jukebox.setSongKey(new NamespacedKey("athena","hide"));
        meta.setJukeboxPlayable(jukebox);

        meta.displayName(Component.text("Hide - CS01",NamedTextColor.DARK_PURPLE));
        meta.lore(List.of( 
            Component.text("The Nothing")
        ));

        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createCustomDiscTheStranglers(){
        ItemStack item = new ItemStack(Material.MUSIC_DISC_PIGSTEP);
        ItemMeta meta = item.getItemMeta();

        JukeboxPlayableComponent jukebox = meta.getJukeboxPlayable();
        jukebox.setSongKey(new NamespacedKey("athena","the_stranglers"));
        meta.setJukeboxPlayable(jukebox);

        meta.displayName(Component.text("The Stranglers - Golden Browns",NamedTextColor.DARK_PURPLE));
        meta.lore(List.of( 
            Component.text("Never, Never, Never Frown")
        ));

        item.setItemMeta(meta);
        return item;
    }

    private BukkitTask startElderGenerateParticle(World world,Location loc){
        return new BukkitRunnable() {
            double radius = 2.0;
            @Override 
            public void run(){
                if(radius >= 10.0){
                    cancel();
                    return;
                }
                
                for(double i = 0.0;i < Math.PI * 2;i += 0.2f){
                    double x = radius * Math.cos(i);
                    double z = radius * Math.sin(i);
                    Location paritcleSpawn = loc.clone().add(x,1.0,z);
                    world.spawnParticle(Particle.PALE_OAK_LEAVES,paritcleSpawn,5);
                }
                
                radius += 0.5;
            }
        }.runTaskTimer(AthenaPlugin.instance,0L,20L);
    }
    
    @EventHandler 
    public void onElderStickClick(PlayerInteractEvent event){
        ItemStack item = event.getItem();
        if(item == null || item.getType() != Material.STICK) return;

        ItemMeta meta = item.getItemMeta();
        if(meta == null) return;

        Boolean elderBoolean = meta.getPersistentDataContainer()
            .get(ELDER_STICK,PersistentDataType.BOOLEAN);

        if(elderBoolean == null || !elderBoolean) return;
        
        Player player = event.getPlayer();
        World world = player.getWorld();

        Random rand = new Random();

        if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK){
            if(player.isSneaking()){
                var entityList = player.getTargetEntity(20);
                if(entityList == null){
                    var block = player.getTargetBlockExact(20);
                    world.generateTree(block.getLocation(),rand,TreeType.RED_MUSHROOM);
                    startElderGenerateParticle(world,block.getLocation());
                } else {
                    world.generateTree(entityList.getLocation(), rand,TreeType.PALE_OAK_CREAKING);
                    startElderGenerateParticle(world,entityList.getLocation());
                    
                    Location playerLocation = entityList.getLocation().clone().add(0,player.getHeight(),0);
                    double radius = 2.0;
                    for(double angle = 0.0;angle <= Math.PI * 2;angle += 0.2){
                        double x = radius * Math.cos(angle);
                        double z = radius * Math.sin(angle);
                        
                        Location particleLocation = playerLocation.clone().add(x,0,z);
                        world.spawnParticle(Particle.SMOKE,particleLocation,2);
                        radius += 1.0;
                    }
                }
            }
        }
    }
    
    @EventHandler 
    public void onStickPoseChangerClick(PlayerInteractEvent event){
        ItemStack item = event.getItem();
        if(item == null || item.getType() != Material.STICK) return;
        
        ItemMeta meta = item.getItemMeta();
        if(meta == null) return;
        
        Boolean stickBoolean = meta.getPersistentDataContainer()
            .get(POSE_CHANGER,PersistentDataType.BOOLEAN);

        if(stickBoolean == null || !stickBoolean) return;

        Player player = event.getPlayer();
        Entity entity = player.getTargetEntity(10);

        if(!(entity instanceof Player p)) return;
        
        if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK){
            if(player.isSneaking()){
                p.setPose(Pose.SLEEPING);
            } else {
                p.setPose(Pose.SITTING);
            }
        } else if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK){
            if(player.isSneaking()){
                p.setPose(Pose.SNEAKING);
            } else {
                p.setPose(Pose.DYING);
            }
        }
    }

    @EventHandler
    public void onBlakckSwordInteract(PlayerInteractEvent event){
        ItemStack item = event.getItem();
        if(item == null || item.getType() != Material.NETHERITE_SWORD) return;

        ItemMeta meta = item.getItemMeta();
        if(meta == null) return;

        Boolean swordBoolean = meta.getPersistentDataContainer()
            .get(BLACK_SWORD,PersistentDataType.BOOLEAN);

        if(swordBoolean == null || !swordBoolean) return;

        Player player = event.getPlayer();
        World world = player.getWorld();

        if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK){
            if(player.getCooldown(BLACK_SWORD) >= 2.0f){
                return;
            }
            if(player.isSneaking()){
                var listEntity = player.getTargetEntity(10);
                if(listEntity instanceof LivingEntity entity){
                    world.playSound(player.getLocation(),Sound.ENTITY_ALLAY_HURT,1.0f,1.0f);
                    Location tpLocation = entity.getLocation().clone();

                    Vector newLocation = tpLocation.getDirection().normalize().multiply(2.0f);
                    Location tpLocation2 = tpLocation.clone().subtract(newLocation);
                    tpLocation2.setY(tpLocation.getY());

                    world.spawnParticle(Particle.SWEEP_ATTACK, tpLocation2,5);
                    world.spawnParticle(Particle.RAID_OMEN, tpLocation2, 5);
                    entity.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS,100,255,false,false,false));

                    Vector lineDirection = tpLocation2.toVector().subtract(player.getEyeLocation().toVector()).normalize();
                    double distance = player.getEyeLocation().distance(tpLocation2);
                            
                    for (double i = 0; i < distance; i += 0.5) { 
                        Location point = player.getEyeLocation().clone().add(lineDirection.clone().multiply(i));
                        world.spawnParticle(Particle.SWEEP_ATTACK, point, 2);
                    }
                    
                    player.teleport(tpLocation2);
                    player.setCooldown(BLACK_SWORD,40);
                }
            } else {
                Entity entity = player.getTargetEntity(5);
                if(entity instanceof LivingEntity damageable){
                    world.spawnParticle(Particle.CLOUD,entity.getLocation(),5);
                    player.playSound(player.getLocation(),Sound.ENTITY_PLAYER_ATTACK_SWEEP,0.5f,1.0f);
                    damageable.damage(5);
                }
            }
            if(meta instanceof Damageable damage){
             int currentDamage = damage.getDamage();
             int maxDurability = player.getInventory().getItemInMainHand().getType().getMaxDurability();
             int new_damage = currentDamage + 2;
             if(new_damage >= maxDurability){
                 player.getInventory().setItemInMainHand(null);
                 world.playSound(player.getLocation(),Sound.ENTITY_ITEM_BREAK,1.0f,1.0f);
                 world.spawnParticle(Particle.ENCHANT,player.getLocation(),100);
             } else {
                 damage.setDamage(new_damage);
                 player.getInventory().getItemInMainHand().setItemMeta(meta);
                 player.swingMainHand();
             }
        }
        }
    }

    @EventHandler
    public void onHealStickInteract(PlayerInteractEvent event){
        ItemStack item = event.getItem();
        if(item == null || item.getType() != Material.WOODEN_SPEAR) return;

        ItemMeta meta = item.getItemMeta();
        if(meta == null) return;

        Boolean healBoolean = meta.getPersistentDataContainer()
            .get(HEAL_STICK,PersistentDataType.BOOLEAN);

        if(healBoolean == null || !healBoolean) return;
        
        Player player = event.getPlayer();
        World world = player.getWorld();
        
        event.setCancelled(true);
        
        if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK){
            if(player.isSneaking()){
                var playerList = Bukkit.getOnlinePlayers();
                for(var playerHealable : playerList){
                    if(playerHealable.getLocation().distanceSquared(player.getLocation()) <= 10000.0f){
                        playerHealable.addPotionEffect(
                            new PotionEffect(
                                PotionEffectType.REGENERATION,
                                100,
                                1
                            )
                        );
                        playerHealable.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME,playerHealable.getLocation(),50);
                        playerHealable.playSound(playerHealable.getLocation(),Sound.ENTITY_VEX_AMBIENT,1.0f,1.0f);
                        Location particleLoc = player.getLocation().add(0.0,player.getHeight() + 100.0,0.0);

                        for(double y = player.getHeight() + 100.0;y >= player.getHeight();y -= 0.2){
                            Location particleDy = particleLoc.clone().subtract(0.0,y,0.0);
                            world.spawnParticle(Particle.RAID_OMEN,particleDy,5);
                        }
                    }
                }
            } else {
                player.addPotionEffect(
                    new PotionEffect(
                        PotionEffectType.REGENERATION,
                        100,
                        3
                    )
                );
                player.getWorld().playSound(player.getLocation(),Sound.ENTITY_ALLAY_DEATH,1.0f,1.0f);
                player.getWorld().spawnParticle(Particle.HEART,player.getLocation(),10);
            }
        }
        
        if(meta instanceof Damageable damage){
             int currentDamage = damage.getDamage();
             int maxDurability = player.getInventory().getItemInMainHand().getType().getMaxDurability();
             int new_damage = currentDamage + 2;
             if(new_damage >= maxDurability){
                 player.getInventory().setItemInMainHand(null);
                 world.playSound(player.getLocation(),Sound.ENTITY_ITEM_BREAK,1.0f,1.0f);
                 world.spawnParticle(Particle.ENCHANT,player.getLocation(),100);
             } else {
                 damage.setDamage(new_damage);
                 player.getInventory().getItemInMainHand().setItemMeta(meta);
             }
        }
    }

    @EventHandler
    public void onFireballStickInteract(PlayerInteractEvent event){
        ItemStack item = event.getItem();
        if(item == null || item.getType() != Material.STICK) return;

        ItemMeta meta = item.getItemMeta();
        if(meta == null) return;

        Boolean firebalBoolean = meta.getPersistentDataContainer()
            .get(FIREBALL_STICK,PersistentDataType.BOOLEAN);

        if(firebalBoolean == null || !firebalBoolean) return;

        Player player = event.getPlayer();
        World world = player.getWorld();
        
        event.setCancelled(true);

        if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK){
            if(player.getCooldown(FIREBALL_STICK) > 0){
                return;
            }
            player.playSound(player.getLocation(),Sound.BLOCK_LAVA_EXTINGUISH,1.0f,1.0f);
            Location spawn = player.getEyeLocation()
                .add(player.getLocation().getDirection().clone().multiply(2.0f));
                
            if(player.isSneaking()){
                world.playSound(player.getLocation(),Sound.ENTITY_EVOKER_PREPARE_ATTACK,1.0f,1.0f);
                world.spawnParticle(Particle.PORTAL,player.getLocation(),10);
                WitherSkull wither = world.spawn(spawn,WitherSkull.class);
                wither.setYield(5.0f);
                player.setPose(Pose.SWIMMING);
                
                wither.setVelocity(player.getLocation().getDirection().multiply(1.5f));
            } else {
                player.getWorld().spawnParticle(Particle.CLOUD,spawn,10);

                Fireball fb = world.spawn(spawn,Fireball.class);
                fb.setYield(5.0f);

                fb.setVelocity(player.getLocation().getDirection().multiply(1.5f));
            }
            
            player.setCooldown(FIREBALL_STICK,60);
        }
    }

    @EventHandler
    public void onOrbitalStickInteract(PlayerInteractEvent event){
        ItemStack item = event.getItem();
        if(item == null || item.getType() != Material.WOODEN_HOE) return;

        ItemMeta meta = item.getItemMeta();
        if(meta == null) return;

        Boolean isOrbitalStick = meta.getPersistentDataContainer()
            .get(ORBITAL_STICK_KEY,PersistentDataType.BOOLEAN);

        if(isOrbitalStick == null || !isOrbitalStick) return;

        Player player = event.getPlayer();
        World world = player.getWorld();

        event.setCancelled(true);

        if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK){
            player.playSound(player.getLocation(),Sound.ENTITY_ITEM_BREAK,1.0f,1.0f);

            player.heal(255.0);

            Vector direction = player.getLocation().getDirection();
            var targetBlock = player.getTargetBlockExact(10);
            
            if(player.isSneaking()){
                Entity entityTarget = player.getTargetEntity(50);
                if(entityTarget != null){
                    world.playSound(player.getLocation(),Sound.ENTITY_EVOKER_PREPARE_SUMMON,1.0f,1.0f);
                    world.playSound(entityTarget.getLocation(),Sound.ENTITY_EVOKER_PREPARE_ATTACK,1.0f,1.0f);

                    world.createExplosion(entityTarget.getLocation(),6.0f,false,true);
                    world.strikeLightning(entityTarget.getLocation());
                    world.spawnParticle(Particle.ENCHANT,entityTarget.getLocation(),100);

                    Location startLoc = player.getEyeLocation();
                    Location endLoc = entityTarget.getLocation();
                    Vector lineDirection = endLoc.toVector().subtract(startLoc.toVector()).normalize();
                    double distance = startLoc.distance(endLoc);
                            
                    for (double i = 0; i < distance; i += 0.5) { 
                        Location point = startLoc.clone().add(lineDirection.clone().multiply(i));
                        world.spawnParticle(Particle.ENCHANT, point, 5);
                        world.spawnParticle(Particle.ELECTRIC_SPARK, point, 5);
                    }
                } else {
                    world.spawnParticle(Particle.CLOUD,player.getLocation(),10);
                    return;
                }
            } else if(targetBlock != null){
                player.getWorld().spawnParticle(Particle.ENCHANT,player.getLocation(),100);
                world.createExplosion(targetBlock.getLocation(), 5.0f,false,true);
            }
            else {
                Location targetExplosion = player.getEyeLocation().add(direction.normalize().multiply(5.0f));
                world.createExplosion(targetExplosion, 5.0f,false,true);
                world.spawnParticle(Particle.CLOUD,targetExplosion,100);
            }

            if(meta instanceof Damageable damage){
                int currentDamage = damage.getDamage();
                int maxDurability = player.getInventory().getItemInMainHand().getType().getMaxDurability();
                int new_damage = currentDamage + 1;

                if(new_damage >= maxDurability){
                    player.getInventory().setItemInMainHand(null);
                    world.playSound(player.getLocation(),Sound.ENTITY_ITEM_BREAK,1.0f,1.0f);
                    world.spawnParticle(Particle.ENCHANT,player.getLocation(),100);
                } else {
                    damage.setDamage(new_damage);
                    player.getInventory().getItemInMainHand().setItemMeta(meta);
                }
            }
        }
    }

    /* NOTE: AthenaPickRole */
    
    public Dialog buildSubRoleDialog(){
        return Dialog.create(builder -> builder.empty()
            .base(DialogBase.builder(Component.text("Pick Sub Roles"))
            .externalTitle(Component.text("Pilih sub role kamu, (tidak wajib untuk mengikuti alur cerita)",NamedTextColor.GRAY))
            .build())
            .type(DialogType.multiAction(
                List.of(
                    ActionButton.builder(Component.text("Istana"))
                        .action(DialogAction.customClick(Key.key("athena:subrole_aulicus"), null))
                        .tooltip(Component.text("Kamu akan menjadi pelayan di istana"))
                        .build(),
                    ActionButton.builder(Component.text("Bandit"))
                        .action(DialogAction.customClick(Key.key("athena:subrole_latro"), null))
                        .tooltip(Component.text("Kamu akan menjadi bandit liar"))
                        .build(),
                    ActionButton.builder(Component.text("Merchant"))
                        .action(DialogAction.customClick(Key.key("athena:subrole_merchant"), null))
                        .tooltip(Component.text("Kamu akan menjadi Merchant"))
                        .build(),
                    ActionButton.builder(Component.text("Thief"))
                        .action(DialogAction.customClick(Key.key("athena:subrole_fur"), null))
                        .tooltip(Component.text("Kamu akan menjadi pencuri"))
                        .build(),
                    ActionButton.builder(Component.text("Assassin"))
                        .action(DialogAction.customClick(Key.key("athena:subrole_sicarius"), null))
                        .tooltip(Component.text("Kamu akan menjadi Assassin"))
                        .build(),
                    ActionButton.builder(Component.text("Spy"))
                        .action(DialogAction.customClick(Key.key("athena:subrole_speculator"), null))
                        .tooltip(Component.text("Kamu akan menjadi mata mata"))
                        .build()
                ),null,2))
        );
    }
    
    @EventHandler 
    public void onBannedInterfaceClick(PlayerCustomClickEvent event){
        if(event.getCommonConnection() instanceof PlayerGameConnection conn){
            Player player = conn.getPlayer();
            if(!playerTemporaryBannedList.containsKey(player.getUniqueId())){
                return;
            }
            Key identifier = event.getIdentifier();
            if(identifier.equals(Key.key("athena:banned_acc"))){
                player.getConnection().disconnect(Component.text("Sorry"));
            }
        }
    }

    @EventHandler 
    public void onPickRolesClick(PlayerCustomClickEvent event){
        if(event.getCommonConnection() instanceof PlayerGameConnection conn){
            Player player = conn.getPlayer();
            UUID playerId = player.getUniqueId();
            Bukkit.getScheduler().runTaskLater(AthenaPlugin.instance,
                () -> {
                    Key identifier = event.getIdentifier();

                    PickRolesTypes role;

                    if(identifier.equals(Key.key("athena:role_adventure"))){
                        role = PickRolesTypes.ADVENTURE;
                    } else if(identifier.equals(Key.key("athena:role_mage"))){
                        role = PickRolesTypes.MAGE;
                    } else if(identifier.equals(Key.key("athena:role_knight"))){
                        role = PickRolesTypes.KNIGHT;
                    } else if(identifier.equals(Key.key("athena:role_archery"))){
                        role = PickRolesTypes.ARCHERY;
                    } else {
                        return;
                    }

                    AthenaRolesPick playerInstance = ATHENA_ROLES_PLAYER_ID_LIST.getOrDefault(playerId,null);
                    if(playerInstance != null && playerInstance.isAlreadyGetRole()){
                        player.sendMessage(
                            Component.text("[!] kamu sudah assign role sebelumnya",NamedTextColor.RED)
                        );
                        player.getWorld().playSound(player.getLocation(),Sound.ENTITY_PLAYER_HURT_FREEZE,1.0f,1.0f);
                        return;
                    }
                    
                    ATHENA_ROLES_PLAYER_ID_LIST.put(playerId,new AthenaRolesPick(role, null, false));
                    player.sendMessage(
                        Component.text("[!] role kamu adalah " + role.label)
                    );
                    
                    switch(role){
                        case ADVENTURE -> player.getInventory().addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE));
                        case MAGE -> player.getInventory().addItem(new ItemStack(createFireballStick()));
                        case KNIGHT -> player.getInventory().addItem(new ItemStack(createBlackSword()));
                        case ARCHERY -> {
                            ItemStack bow = new ItemStack(Material.BOW);
                            ItemMeta attr = bow.getItemMeta();
                            
                            attr.addEnchant(Enchantment.POWER, 3, true);
                            attr.addEnchant(Enchantment.INFINITY,1, true);
                            bow.setItemMeta(attr);
                            player.getInventory().addItem(bow);
                        }
                    }
                    player.getWorld().playSound(player.getLocation(),Sound.ENTITY_PLAYER_LEVELUP,1.0f,1.0f);                    
                    player.showDialog(buildSubRoleDialog());
                },2L
            );
        }
    }
    
    @EventHandler 
    public void onPickSubRolesClick(PlayerCustomClickEvent event){
        if(!(event.getCommonConnection() instanceof PlayerGameConnection connection)) return;

        Player player = connection.getPlayer();
        UUID playerId = player.getUniqueId();

        Bukkit.getScheduler().runTaskLater(AthenaPlugin.instance,() -> {
            AthenaRolesPick existing = ATHENA_ROLES_PLAYER_ID_LIST.getOrDefault(playerId, null);
            if(existing == null || existing.isAlreadyGetRole()) return;

            if(existing.getSubRoles() != null) {
                player.sendMessage(
                    Component.text("[!] kamu sudah assign sub role sebelumnya")
                );
                return;
            }

            Key identifier = event.getIdentifier();
            PickSubRolesTypes subrole;

            if(identifier.equals(Key.key("athena:subrole_aulicus"))){
                subrole = PickSubRolesTypes.AULICUS;
            } else if(identifier.equals(Key.key("athena:subrole_latro"))){
                subrole = PickSubRolesTypes.LATRO;
            } else if(identifier.equals(Key.key("athena:subrole_merchant"))){
                subrole = PickSubRolesTypes.MERCATOR;
            } else if(identifier.equals(Key.key("athena:subrole_fur"))){
                subrole = PickSubRolesTypes.FUR;
            } else if(identifier.equals(Key.key("athena:subrole_sicarius"))){
                subrole = PickSubRolesTypes.SICARIUS;
            } else if(identifier.equals(Key.key("athena:subrole_speculator"))){
                subrole = PickSubRolesTypes.SPECULATOR;
            } else {
                return;
            }

            player.sendMessage(
                Component.text("[!] sub-role kamu adalah " + subrole)
            );
            ATHENA_ROLES_PLAYER_ID_LIST.put(playerId,new AthenaRolesPick(existing.roles,subrole,true));
        },2L);
    }

    @EventHandler 
    public void onLoginPageDialogClick(PlayerCustomClickEvent event){
        if(!(event.getCommonConnection() instanceof PlayerGameConnection conn)){
            return;
        }

        var player = conn.getPlayer();
        var playerId = player.getUniqueId();

        Key identifier = event.getIdentifier();
        if(identifier.equals(Key.key("athena:accept_rules"))){
            player.sendMessage(
                Component.text("Terima kasih sudah mematuhi pedoman yang ada",NamedTextColor.GOLD)
            );
            playerJoinHandlerList.add(playerId);

            player.showTitle(
                Title.title(
                    Component.text("Traveler",NamedTextColor.GOLD,TextDecoration.BOLD),
                    Component.empty(),
                    Title.Times.times(Duration.ofMillis(100),Duration.ofSeconds(3),Duration.ofMillis(500))
                )
            );
            
            Bukkit.getScheduler().runTaskLater(AthenaPlugin.instance,() -> {
                player.showTitle(
                    Title.title(
                        Component.empty(),
                        Component.text("Welcome to Centuria.", NamedTextColor.YELLOW),
                        Title.Times.times(Duration.ofMillis(100), Duration.ofSeconds(3), Duration.ofMillis(500))
                    )
                );
            },50L);
        
            Bukkit.getScheduler().runTaskLater(AthenaPlugin.instance,() -> {
                player.showTitle(
                    Title.title(
                        Component.text("Ad Astra Centuria", NamedTextColor.GOLD, TextDecoration.BOLD),
                        Component.text("May Winds Guide You", NamedTextColor.AQUA,TextDecoration.ITALIC),
                        Title.Times.times(Duration.ofMillis(100), Duration.ofSeconds(3), Duration.ofMillis(500))
                    )
                );
            },100L);
            player.sendMessage(
                Component.text("[?] Jika butuh bantuan, silahkan kirim chat ke Server Discord kami",NamedTextColor.GREEN,TextDecoration.UNDERLINED)
                .append(Component.newline())
                .append(Component.text("Plugins Server Created By Yuvy(Alter)",NamedTextColor.GOLD))
            );
            var playerLocation = player.getLocation();
            player.playSound(playerLocation,Sound.UI_TOAST_CHALLENGE_COMPLETE,1.0f,1.0f);
            playerLocation.getWorld().spawnParticle(Particle.PORTAL, playerLocation,150,0.5,1.0,0.5,0.5);

            Location particleLocation = playerLocation.clone().add(0,player.getHeight() * 0.5,0);
            double radius = 2.0;

            for(double angle = 0.0;angle <= Math.PI * 2;angle += 0.2){
                double x = radius * Math.cos(angle);
                double z = radius * Math.sin(angle);
                radius += 0.2;

                Location spawnLocation = particleLocation.clone().add(x,0.0,z);
                player.getWorld().spawnParticle(Particle.PALE_OAK_LEAVES,spawnLocation,2);
            }
            
            Location particleFlower = playerLocation.clone().add(0,player.getHeight(),0);
            for(double i = player.getHeight();i < player.getHeight() + 100;i++){
                Location newFlowerLocation = particleFlower.clone().add(0,i,0);
                player.getWorld().spawnParticle(Particle.PALE_OAK_LEAVES, newFlowerLocation, 2);
            }
            
            player.getWorld().strikeLightningEffect(player.getLocation());
        } 

        if(identifier.equals(Key.key("athena:deny_rules"))){
            player.sendMessage(
                Component.text("Yaudah si")
            );
            if(playerJoinHandlerList.contains(playerId)){
                playerJoinHandlerList.remove(playerId);
            }
            Bukkit.getServer().broadcast(
                Component.text("[!] " + player.getName() + " tidak mematuhi rules",NamedTextColor.RED)
            );
            player.getConnection().disconnect(Component.text("Kamu tidak mematuhi rules server",NamedTextColor.RED));
        }
    }
    
    @EventHandler
    public void onDialogClick(PlayerCustomClickEvent event){
        if(!(event.getCommonConnection() instanceof PlayerGameConnection conn)){
            return;
        }

        var player = conn.getPlayer();
        Key identifier = event.getIdentifier();
        World world = Bukkit.getWorlds().get(0);
        
        if(identifier.equals(Key.key("athena:toggle_fitur_broadcast"))){
            if(fitur.get(player.getUniqueId())){
                fitur.put(player.getUniqueId(),false);
                player.sendMessage(
                    Component.text("[!] kamu menonaktifkan toggle broadcast")
                );
            } else {
                fitur.put(player.getUniqueId(),true);
                player.sendMessage(
                    Component.text("[!] kamu mengaktifkan toggle broadcast")
                );
            }
        }
        
        if(identifier.equals(Key.key("athena:toggle_teleport"))){
            if(player.isOnline()){
                Location spawnLocation = world.getSpawnLocation();
                player.teleport(spawnLocation);
                player.playSound(player.getLocation(),Sound.ENTITY_PLAYER_LEVELUP,1.0f,1.0f);
            }
        }

        if(identifier.equals(Key.key("athena:toggle_self_kill"))){
            if(player.isOnline()){
                player.kill();
                Bukkit.getServer().broadcast(
                    Component.text("[!] player " + player.getName() + " sudah capek")
                );
            }
        }

        if(identifier.equals(Key.key("athena:enable_walking"))){
            if(player.isOnline()){
                if(!walkingEffectPlayerList.containsKey(player.getUniqueId())){
                    walkingEffectPlayerList.put(player.getUniqueId(),true);
                    player.sendMessage(
                        Component.text("[!] walking effect enable",NamedTextColor.AQUA)
                    );
                } else {
                    player.sendMessage(
                        Component.text("[!] walking effect disable")
                    );
                    walkingEffectPlayerList.remove(player.getUniqueId());
                }
            }
        }
        
        if(identifier.equals(Key.key("athena:afk"))){
            if(player.isOnline()){
                if(!playerAfkList.contains(player.getUniqueId())){
                    playerAfkList.add(player.getUniqueId());
                    player.sendMessage(
                        Component.text("[!] afk mode di aktifkan, kamu tidak bisa bergerak maupun terkena hit")
                    );
                } else {
                    playerAfkList.remove(player.getUniqueId());
                    player.sendMessage(
                        Component.text("[!] afk mode telah di matikan")
                    );
                }
            }
        }
        
        if(identifier.equals(Key.key("athena:disconnect"))){
            if(player.isOnline()){
                player.getConnection().disconnect(Component.text("Kamu disconnect dari server",NamedTextColor.YELLOW));
            }
        }
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        Player player = event.getPlayer();
        if(getPlayerFrooze(player.getUniqueId()) || playerAfkList.contains(player.getUniqueId())) {
            if(event.getFrom().getX() != event.getTo().getX()
                || event.getFrom().getY() != event.getTo().getY()
                || event.getFrom().getZ() != event.getTo().getZ()) {
                    event.setCancelled(true);
                }
        }
        
        boolean playerPermission = walkingEffectPlayerList.containsKey(player.getUniqueId());
        if(playerPermission){
            World world = player.getWorld();
            Location loc = player.getLocation();
            var playerFacing = player.getEyeLocation().getDirection();

            Location positonParticle = loc.clone().subtract(playerFacing.multiply(1.5f));

            world.spawnParticle(Particle.CHERRY_LEAVES,positonParticle,1);
            world.spawnParticle(Particle.ELECTRIC_SPARK,positonParticle,1);
        }
    }
    
    /*NOTE: fungsi chat handler */

    private boolean containsBadWords(String msg){
        String[] words = msg.toLowerCase().split("\\s+");
        for(String word : words){
            if(bannedWords.contains(word)){
                return true;
            }
        }
        return false;
    }
    
    @EventHandler
    public void onPlayerChat(AsyncChatEvent event){
        if(getPlayerFrooze(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
        
        if(containsBadWords(PlainTextComponentSerializer.plainText().serialize(event.message()))){
            event.setCancelled(true);
            event.getPlayer().sendMessage(
                Component.text("[!] tidak boleh menggunakan kata kata kasar, tolong patuhi rules.",NamedTextColor.RED,TextDecoration.BOLD)
            );
            if(event.getPlayer() instanceof LivingEntity entity){
                entity.damage(10);
                entity.addPotionEffect(
                    new PotionEffect(PotionEffectType.BLINDNESS,120,255,false,true)
                );
            }
            event.getPlayer().playSound(event.getPlayer().getLocation(),Sound.ENTITY_ALLAY_DEATH,1.0f,1.0f);
            Bukkit.getServer().broadcast(Component.text("[!] " + event.getPlayer().getName() + " berkata kasar",NamedTextColor.RED));
            return;
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        if(getPlayerFrooze(event.getPlayer().getUniqueId()) || playerAfkList.contains(event.getPlayer().getUniqueId())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Player player){
            if(getPlayerFrooze(player.getUniqueId()) || playerAfkList.contains(player.getUniqueId())){
                event.setCancelled(true);
                return;
            }
        }

        handleNecromancerAggro(event);
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        walkingEffectPlayerList.remove(event.getPlayer().getUniqueId());
        if(playerJoinHandlerList.contains(event.getPlayer().getUniqueId())){
            playerJoinHandlerList.remove(event.getPlayer().getUniqueId());
        }
    }
}
