package me.miquiis.record.common.models;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;

public class HitRecordTickEvent extends RecordTickEvent {

    private float damage;

    public HitRecordTickEvent(float damage) {
        this.damage = damage;
    }

    @Override
    public void execute(Entity entity) {
        super.execute(entity);
        if (entity instanceof LivingEntity)
        {
            LivingEntity livingEntity = (LivingEntity) entity;
            livingEntity.attackEntityFrom(DamageSource.GENERIC, damage);
        }
    }

    public float getDamage() {
        return damage;
    }
}