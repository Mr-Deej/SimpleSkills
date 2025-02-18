package dansplugins.simpleskills.commands;

import dansplugins.simpleskills.data.PersistentData;
import dansplugins.simpleskills.data.PlayerRecord;
import dansplugins.simpleskills.services.LocalMessageService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;
import preponderous.ponder.minecraft.bukkit.tools.UUIDChecker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

/**
 * @author Daniel Stephenson
 */
public class InfoCommand extends AbstractPluginCommand {

    public InfoCommand() {
        super(
                new ArrayList<>(Collections.singletonList("info")),
                new ArrayList<>(Collections.singletonList("ss.info"))
        );
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Only players can use this command.");
            return false;
        }
        Player player = (Player) commandSender;
        PlayerRecord playerRecord = PersistentData.getInstance().getPlayerRecord(player.getUniqueId());
        if (playerRecord == null) {
            player.sendMessage(LocalMessageService.getInstance().convert(LocalMessageService.getInstance().getlang().getString("DontHaveRecord")));
            return false;
        }
        playerRecord.sendInfo(commandSender);
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] args) {
        String playerName = args[0];
        UUID playerUUID = new UUIDChecker().findUUIDBasedOnPlayerName(playerName);
        if (playerUUID == null) {
            commandSender.sendMessage(LocalMessageService.getInstance().convert(LocalMessageService.getInstance().getlang().getString("NotFound")));
            return false;
        }
        PlayerRecord playerRecord = PersistentData.getInstance().getPlayerRecord(playerUUID);
        if (playerRecord == null) {
            commandSender.sendMessage(LocalMessageService.getInstance().convert(LocalMessageService.getInstance().getlang().getString("DoesntHaveRecord")));
            return false;
        }
        playerRecord.sendInfo(commandSender);
        return true;
    }
}