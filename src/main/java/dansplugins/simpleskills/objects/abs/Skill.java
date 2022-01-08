package dansplugins.simpleskills.objects.abs;

import dansplugins.simpleskills.services.LocalConfigService;
import dansplugins.simpleskills.services.LocalMessageService;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.HashSet;

/**
 * @author Daniel Stephenson
 */
public abstract class Skill {
    private int ID;
    private String name;
    private int maxLevel;
    private int baseExperienceRequirement;
    private double experienceIncreaseFactor;
    private boolean active;

    private HashSet<Benefit> benefits = new HashSet<>();

    public Skill(int ID, String name, int maxLevel, int baseExperienceRequirement, double experienceIncreaseFactor) {
        this.ID = ID;
        this.name = name;
        this.maxLevel = maxLevel;
        this.baseExperienceRequirement = baseExperienceRequirement;
        this.experienceIncreaseFactor = experienceIncreaseFactor;
        active = true;
    }

    public Skill(int ID, String name) {
        this(ID, name, getDefaultMaxLevel(), getDefaultBaseExperienceRequirement(), getDefaultExperienceIncreaseFactor());
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getBaseExperienceRequirement() {
        return baseExperienceRequirement;
    }

    public void setBaseExperienceRequirement(int baseExperienceRequirement) {
        this.baseExperienceRequirement = baseExperienceRequirement;
    }

    public double getExperienceIncreaseFactor() {
        return experienceIncreaseFactor;
    }

    public void setExperienceIncreaseFactor(double experienceIncreaseFactor) {
        this.experienceIncreaseFactor = experienceIncreaseFactor;
    }

    public HashSet<Benefit> getBenefits() {
        return benefits;
    }

    public void setBenefits(HashSet<Benefit> benefits) {
        this.benefits = benefits;
    }

    public void addBenefit(Benefit benefit) {
        benefits.add(benefit);
    }

    public void removeBenefit(Benefit benefit) {
        benefits.remove(benefit);
    }

    public Benefit getBenefit(int benefitID) {
        for (Benefit benefit : benefits) {
            if (benefit.getID() == benefitID) {
                return benefit;
            }
        }
        return null;
    }

    public boolean hasBenefit(int benefitID) {
        return (getBenefit(benefitID) != null);
    }

    // ---

    public void sendInfo(CommandSender commandSender) {
        for (String sinfo : LocalMessageService.getInstance().getlang().getStringList("Skill-Info"))
            commandSender.sendMessage(LocalMessageService.getInstance().convert(sinfo)
                    .replaceAll("%skillname%", getName())
                    .replaceAll("%active%", String.valueOf(isActive()))
                    .replaceAll("%nob%", String.valueOf(getBenefits().size()))
                    .replaceAll("%mlevel%",  String.valueOf(getMaxLevel()))
                    .replaceAll("%ber%",  String.valueOf(getBaseExperienceRequirement()))
                    .replaceAll("%eif%",  String.valueOf(getExperienceIncreaseFactor())));
    }

    private static int getDefaultMaxLevel() {
        return LocalConfigService.getInstance().getconfig().getInt("defaultMaxLevel");
    }

    private static int getDefaultBaseExperienceRequirement() {
        return LocalConfigService.getInstance().getconfig().getInt("defaultBaseExperienceRequirement", 10);
    }

    private static double getDefaultExperienceIncreaseFactor() {
        return LocalConfigService.getInstance().getconfig().getDouble("defaultDefaultExperienceIncreaseFactor", 1.2);
    }
}