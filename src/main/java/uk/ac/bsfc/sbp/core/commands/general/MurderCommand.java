package uk.ac.bsfc.sbp.core.commands.general;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import uk.ac.bsfc.sbp.utils.command.SBCommand;
import uk.ac.bsfc.sbp.utils.user.SBPlayer;

import java.util.function.Predicate;

public class MurderCommand extends SBCommand {
    public MurderCommand() {
        super();

        this.name("murder");
        this.description("kills the entity you're looking at.");
        this.permission("sbp.murder");
    }

    @Override
    public void execute() {
        if (!(user instanceof SBPlayer sbPlayer)) {
            user.sendMessage("<red>This command can only be used by players.");
            return;
        }
        if (args.length != 0) {
            user.sendMessage("<red>too many arguments");
            return;
        }
        Player player = user.toBukkit(Player.class);

        LivingEntity entity = rayTraceLiving(player, 100);
        if (entity == null){
            user.sendMessage("<red>No entity found!");
            return;
        }

        entity.setHealth(0.0);
        user.sendMessage("<red>Entity has been killed!");
    }

    public static LivingEntity rayTraceLiving(Player player, double maxDistance) {
        Location eye = player.getEyeLocation();
        World world = player.getWorld();
        Predicate<Entity> filter = e -> e instanceof LivingEntity && !e.equals(player) && e.isValid();
        RayTraceResult result = world.rayTraceEntities(eye, eye.getDirection(), maxDistance, 0.5, filter);
        if (result != null && result.getHitEntity() instanceof LivingEntity) {
            return (LivingEntity) result.getHitEntity();
        }
        return null;
    }
}
