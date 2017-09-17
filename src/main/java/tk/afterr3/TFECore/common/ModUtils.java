package tk.afterr3.TFECore.common;

import net.minecraftforge.fml.common.FMLCommonHandler;

public class ModUtils {
    public static boolean isStringInArray(String[] arr, String match) {
        return isStringInArray(arr, match, false);
    }

    public static boolean isStringInArray(String[] arr, String match, boolean ignoreCase) {
        for (String str : arr) {
            if ((ignoreCase && str.equalsIgnoreCase(match)) || str.equals(match)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isPlayerOp(String username) {
        return isStringInArray(FMLCommonHandler.instance().getMinecraftServerInstance()
                .getServer().getPlayerList().getOppedPlayerNames(), username, true);
    }
}
