package me.miquiis.record.common.models;

import com.google.gson.annotations.SerializedName;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.miquiis.record.common.events.custom.RecordEventPlayEvent;
import me.miquiis.record.common.models.RecordScript;
import me.miquiis.record.common.utils.SItemStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RecordTick {
    // Player Position
    public double posx;
    public double posy;
    public double posz;
    public float falldistance;
    public float pitch;
    public float yaw;

    // Player Actions
    public boolean isSwingInProgress;
    public float swingProgress;
    public boolean isCrouching;

    // Player Inventory
    public SItemStack itemInHand;

    // Events
    public List<RecordTickEvent> events;

    public RecordTick(LivingEntity livingEntity)
    {
        this.posx = livingEntity.getPosX();
        this.posy = livingEntity.getPosY();
        this.posz = livingEntity.getPosZ();
        this.falldistance = livingEntity.fallDistance;
        this.pitch = livingEntity.rotationPitch;
        this.yaw = livingEntity.rotationYawHead;
        this.isSwingInProgress = livingEntity.isSwingInProgress;
        this.swingProgress = livingEntity.swingProgress;
        this.isCrouching = livingEntity.isCrouching();
        this.itemInHand = new SItemStack(livingEntity.getHeldItemMainhand());

        this.events = new ArrayList<>();
    }

    public void addRecordTickEvent(RecordTickEvent recordTickEvent)
    {
        this.events.add(recordTickEvent);
    }

    public boolean hasEvents()
    {
        return this.events.size() > 0;
    }

}