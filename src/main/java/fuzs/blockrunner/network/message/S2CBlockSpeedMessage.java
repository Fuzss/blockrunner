package fuzs.blockrunner.network.message;

import com.google.gson.JsonObject;
import fuzs.blockrunner.data.BlockSpeedManager;
import fuzs.puzzleslib.json.JsonConfigFileUtil;
import fuzs.puzzleslib.network.message.Message;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public class S2CBlockSpeedMessage implements Message {
    private JsonObject customBlockSpeeds;

    public S2CBlockSpeedMessage() {
    }

    public S2CBlockSpeedMessage(JsonObject customBlockSpeeds) {
        this.customBlockSpeeds = customBlockSpeeds;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(JsonConfigFileUtil.GSON.toJson(this.customBlockSpeeds), 262144);
    }

    @Override
    public void read(FriendlyByteBuf buf) {
        this.customBlockSpeeds = JsonConfigFileUtil.GSON.fromJson(buf.readUtf(262144), JsonObject.class);
    }

    @Override
    public BlockSpeedHandler makeHandler() {
        return new BlockSpeedHandler();
    }

    private static class BlockSpeedHandler extends PacketHandler<S2CBlockSpeedMessage> {
        @Override
        public void handle(S2CBlockSpeedMessage packet, Player player, Object gameInstance) {
            BlockSpeedManager.INSTANCE.deserialize(packet.customBlockSpeeds);
        }
    }
}
