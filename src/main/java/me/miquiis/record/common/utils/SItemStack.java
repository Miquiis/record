package me.miquiis.record.common.utils;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Random;

public class SItemStack {

    public String itemID;
    public int itemCount;
    public String nbtTag;

    public SItemStack(String itemID, int itemCount, String nbtTag) {
        this.itemID = itemID;
        this.itemCount = itemCount;
        this.nbtTag = nbtTag;
    }

    public SItemStack(ItemStack itemStack) {
        this.itemID = itemStack.getItem().getRegistryName().toString();
        this.itemCount = itemStack.getCount();
        this.nbtTag = itemStack.getOrCreateTag().toString();
    }

    public ItemStack createItemstack()
    {
        CompoundNBT compoundNBT = null;
        try {
            compoundNBT = JsonToNBT.getTagFromJson(nbtTag);
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
        return new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemID)), itemCount, compoundNBT);
    }

}
