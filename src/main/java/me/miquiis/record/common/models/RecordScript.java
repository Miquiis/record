package me.miquiis.record.common.models;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RecordScript {

    public static class RecordTick {

        public static class RecordTickEvent {

            protected transient String type;

            public RecordTickEvent(String type)
            {
                this.type = type;
            }

            public void execute(Entity entity)
            {

            }

        }

        public static class ItemRecordTickEvent extends RecordTickEvent {

            public String itemID;
            public int itemCount;
            public String nbtTag;

            public ItemRecordTickEvent(String itemID, int itemCount, String nbtTag) {
                super("item");
                this.itemID = itemID;
                this.itemCount = itemCount;
                this.nbtTag = nbtTag;
            }

            @Override
            public void execute(Entity entity) {
                super.execute(entity);
                final ItemStack stack = createItemstack();
                if (!stack.isEmpty()) {
                    if (!entity.world.isRemote) {
                        ItemEntity itementity = new ItemEntity(entity.world, entity.getPosX(), entity.getPosYEye(), entity.getPosZ(), stack);
                        itementity.setDefaultPickupDelay();
                        throwItem(entity, itementity);
                        if (entity.captureDrops() != null) entity.captureDrops().add(itementity);
                        else
                            entity.world.addEntity(itementity);
                    }
                }
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

            private void throwItem(Entity entity, ItemEntity itemEntity)
            {
                Random rand= new Random();
                float f7 = 0.3F;
                float f8 = MathHelper.sin(entity.rotationPitch * ((float)Math.PI / 180F));
                float f2 = MathHelper.cos(entity.rotationPitch * ((float)Math.PI / 180F));
                float f3 = MathHelper.sin(entity.rotationYaw * ((float)Math.PI / 180F));
                float f4 = MathHelper.cos(entity.rotationYaw * ((float)Math.PI / 180F));
                float f5 = rand.nextFloat() * ((float)Math.PI * 2F);
                float f6 = 0.02F * rand.nextFloat();
                itemEntity.setMotion((double)(-f3 * f2 * 0.3F) + Math.cos((double)f5) * (double)f6, (double)(-f8 * 0.3F + 0.1F + (rand.nextFloat() - rand.nextFloat()) * 0.1F), (double)(f4 * f2 * 0.3F) + Math.sin((double)f5) * (double)f6);
            }
        }

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

    public String name;
    public List<RecordTick> ticks;

    public RecordScript(String name)
    {
        this.name = name;
        this.ticks = new ArrayList<>();
    }

    public void addTick(RecordTick tick)
    {
        this.ticks.add(tick);
    }

    public int getMaxTicks()
    {
        return ticks.size();
    }

    public RecordTick getFirstTick()
    {
        if (getMaxTicks() == 0) return null;
        return this.ticks.get(0);
    }

    public RecordTick getLastTick()
    {
        if (getMaxTicks() == 0) return null;
        return this.ticks.get(getMaxTicks() - 1);
    }

}
