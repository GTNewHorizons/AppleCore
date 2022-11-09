package squeek.applecore.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.EnumDifficulty;

public class MessageDifficultySync implements IMessage, IMessageHandler<MessageDifficultySync, IMessage> {
    EnumDifficulty difficulty;

    public MessageDifficultySync() {}

    public MessageDifficultySync(EnumDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(difficulty.getDifficultyId());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        difficulty = EnumDifficulty.getDifficultyEnum(buf.readByte());
    }

    @Override
    public IMessage onMessage(MessageDifficultySync message, MessageContext ctx) {
        NetworkHelper.getSidedPlayer(ctx).worldObj.difficultySetting = message.difficulty;
        return null;
    }
}
