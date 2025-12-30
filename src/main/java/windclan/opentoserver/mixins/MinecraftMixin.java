package windclan.opentoserver.mixins;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.yggdrasil.ProfileActionType;
import com.mojang.authlib.yggdrasil.ProfileResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.BanNoticeScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    /**
     * @author WindClan
     * @reason Changes the order the initial screens are added
     */
    @Overwrite
    private boolean addInitialScreens(List<Function<Runnable, Screen>> list) {
        Minecraft a = (Minecraft)(Object)this;
        boolean bl = false;

        ProfileResult profileResult = a.profileFuture.join();
        if (profileResult != null) {
            GameProfile gameProfile = profileResult.profile();
            Set<ProfileActionType> set = profileResult.actions();
            if (set.contains(ProfileActionType.FORCED_NAME_CHANGE)) {
                list.add((Function)(runnable) -> BanNoticeScreens.createNameBan(gameProfile.name(), (Runnable) runnable));
            }

            if (set.contains(ProfileActionType.USING_BANNED_SKIN)) {
                list.add(BanNoticeScreens::createSkinBan);
            }
        }

        list.add((Function)(runnable) -> new JoinMultiplayerScreen(new TitleScreen()));

        return bl;
    }
}
