package tc.oc.pgm.menu.gui;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import tc.oc.commons.bukkit.gui.buttons.Button;
import tc.oc.commons.bukkit.gui.interfaces.ChestInterface;
import tc.oc.commons.bukkit.stats.StatsUtil;
import tc.oc.commons.bukkit.util.Constants;
import tc.oc.commons.bukkit.util.ItemCreator;
import tc.oc.pgm.PGMTranslations;
import tc.oc.pgm.tokens.gui.MainTokenButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainMenuInterface extends ChestInterface {
    private static MainMenuInterface instance;

    public MainMenuInterface(Player player) {
        super(player, new ArrayList<>(), 27, "Main Menu", getInstance());
        updateButtons();
        instance = this;
    }

    @Override
    public ChestInterface getParent() {
        return getInstance();
    }

    public static MainMenuInterface getInstance() {
        return instance;
    }

    @Override
    public void updateButtons() {
        List<Button> buttons = new ArrayList<>();

        MainTokenButton.getInstance().setSlot(10);
        buttons.add(MainTokenButton.getInstance());

        HashMap<String, Double> stats = StatsUtil.getStats(getPlayer());

        buttons.add(new Button(
                new ItemCreator(Material.GOLDEN_APPLE)
                        .setData(1)
                        .setName(Constants.PREFIX + getPlayer().getDisplayName() + PGMTranslations.get().t("stats.ui.list", getPlayer()))
                        .addLore(ChatColor.AQUA + PGMTranslations.get().t("stats.ui.kills", getPlayer()) + ChatColor.BLUE + String.format("%,d", (int)(double)stats.get("kills")))
                        .addLore(ChatColor.AQUA + PGMTranslations.get().t("stats.ui.deaths", getPlayer()) + ChatColor.BLUE + String.format("%,d", (int)(double)stats.get("deaths")))
                        .addLore(ChatColor.AQUA + PGMTranslations.get().t("stats.ui.kd", getPlayer()) + ChatColor.BLUE + String.format("%.2f", stats.get("kd")))
                        .addLore(ChatColor.AQUA + PGMTranslations.get().t("stats.ui.wools", getPlayer()) + ChatColor.BLUE + String.format("%,d", (int)(double)stats.get("wool_placed")))
                        .addLore(ChatColor.AQUA + PGMTranslations.get().t("stats.ui.cores", getPlayer()) + ChatColor.BLUE + String.format("%,d", (int)(double)stats.get("cores_leaked")))
                        .addLore(ChatColor.AQUA + PGMTranslations.get().t("stats.ui.monuments", getPlayer()) + ChatColor.BLUE + String.format("%,d", (int)(double)stats.get("destroyables_destroyed")))
                , 12));

        buttons.add(new Button(
                new ItemCreator(Material.BOOK_AND_QUILL)
                        .setName(Constants.PREFIX + "Settings")
                , 14) {
            @Override
            public void function(Player player) {
                player.openInventory(new SettingsInterface(player).getInventory());
            }
        });

        buttons.add(new Button(
                new ItemCreator(Material.BOOK_AND_QUILL)
                        .setName(Constants.PREFIX + "Achievements")
                , 16) {
            @Override
            public void function(Player player) {
                player.openInventory(new AchievementsInterface(player).getInventory());
            }
        });

        setButtons(buttons);
        updateInventory();
    }
}
