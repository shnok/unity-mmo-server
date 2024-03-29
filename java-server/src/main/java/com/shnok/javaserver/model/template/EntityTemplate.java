package com.shnok.javaserver.model.template;

import lombok.Data;

@Data
public class EntityTemplate {
    public byte baseSTR;
    public byte baseCON;
    public byte baseDEX;
    public byte baseINT;
    public byte baseWIT;
    public byte baseMEN;
    public int baseHpMax;
    public int baseCpMax;
    public int baseMpMax;
    public float baseHpReg;
    public float baseMpReg;
    public int basePAtk;
    public int baseMAtk;
    public int basePDef;
    public int baseMDef;
    public int basePAtkSpd;
    public int baseMAtkSpd;
    public float baseMReuseRate;
    public float baseAtkRange;
    public int baseCritRate;
    public int baseWalkSpd;
    public int baseRunSpd;
    public float collisionRadius;
    public float collisionHeight;

    public EntityTemplate() {}
}
