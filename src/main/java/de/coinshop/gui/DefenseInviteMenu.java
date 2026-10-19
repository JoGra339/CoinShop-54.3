package de.coinshop.gui;

import de.coinshop.CoinShopPlugin;
import de.coinshop.ItemUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

/** Einladung in die laufende Verteidigung: alle Spieler, die gerade nicht in einer Expedition sind. */
public final class DefenseInviteMenu extends Menu {

    public DefenseInviteMenu(CoinShopPlugin plugin, Player host) {
        super(plugin, 5, Component.text("Mitspieler einladen"));
        List<Player> candidates = plugin.expeditions().invitable(host);
        int slot = 0;
        for (Player target : candidates) {
            if (slot >= 36) {
                break;
            }
            set(slot++, BountyMenu.head(target.getUniqueId(), ExpeditionMenu.line(target.getName(), NamedTextColor.GOLD), List.of(
                    ExpeditionMenu.line("Klicken: einladen", NamedTextColor.GREEN),
                    ExpeditionMenu.line("Die Einladung gilt 2 Minuten.", NamedTextColor.GRAY))), (p, c) -> {
                plugin.expeditions().invite(p, target);
                p.closeInventory();
            });
        }
        if (candidates.isEmpty()) {
            set(22, ItemUtil.icon(Material.BARRIER, ExpeditionMenu.line("Gerade ist niemand frei", NamedTextColor.RED), List.of(
                    ExpeditionMenu.line("Es gibt keinen Spieler, den du einladen kannst.", NamedTextColor.GRAY))), null);
        }
        set(40, ItemUtil.icon(Material.OAK_DOOR, ExpeditionMenu.line("Schließen", NamedTextColor.GRAY), List.of()), (p, c) -> p.closeInventory());
        fillEmpty();
    }
}
