package uk.ac.bsfc.sbp.utils.npc;


import com.mojang.authlib.GameProfile;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class NPC {
    private final FakeServerPlayer fakePlayer;

    public NPC(ServerLevel level, String name, double x, double y, double z) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), name);
        this.fakePlayer = new FakeServerPlayer(level.getServer(), level, profile);
        this.fakePlayer.setPos(x, y, z);
    }

    public ServerPlayer getPlayer() {
        return fakePlayer;
    }

    public void spawn() {
        fakePlayer.level().addFreshEntity(fakePlayer);

        fakePlayer.setCustomNameVisible(true);
    }

    public void remove() {
        fakePlayer.remove(Player.RemovalReason.DISCARDED);
    }
}
