// Copyright (c) 2023 Joseph Hale
//
// This Source Code Form is subject to the terms of the Mozilla Public
// License, v. 2.0. If a copy of the MPL was not distributed with this
// file, You can obtain one at http://mozilla.org/MPL/2.0/.

package dev._vinzsan.athena;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.plugin.Plugin;

/**
 * Mencegat packet command (mis. "/fill ...") sebelum vanilla Minecraft
 * sempat mengecek karakter ilegal dan mengkick pemain dengan alasan
 * "Illegal characters in chat".
 *
 * Dipasang lewat ProtocolLib karena pengecekan aslinya terjadi di level
 * NMS/packet, sebelum event Bukkit (seperti AsyncChatEvent) sempat fire.
 *
 * CATATAN: ini SENGAJA hanya menargetkan packet COMMAND, bukan CHAT biasa.
 * Chat message biasa (sejak MC 1.19.1+) ditandatangani secara kriptografis
 * oleh client. Mengubah isinya di sini tanpa menyesuaikan signature-nya
 * bisa memicu jenis penolakan lain (signature invalid). Command umumnya
 * tidak memakai skema signing yang sama, jadi lebih aman untuk disanitize
 * di sini.
 *
 * CATATAN TAMBAHAN soal CHAT_COMMAND_SIGNED: packet ini bisa membawa
 * signature PER-ARGUMENT (bukan cuma satu signature untuk seluruh command),
 * dipakai untuk command yang argumennya sendiri butuh diverifikasi
 * (misalnya nama pemain di /msg). Kalau ternyata karakter ilegal ada di
 * salah satu argumen yang signed, sanitasi di sini bisa membuat
 * argumen tsb tidak lagi cocok dengan signature-nya. Untuk kasus
 * "/fill ..." (Litematica) argumennya bukan tipe yang perlu signed,
 * jadi kemungkinan besar aman -- tapi kalau nanti muncul kick jenis baru
 * (bukan lagi "illegal characters"), itu tandanya ini penyebabnya.
 */
public final class LegalChar {

    // Aktifkan dari AthenaPlugin#onEnable() dengan: LegalCharFix.register(this);
    public static void register(Plugin plugin) {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(
                plugin,
                ListenerPriority.HIGHEST,
                PacketType.Play.Client.CHAT_COMMAND,
                PacketType.Play.Client.CHAT_COMMAND_SIGNED
        ) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                var packet = event.getPacket();
                String message = packet.getStrings().read(0);
                if (message == null) return;

                String sanitized = sanitize(message);
                if (!sanitized.equals(message)) {
                    packet.getStrings().write(0, sanitized);
                }
            }
        });
    }

    private static String sanitize(String input) {
        StringBuilder sb = new StringBuilder(input.length());
        for (char c : input.toCharArray()) {
            if (isAllowedCommandCharacter(c)) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    // Meniru logika isAllowedChatCharacter vanilla: buang char kontrol,
    // char 127 (DEL), dan simbol formatting '§' (code 167).
    private static boolean isAllowedCommandCharacter(char c) {
        return c != 167 && c != 127 && c >= 32;
    }

    private LegalChar() {
        // no instances
    }
}