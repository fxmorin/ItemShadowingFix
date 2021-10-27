package ca.fxco.itemshadowingfix.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenHandler.class)
public class ScreenHandler_changeOrderMixin {


    /* =====================================================
                  If mojang swaps the lines around
       ===================================================== */
    @Redirect(
            method= "internalOnSlotClick(IILnet/minecraft/screen/slot/SlotActionType;Lnet/minecraft/entity/player/PlayerEntity;)V",
            slice=@Slice(
                    from=@At(
                            value="FIELD",
                            target="Lnet/minecraft/screen/slot/SlotActionType;SWAP:Lnet/minecraft/screen/slot/SlotActionType;"
                    )
            ),
            at=@At(
                    value="INVOKE",
                    target="Lnet/minecraft/entity/player/PlayerInventory;setStack(ILnet/minecraft/item/ItemStack;)V",
                    ordinal = 1
            )
    )
    private void dontRunBeforeInventoryUpdate(PlayerInventory instance, int slot, ItemStack stack) {}

    @Inject(
            method= "internalOnSlotClick(IILnet/minecraft/screen/slot/SlotActionType;Lnet/minecraft/entity/player/PlayerEntity;)V",
            slice=@Slice(
                    from=@At(
                            value="FIELD",
                            target="Lnet/minecraft/screen/slot/SlotActionType;SWAP:Lnet/minecraft/screen/slot/SlotActionType;"
                    )
            ),
            at=@At(
                    value="INVOKE",
                    target="Lnet/minecraft/screen/slot/Slot;setStack(Lnet/minecraft/item/ItemStack;)V",
                    ordinal = 2,
                    shift = At.Shift.AFTER
            )
    )
    private void RunAfterInventoryUpdate(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        player.getInventory().setStack(button, ItemStack.EMPTY);
    }

    /* =====================================================
               If mojang uses a split like carpet-fixes
       ===================================================== */

    @Redirect(
            method= "internalOnSlotClick(IILnet/minecraft/screen/slot/SlotActionType;Lnet/minecraft/entity/player/PlayerEntity;)V",
            slice=@Slice(
                    from=@At(
                            value="FIELD",
                            target="Lnet/minecraft/screen/slot/SlotActionType;SWAP:Lnet/minecraft/screen/slot/SlotActionType;"
                    )
            ),
            at=@At(
                    value="INVOKE",
                    target="Lnet/minecraft/item/ItemStack;split(I;)Lnet/minecraft/item/ItemStack;",
                    ordinal = 1
            )
    )
    private ItemStack dontDoSplit(ItemStack self, int amt) {
        return self;
    }
}