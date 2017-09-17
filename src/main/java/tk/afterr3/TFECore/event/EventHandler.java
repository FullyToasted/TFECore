package tk.afterr3.TFECore.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tk.afterr3.TFECore.TFECore;

public class EventHandler {
    private final boolean client;
    private int tickCount = 0;

    public EventHandler() {
        this.client = FMLCommonHandler.instance().getEffectiveSide().isClient();
        System.out.println("HN Event Handler started on side " + FMLCommonHandler.instance().getEffectiveSide());
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onRenderLiving(RenderLivingEvent.Specials.Pre event) {
        if (event.getEntity() instanceof EntityPlayer) {
            if (event.isCancelable()) {
                Object hidden = TFECore.INSTANCE.hiddenPlayers.get(
                        event.getEntity().getCommandSenderEntity().getName().toLowerCase());
                if (hidden != null && (Boolean) hidden) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!client) {
            TFECore.INSTANCE.onClientConnect(event.player);
        }
    }

    @SubscribeEvent
    public void tickEnd(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            onTickInGame();
        }
    }

    private void onTickInGame() {
        if (!TFECore.saveOfflinePlayers) {
            TFECore.INSTANCE.removeOfflinePlayers();
        }

        TFECore.INSTANCE.checkFile();

        String[] users = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getAllUsernames();
        for (String user : users) {
            if (!TFECore.INSTANCE.hiddenPlayers.containsKey(user.toLowerCase())
                    || TFECore.INSTANCE.hiddenPlayers.get(user.toLowerCase()) == null) {

                EntityPlayerMP player = FMLCommonHandler.instance().getMinecraftServerInstance()
                        .getServer().getPlayerList().getPlayerByUsername(user);
                TFECore.INSTANCE.updateHiddenPlayers(user, TFECore.defaultHiddenStatus);
                player.addChatMessage(new TextComponentString(
                        "Your name is: " + (TFECore.defaultHiddenStatus ? "\u00a7a Hidden" : "\u00a74 Visible")));
            }
        }

        if (tickCount == 20) {
            tickCount = 0;

            Configuration tempConfig = TFECore.INSTANCE.config;
            tempConfig.load();

            if (TFECore.defaultHiddenStatus != tempConfig.get(Configuration.CATEGORY_GENERAL, "defaultHiddenStatus", false, "Default state for new players").getBoolean(false)) {
                Property temp = tempConfig.get(Configuration.CATEGORY_GENERAL, "defaultHiddenStatus", false, "Default state for new players");
                temp.set(TFECore.defaultHiddenStatus);
            }

            if (TFECore.showHideStatusOnLogin != tempConfig.get(Configuration.CATEGORY_GENERAL, "showHideStatusOnLogin",
                    true, "Showing information about hide status after enter the game").getBoolean(true)) {
                Property temp = tempConfig.get(Configuration.CATEGORY_GENERAL, "showHideStatusOnLogin", true, "Showing information about hide status after enter the game");
                temp.set(TFECore.showHideStatusOnLogin);
            }

            if (TFECore.saveOfflinePlayers != tempConfig.get(Configuration.CATEGORY_GENERAL, "saveOfflinePlayers", true, "Whether or not to keep players in 'hidden.txt' if they are offline - useful for big servers").getBoolean(true)) {
                Property temp = tempConfig.get(Configuration.CATEGORY_GENERAL, "saveOfflinePlayers", true, "Whether or not to keep players in 'hidden.txt' if they are offline - useful for big servers");
                temp.set(TFECore.saveOfflinePlayers);
            }

            if (TFECore.allowCommand != tempConfig.get(Configuration.CATEGORY_GENERAL, "allowCommand", true, "Whether or not non-ops can use the /name command").getBoolean(true)) {
                Property temp = tempConfig.get(Configuration.CATEGORY_GENERAL, "allowCommand", true, "Whether or not non-ops can use the /name command");
                temp.set(TFECore.allowCommand);
            }

            tempConfig.save();
        } else {
            tickCount++;
        }
    }
}
