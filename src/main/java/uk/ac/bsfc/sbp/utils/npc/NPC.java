package uk.ac.bsfc.sbp.utils.npc;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
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
        MinecraftServer server = MinecraftServer.getServer();
        ServerLevel level = fakePlayer.level();

        server.getPlayerList().placeNewPlayer(
                new Connection(PacketFlow.CLIENTBOUND),
                fakePlayer,
                CommonListenerCookie.createInitial(fakePlayer.getGameProfile(), false)
        );

        // Set custom name visibility
        fakePlayer.setCustomNameVisible(true);
    }

    public void remove() {
        fakePlayer.remove(Player.RemovalReason.DISCARDED);
    }
}
