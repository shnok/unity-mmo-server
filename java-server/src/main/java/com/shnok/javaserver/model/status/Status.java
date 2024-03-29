package com.shnok.javaserver.model.status;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public abstract class Status {
    protected int hp;
    protected int maxHp;
    protected int level;
    protected int moveSpeed = 5;

    protected Status(int level, int maxHp) {
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.level = level;
    }
}
