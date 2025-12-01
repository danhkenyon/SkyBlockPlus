package uk.ac.bsfc.sbp.utils.npc;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class FakeServerPlayer extends ServerPlayer {
    public FakeServerPlayer(MinecraftServer server, ServerLevel level, GameProfile profile) {
        super(server, level, profile, CommonListenerCookie.createInitial(profile, false).clientInformation());
        net.minecraft.network.Connection connection = server.getConnection().getConnections().getFirst();
        this.connection = new ServerGamePacketListenerImpl(server, connection, this, CommonListenerCookie.createInitial(profile, false));
    }
}