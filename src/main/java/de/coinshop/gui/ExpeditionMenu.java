package de.coinshop.gui;

import de.coinshop.CoinShopPlugin;
import de.coinshop.ExpeditionManager;
import de.coinshop.ItemUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/** Hauptmenü der Expeditionen: Ausrüstung, Expeditionslevel und Belohnungs-Multiplikator. */
public final class ExpeditionMenu extends Menu {

    static Component line(String text, NamedTextColor color) {
        return Component.text(text, color).decoration(TextDecoration.ITALIC, false);
    }

    private final Player viewer;

    public ExpeditionMenu(CoinShopPlugin plugin, Player viewer) {
        super(plugin, 3, Component.text("Expeditionen"));
        this.viewer = viewer;
        ExpeditionManager ex = plugin.expeditions();
        var uuid = viewer.getUniqueId();

        set(10, ItemUtil.icon(Material.NETHERITE_CHESTPLATE, line("Ausrüstung", NamedTextColor.GOLD), List.of(
                line("Level deine Start-Ausrüstung mit Coins:", NamedTextColor.GRAY),
                line("Rüstung, Waffen, Pfeile, Nahrung, Blöcke, Utensilien.", NamedTextColor.GRAY),
                line("Du kannst auch ohne Ausrüstung starten.", NamedTextColor.DARK_GRAY))),
                (p, c) -> new ExpeditionGearMenu(plugin, p).open(p));

        set(12, ItemUtil.icon(Material.WOLF_ARMOR, line("Begleiter", NamedTextColor.GOLD), List.of(
                line("Kaufe Wölfe, Golems, Katzen und mehr,", NamedTextColor.GRAY),
                line("die dir im Level helfen.", NamedTextColor.GRAY),
                line("Plätze: " + ex.slotsUnlocked(uuid) + ", dabei: " + ex.companionsTaken(uuid), NamedTextColor.YELLOW),
                line("Kaufen, mitnehmen, leveln, auf Reisen schicken.", NamedTextColor.DARK_GRAY))),
                (p, c) -> new ExpeditionCompanionHubMenu(plugin, p).open(p));

        set(14, ItemUtil.icon(Material.DIAMOND_SWORD, line("Expeditionslevel", NamedTextColor.GOLD), List.of(
                line("Nächstes Level: " + ex.nextLevel(uuid) + " von " + ExpeditionManager.MAX_LEVEL, NamedTextColor.YELLOW),
                line("Sieh, welche Kreaturen dich erwarten,", NamedTextColor.GRAY),
                line("und starte die Expedition.", NamedTextColor.GRAY))),
                (p, c) -> new ExpeditionLevelMenu(plugin, p).open(p));

        if (ex.defenseEnabled()) {
            set(24, ItemUtil.icon(Material.BEACON, line("Verteidigung", NamedTextColor.GOLD), List.of(
                    line("Tower Defense: Schütze den Kern vor Welle um Welle.", NamedTextColor.GRAY),
                    line("Du beginnst immer bei Welle 1, jede Welle ist schwerer.", NamedTextColor.GRAY),
                    line("Jeder besiegte Gegner bringt Coins, ausgezahlt wird am Ende.", NamedTextColor.GRAY),
                    line("Der Kern darf nicht zugebaut werden.", NamedTextColor.GRAY),
                    line("Deine beste Welle: " + ex.defenseBest(uuid), NamedTextColor.YELLOW),
                    line("Klicken zum Starten", NamedTextColor.GREEN))),
                    (p, c) -> {
                        p.closeInventory();
                        ex.startDefense(p);
                    });
        }

        int stage = ex.multStage(uuid);
        List<Component> lore = new ArrayList<>();
        lore.add(line("Belohnung pro Level: Levelzahl x " + trim(ex.multFactor(stage)), NamedTextColor.YELLOW));
        lore.add(line("Stufe " + stage + " von " + ex.maxMultStage() + " (x" + trim(ex.multFactor(stage)) + ")", NamedTextColor.GRAY));
        if (stage < ex.maxMultStage()) {
            lore.add(line("Nächste Stufe (x" + trim(ex.multFactor(stage + 1)) + "): " + ex.multPrice(stage + 1) + " Coins", NamedTextColor.GOLD));
            lore.add(line("Klicken zum Kaufen", NamedTextColor.GREEN));
        } else {
            lore.add(line("Höchste Stufe erreicht", NamedTextColor.GREEN));
        }
        set(22, ItemUtil.icon(Material.CHEST, line("Inventar sortieren", NamedTextColor.GOLD), List.of(
                line("Lege fest, auf welchem Platz im Rucksack", NamedTextColor.GRAY),
                line("jeder Gegenstand liegt, wenn das Level startet.", NamedTextColor.GRAY))),
                (p, c) -> new ExpeditionLayoutMenu(plugin, p, -1).open(p));

        set(16, ItemUtil.icon(Material.EMERALD, line("Belohnungs-Multiplikator", NamedTextColor.GOLD), lore), (p, c) -> {
            ex.upgradeMult(p);
            new ExpeditionMenu(plugin, p).open(p);
        });

        set(18, backButton(), (p, c) -> new MainMenu(plugin).open(p));
        fillEmpty();
    }

    static String trim(double v) {
        return v == Math.floor(v) ? String.valueOf((long) v) : String.format(java.util.Locale.ROOT, "%.1f", v);
    }

    @Override
    public void open(Player player) {
        set(26, balanceIcon(player), incomeClick());
        super.open(player);
    }
}
