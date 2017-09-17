package tk.afterr3.TFECore.network.packet;

import tk.afterr3.TFECore.TFECore;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;


public class PacketTFEChange implements IMessage {
    private String username;
    private boolean newState;

    public PacketTFEChange() {
    }

    public PacketTFEChange(EntityPlayer player, boolean newState) {
        this.username = player.getCommandSenderEntity().getName();
        this.newState = newState;
    }

    public PacketTFEChange(String username, boolean newState) {
        this.username = username;
        this.newState = newState;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.username = ByteBufUtils.readUTF8String(buf);
        this.newState = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.username);
        buf.writeBoolean(this.newState);
    }

    public static class Handler implements IMessageHandler<PacketTFEChange, IMessage> {
        @Override
        public IMessage onMessage(PacketTFEChange message, MessageContext ctx) {
            TFECore.INSTANCE.hiddenPlayers.remove(message.username);
            TFECore.INSTANCE.hiddenPlayers.put(message.username, message.newState);
            return null; //Reply
        }
    }
}
