package dev._vinzsan.athena;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.keys.DialogKeys;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.keys.SoundEventKeys;
import io.papermc.paper.registry.keys.tags.DialogTagKeys;
import io.papermc.paper.registry.TypedKey;
import net.kyori.adventure.text.Component;
import org.bukkit.JukeboxSong;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Jukebox;
import org.bukkit.inventory.ItemStack;

import java.util.List;
public class AthenaBootstrapper implements PluginBootstrap {
    
    private static final TypedKey<Dialog> ATHENA_MAIN_MENU_KEY = DialogKeys.create(Key.key("athena:main_menu"));
    private static final TypedKey<Dialog> ATHENA_LOGIN_MENU_KEY = DialogKeys.create(Key.key("athena:login_page"));
    private static final TypedKey<Dialog> ATHENA_CHOOSE_ROLE = DialogKeys.create(Key.key("athena:choose_role"));
    private static final TypedKey<Dialog> ATHENA_DEBUG_SCREEN = DialogKeys.create(Key.key("athena:debug_screen"));

    private static final TypedKey<Dialog> ATHENA_CHOOSE_SUB_ROLE = DialogKeys.create(Key.key("athena:choose_sub_roles"));
    
    private static final TypedKey<Dialog> ATHENA_BANNED_INTERFACE = DialogKeys.create(Key.key("athena:banned_player"));

    @Override
    public void bootstrap(BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(RegistryEvents.DIALOG.compose()
            .newHandler(event -> event.registry().register(
                ATHENA_MAIN_MENU_KEY,
                builder -> builder
                    .base(DialogBase.builder(Component.text("Athena", NamedTextColor.GOLD))
                        .externalTitle(Component.text("Athena Settings"))
                        .body(List.of(
                            DialogBody.plainMessage(
                                Component.text("Server Settings & Utilities", NamedTextColor.GRAY, TextDecoration.ITALIC)
                            )
                        ))
                        .build()
                    )
                    .type(DialogType.multiAction(
                        List.of(
                            ActionButton.builder(Component.text("Broadcast Message", NamedTextColor.GREEN))
                                .tooltip(Component.text("Klik untuk aktif atau menonaktifkan pesan broadcast player join",NamedTextColor.GOLD))
                                .action(DialogAction.customClick(Key.key("athena:toggle_fitur_broadcast"), null))
                                .build(),
                            ActionButton.builder(Component.text("Teleport Lobby"))
                                .tooltip(Component.text("Klik untuk kembali ke lobby",NamedTextColor.GOLD))
                                .action(DialogAction.customClick(Key.key("athena:toggle_teleport"), null))
                                .build(),
                            ActionButton.builder(Component.text("Bundir Capek"))
                                .tooltip(Component.text("Klik kalau kamu capek main",NamedTextColor.RED))
                                .action(DialogAction.customClick(Key.key("athena:toggle_self_kill"), null))
                                .build(),
                            ActionButton.builder(Component.text("Walking Effect"))
                                .tooltip(Component.text("Aktifkan walking effect partikel (sedikit overhead)"))
                                .action(DialogAction.customClick(Key.key("athena:enable_walking"), null))
                                .build(),
                            ActionButton.builder(Component.text("AFK Mode"))
                                .tooltip(Component.text("Kalau kamu ingin afk di lobby tolong pakai ini"))
                                .action(DialogAction.customClick(Key.key("athena:afk"), null))
                                .build(),
                            ActionButton.builder(Component.text("Disconnect Server"))
                                .tooltip(Component.text("Hati hati untuk disconnect"))
                                .action(DialogAction.customClick(Key.key("athena:disconnect"), null))
                                .build()
                        ),
                        null,
                        3   
                    ))
            )));

        
            
        context.getLifecycleManager().registerEventHandler(RegistryEvents.DIALOG.compose()
                .newHandler(event -> event.registry().register(
                    ATHENA_LOGIN_MENU_KEY,
                    builder -> builder
                        .base(DialogBase.builder(Component.text("Minecraft AAC Server Rules",NamedTextColor.GREEN))
                        .canCloseWithEscape(false)
                        .externalTitle(Component.text("Rules of Term"))
                        .body(List.of(
                            DialogBody.plainMessage(
                                Component.text("Peraturan untuk server ini (guide)",NamedTextColor.GRAY,TextDecoration.ITALIC)
                            ),
                            DialogBody.plainMessage(
                                Component.text("Tidak boleh griefing")
                            ),
                            DialogBody.plainMessage(
                                Component.text("Tidak boleh melakukan kekerasan dalam bentuk apapun (SARA / Rasis / Superior)")
                            ),
                            DialogBody.plainMessage(
                                Component.text("Semua player di dalam server berstatus sama dan seimbang.",NamedTextColor.GREEN)
                            ),
                            DialogBody.plainMessage(
                                Component.text("Mari buat server ini terjaga dan menyenangkan untuk pihak manapun",NamedTextColor.AQUA)
                            )
                        ))
                        .build()
                    )
                    .type(DialogType.multiAction(
                        List.of(
                            ActionButton.builder(Component.text("Terima",NamedTextColor.GREEN))
                                .tooltip(Component.text("Dengan ini kamu mematuhi seluruh peraturan yang ada"))
                                .action(DialogAction.customClick(Key.key("athena:accept_rules"), null))
                                .build(),
                            ActionButton.builder(Component.text("Tolak",NamedTextColor.RED))
                                .tooltip(Component.text("Dengan ini kamu menolak seluruh peraturan yang ada",NamedTextColor.RED))
                                .action(DialogAction.customClick(Key.key("athena:deny_rules"), null))
                                .build()
                        ), null, 2))
                )));

        context.getLifecycleManager().registerEventHandler(RegistryEvents.DIALOG.compose()
                .newHandler(event -> event.registry().register(
                    ATHENA_CHOOSE_ROLE,
                    builder -> builder
                        .base(DialogBase.builder(Component.text("Pick Roles"))
                        .externalTitle(Component.text("Pilih Role yang sesuai untuk kamu!"))
                        .body(List.of(
                            DialogBody.plainMessage(Component.text("Hati hati sekali pilih role resource tidak bisa di ubah!",NamedTextColor.GRAY)
                        ))).build()
                    )
                    .type(DialogType.multiAction(
                        List.of(
                            ActionButton.builder(Component.text("Adventure",NamedTextColor.GREEN))
                            .tooltip(Component.text("Role Adventure biasa"))
                            .action(DialogAction.customClick(Key.key("athena:role_adventure"), null))
                            .build(),
                            ActionButton.builder(Component.text("Knight",NamedTextColor.RED))
                            .tooltip(Component.text("Role Knight (sword)"))
                            .action(DialogAction.customClick(Key.key("athena:role_knight"), null))
                            .build(),
                            ActionButton.builder(Component.text("Mage",NamedTextColor.AQUA))
                            .tooltip(Component.text("Role Mage (catalyst)"))
                            .action(DialogAction.customClick(Key.key("athena:role_mage"), null))
                            .build(),
                            ActionButton.builder(Component.text("Archery",NamedTextColor.YELLOW))
                            .tooltip(Component.text("Role Archery (bow)"))
                            .action(DialogAction.customClick(Key.key("athena:role_archery"), null))
                            .build()
                        ), null, 2))
                ))
        );
        
        context.getLifecycleManager().registerEventHandler(RegistryEvents.DIALOG.compose()
                .newHandler(event -> event.registry().register(
                    ATHENA_CHOOSE_SUB_ROLE,
                    builder -> builder
                        .base(DialogBase.builder(Component.text("Pick Sub Roles"))
                        .externalTitle(Component.text("Pilih Role yang sesuai untuk kamu!"))
                        .body(List.of(
                            DialogBody.plainMessage(Component.text("Hati hati sekali pilih role resource tidak bisa di ubah!",NamedTextColor.GRAY)
                        ))).build()
                    )
                    .type(DialogType.multiAction(
                        List.of(
                            ActionButton.builder(Component.text("Istana",NamedTextColor.GREEN))
                            .tooltip(Component.text("Role ini kamu akan di pekerjakan di istana"))
                            .action(DialogAction.customClick(Key.key("athena:subrole_aulicus"), null))
                            .build(),
                            ActionButton.builder(Component.text("Bandit",NamedTextColor.RED))
                            .tooltip(Component.text("Role ini kamu akan menjadi bandit liar"))
                            .action(DialogAction.customClick(Key.key("athena:subrole_latro"), null))
                            .build(),
                            ActionButton.builder(Component.text("Merchant",NamedTextColor.AQUA))
                            .tooltip(Component.text("Role ini kamu akan menjadi merchant"))
                            .action(DialogAction.customClick(Key.key("athena:subrole_merchant"), null))
                            .build(),
                            ActionButton.builder(Component.text("Thief",NamedTextColor.YELLOW))
                            .tooltip(Component.text("Role ini kamu akan menjadi pencuri"))
                            .action(DialogAction.customClick(Key.key("athena:subrole_fur"), null))
                            .build(),
                            ActionButton.builder(Component.text("Assassin",NamedTextColor.GOLD))
                            .tooltip(Component.text("Role ini kamu akan menjadi pembunuh bayaran / berantai"))
                            .action(DialogAction.customClick(Key.key("athena:subrole_sicarius"), null))
                            .build(),
                            ActionButton.builder(Component.text("Spy"))
                            .tooltip(Component.text("Role ini kamu akan menjadi mata mata di pihak manapun"))
                            .action(DialogAction.customClick(Key.key("athena:subrole_speculator"), null))
                            .build()
                        ), null, 2))
                ))
        );


        context.getLifecycleManager().registerEventHandler(RegistryEvents.DIALOG.compose()
            .newHandler(event -> event.registry().register(
                ATHENA_DEBUG_SCREEN,
                builder -> builder
                    .base(DialogBase.builder(Component.text("Debug Screen Yuvy"))
                    .externalTitle(Component.text("Yuvy Debug Test"))
                    .body(List.of(
                        DialogBody.plainMessage(Component.text("Sumatera tidur besok mati lampu lagi"))
                    )).build()
                )
                .type(DialogType.multiAction(
                    List.of(
                        ActionButton.builder(Component.text("Test"))
                        .action(DialogAction.customClick(Key.key("athena:debug_test"), null))
                        .build()
                    ), null, 1))
            ))
        );

        context.getLifecycleManager().registerEventHandler(
            LifecycleEvents.TAGS.postFlatten(RegistryKey.DIALOG)
            .newHandler(event ->{
                event.registrar().addToTag(
                    DialogTagKeys.PAUSE_SCREEN_ADDITIONS,List.of(ATHENA_MAIN_MENU_KEY));
            })
        );
        context.getLifecycleManager().registerEventHandler(
            RegistryEvents.JUKEBOX_SONG.compose().newHandler(event -> {
                    TypedKey<JukeboxSong> key = TypedKey.create(
                        RegistryKey.JUKEBOX_SONG,
                        Key.key("athena:about_you")
                );
                event.registry().register(key,builder -> {
                    builder.soundEvent(sound -> {   
                        sound.empty().location(Key.key("athena:music.about_you"));
                    }).description(Component.text("About You - The 1975"))
                    .lengthInSeconds(326.542222f)
                    .comparatorOutput(15);
                });
            })
        );
        
        context.getLifecycleManager().registerEventHandler(
            RegistryEvents.JUKEBOX_SONG.compose().newHandler(event -> {
                TypedKey<JukeboxSong> key = TypedKey.create(
                    RegistryKey.JUKEBOX_SONG,Key.key("athena:hide"));
                    event.registry().register(key,builder -> {
                        builder.soundEvent(sound -> {
                            sound.empty().location(Key.key("athena:music.hide"));
                        }).description(Component.text("Hide - CS01"))
                        .lengthInSeconds(123.865397f)
                        .comparatorOutput(15);
                    });
            })
        );
        context.getLifecycleManager().registerEventHandler(
            RegistryEvents.JUKEBOX_SONG.compose().newHandler(event -> {
                TypedKey<JukeboxSong> key = TypedKey.create(
                    RegistryKey.JUKEBOX_SONG,Key.key("athena:the_stranglers"));
                    
                event.registry().register(key,builder -> {
                    builder.soundEvent(sound -> {
                        sound.empty().location(Key.key("athena:music.the_stranglers"));
                    }).description(Component.text("The Stranglers - Golden Brown"))
                    .lengthInSeconds(258.554195f)
                    .comparatorOutput(15);
                });
            })
        );
        
    }
}