package fuzs.blockrunner.network;

import com.google.gson.JsonObject;
import fuzs.blockrunner.world.level.block.data.BlockSpeedManager;
import fuzs.puzzleslib.api.config.v3.json.JsonConfigFileUtil;
import fuzs.puzzleslib.api.network.v2.MessageV2;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public class S2CBlockSpeedMessage implements MessageV2<S2CBlockSpeedMessage> {
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
    public MessageHandler<S2CBlockSpeedMessage> makeHandler() {
        return new MessageHandler<>() {

            @Override
            public void handle(S2CBlockSpeedMessage message, Player player, Object gameInstance) {
                BlockSpeedManager.INSTANCE.deserialize(message.customBlockSpeeds);
            }
        };
    }
}
