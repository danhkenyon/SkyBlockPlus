package uk.ac.bsfc.sbp.core.commands.general;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import uk.ac.bsfc.sbp.utils.command.SBCommand;
import uk.ac.bsfc.sbp.utils.npc.FakeEntity;
import uk.ac.bsfc.sbp.utils.npc.MobUtils;
import uk.ac.bsfc.sbp.utils.npc.NPC;

public class TestCommand extends SBCommand {

    MiniMessage mm = MiniMessage.miniMessage();

    public TestCommand() {
        super();
        super.name("test");
        super.description("A test command.");
        super.usage("/test");
        super.permission(null);

        super.aliases("test-command");
    }
    @Override
    public void execute() {
        CraftPlayer cPlayer = (CraftPlayer) user.toBukkit(Player.class);
        ServerPlayer sPlayer = cPlayer.getHandle();

        Location spawnLoc = cPlayer.getLocation().add(0, 1, 0);
        Entity fE = FakeEntity.create(EntityType.VILLAGER, sPlayer.level());

        fE.moveOrInterpolateTo(new Vec3(spawnLoc.getX(), spawnLoc.getY(), spawnLoc.getZ()));
        sPlayer.level().addFreshEntity(fE);

        MobUtils.disableAI(fE);
    }
}

