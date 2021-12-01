package dansplugins.simpleskills.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dansplugins.simpleskills.SimpleSkills;
import dansplugins.simpleskills.data.PersistentData;
import dansplugins.simpleskills.objects.skills.abs.Skill;
import dansplugins.simpleskills.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import preponderous.ponder.modifiers.Cacheable;
import preponderous.ponder.modifiers.Savable;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

/**
 * @author Daniel Stephenson
 */
public class PlayerRecord implements Savable, Cacheable {
    private UUID playerUUID;
    private HashSet<Integer> knownSkills = new HashSet<>();
    private HashMap<Integer, Integer> skillLevels = new HashMap<>();
    private HashMap<Integer, Integer> experience = new HashMap<>();

    public PlayerRecord(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public PlayerRecord(Map<String, String> data) {
        this.load(data);
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public HashSet<Integer> getKnownSkills() {
        return knownSkills;
    }

    public void setKnownSkills(HashSet<Integer> knownSkills) {
        this.knownSkills = knownSkills;
    }

    public boolean addKnownSkill(Skill skill) {
        boolean success = knownSkills.add(skill.getID());
        setSkillLevel(skill.getID(), 0);
        setExperience(skill.getID(), 1);

        // attempt to inform player
        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null) {
            player.sendMessage(ChatColor.GREEN + "You've learned the " + skill.getName() + " skill. Type /ss info to view your skills.");
        }
        Logger.getInstance().log(SimpleSkills.getInstance().getToolbox().getUUIDChecker().findPlayerNameBasedOnUUID(playerUUID) + " learned the " + skill.getName() + " skill.");
        return success;
    }

    public boolean isKnown(Skill skill) {
        return knownSkills.contains(skill.getID());
    }

    public HashMap<Integer, Integer> getSkillLevels() {
        return skillLevels;
    }

    public void setSkillLevels(HashMap<Integer, Integer> skillLevels) {
        this.skillLevels = skillLevels;
    }

    public int getSkillLevel(int ID) {
        if (!skillLevels.containsKey(ID)) {
            skillLevels.put(ID, -1);
            return -1;
        }
        return skillLevels.get(ID);
    }

    public void setSkillLevel(int ID, int value) {
        if (skillLevels.containsKey(ID)) {
            skillLevels.replace(ID, value);
        }
        else {
            skillLevels.put(ID, value);
        }
    }

    public void incrementSkillLevel(int ID) {
        setSkillLevel(ID, getSkillLevel(ID) + 1);
    }

    public HashMap<Integer, Integer> getExperience() {
        return experience;
    }

    public void setExperience(HashMap<Integer, Integer> experience) {
        this.experience = experience;
    }

    public int getExperience(int ID) {
        if (!experience.containsKey(ID)) {
            experience.put(ID, 0);
            return 0;
        }
        return experience.get(ID);
    }

    public void setExperience(int ID, int value) {
        if (experience.containsKey(ID)) {
            experience.replace(ID, value);
        }
        else {
            experience.put(ID, value);
        }
    }

    public void incrementExperience(int ID) {
        setExperience(ID, getExperience(ID) + 1);
        checkForLevelUp(ID);
    }

    // ---

    @Override
    public Object getKey() {
        return getPlayerUUID();
    }

    public void sendInfo(CommandSender commandSender) {
        if (knownSkills.size() == 0) {
            commandSender.sendMessage(ChatColor.RED + "No skills known.");
            return;
        }
        commandSender.sendMessage(ChatColor.AQUA + "=== Skills of " + SimpleSkills.getInstance().getToolbox().getUUIDChecker().findPlayerNameBasedOnUUID(playerUUID) + " === ");
        for (int skillID : knownSkills) {
            int currentLevel = getSkillLevel(skillID);
            int currentExperience = getExperience(skillID);
            Skill skill = PersistentData.getInstance().getSkill(skillID);
            int experienceRequired = getExperienceRequired(getSkillLevel(skillID), skill.getBaseExperienceRequirement(), skill.getExperienceIncreaseFactor());
            commandSender.sendMessage(ChatColor.AQUA + skill.getName() + "- LEVEL: " + currentLevel + " - EXP: " + currentExperience + "/" + experienceRequired);
        }
    }

    public void checkForLevelUp(int ID) {
        int level = getSkillLevel(ID);
        int experience = getExperience(ID);
        Skill skill = PersistentData.getInstance().getSkill(ID);
        int experienceRequiredForLevelUp = getExperienceRequired(level, skill.getBaseExperienceRequirement(), skill.getExperienceIncreaseFactor());
        if (experience >= experienceRequiredForLevelUp) {
            // level up
            incrementSkillLevel(ID);
            setExperience(ID, getExperience(ID) - experienceRequiredForLevelUp);
        }
    }

    @Override()
    public Map<String, String> save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Map<String, String> saveMap = new HashMap<>();
        saveMap.put("playerUUID", gson.toJson(playerUUID));
        saveMap.put("knownSkills", gson.toJson(knownSkills));
        saveMap.put("skillLevels", gson.toJson(skillLevels));

        return saveMap;
    }

    @Override()
    public void load(Map<String, String> data) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Type integerSetType = new TypeToken<HashSet<Integer>>(){}.getType();
        Type integerToIntegerMapType = new TypeToken<HashMap<Integer, Integer>>(){}.getType();

        playerUUID = UUID.fromString(gson.fromJson(data.get("playerUUID"), String.class));
        knownSkills = gson.fromJson(data.get("knownSkills"), integerSetType);
        skillLevels = gson.fromJson(data.get("skillLevels"), integerToIntegerMapType);
    }

    private int getExperienceRequired(int currentLevel, int baseExperienceRequirement, double experienceIncreaseFactor) {
        return (int) (baseExperienceRequirement * Math.pow(experienceIncreaseFactor, currentLevel));
    }
}