package com.shnok.javaserver.model.entity;

import com.shnok.javaserver.Config;
import com.shnok.javaserver.dto.ServerPacket;
import com.shnok.javaserver.dto.serverpackets.EntitySetTargetPacket;
import com.shnok.javaserver.dto.serverpackets.ObjectMoveToPacket;
import com.shnok.javaserver.dto.serverpackets.ObjectPositionPacket;
import com.shnok.javaserver.enums.EntityMovingReason;
import com.shnok.javaserver.enums.Event;
import com.shnok.javaserver.model.GameObject;
import com.shnok.javaserver.model.Point3D;
import com.shnok.javaserver.model.knownlist.EntityKnownList;
import com.shnok.javaserver.model.status.Status;
import com.shnok.javaserver.model.template.EntityTemplate;
import com.shnok.javaserver.pathfinding.Geodata;
import com.shnok.javaserver.pathfinding.MoveData;
import com.shnok.javaserver.pathfinding.PathFinding;
import com.shnok.javaserver.service.GameTimeControllerService;
import com.shnok.javaserver.service.ThreadPoolManagerService;
import com.shnok.javaserver.thread.ai.BaseAI;
import com.shnok.javaserver.util.VectorUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;

/**
 * This class represents all entities in the world.<BR>
 * <BR>
 * Such as : Players or AIs<BR>
 * <BR>
 */

@NoArgsConstructor
@Log4j2
@Getter
@Setter
public abstract class Entity extends GameObject {
    protected boolean canMove = true;
    protected MoveData moveData;
    protected BaseAI ai;
    protected EntityTemplate template;
    protected Status status;
    protected boolean moving;
    protected long attackEndTime;

    public Entity(int id) {
        super(id);
    }

    public void inflictDamage(Entity attacker, int value) {
        if(getAi() != null) {
            getAi().notifyEvent(Event.ATTACKED, attacker);
        }

        status.setHp(Math.max(status.getHp() - value, 0));

        if (status.getHp() == 0) {
            onDeath();
        }
    }

    public abstract void setStatus(Status status);

    public abstract boolean canMove();

    public void onDeath() {
        log.debug("[{}] Entity died", getId());
        if (ai != null) {
            ai.notifyEvent(Event.DEAD);
        }

        setCanMove(false);
        //TODO: stop hp mp regen
        //TODO: give exp?
        //TODO: Share HP
    }

    public void doAttack(Entity target) {
        System.out.println("doAttack");
        if (!canAttack()) {
            return;
        }

        // Get the Attack Speed of the npc (delay (in milliseconds) before next attack)
        int timeAtk = calculateTimeBetweenAttacks();
        // the hit is calculated to happen halfway to the animation
        int timeToHit = timeAtk / 2;

        attackEndTime = GameTimeControllerService.getInstance().getGameTicks();
        attackEndTime += (timeAtk / GameTimeControllerService.getInstance().getTickDurationMs());
        attackEndTime -= 1;


        // Start hit task
        doSimpleAttack(target, timeToHit);
    }

    public void doSimpleAttack(Entity target, int timeToHit) {
        //TODO do damage calculations
        int damage = 15;
        boolean criticalHit = true;

        log.debug("ouchie?");
        ThreadPoolManagerService.getInstance().scheduleAi(new ScheduleHitTask(target, damage, criticalHit), timeToHit);
    }

    public void onHitTimer(Entity target, int damage, boolean criticalHit) {
        log.debug("ouchie!");
        //TODO do apply damage
        //TODO share hit
        //TODO share hp
    }

    public boolean isAttacking() {
        return attackEndTime > GameTimeControllerService.getInstance().getGameTicks();
    }

    public boolean canAttack() {
        return !isAttacking();
    }

    // Return the Attack Speed of the L2Character (delay (in milliseconds) before next attack)
    public int calculateTimeBetweenAttacks() {
        //Todo calculate attack speed
        return 1000;
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    public EntityKnownList getKnownList() {
        if ((super.getKnownList() == null) || !(super.getKnownList() instanceof EntityKnownList)) {
            setKnownList(new EntityKnownList(this));
        }
        return ((EntityKnownList) super.getKnownList());
    }

    @Override
    public void setPosition(Point3D position) {
        super.setPosition(position);

        // Update known list
        float distanceDelta = VectorUtils.calcDistance(
                getPosition().getWorldPosition(), getPosition().getLastWorldPosition());
        if(distanceDelta > 4.0f) {
            getKnownList().forceRecheckSurroundings();
            getPosition().setLastWorldPosition(getPosition().getWorldPosition());
        }
    }

    public boolean moveTo(Point3D destination) {
        return moveTo(destination, 0);
    }

    public boolean moveTo(Point3D destination, float stopAtRange) {
        //System.out.println("AI find path: " + x + "," + y + "," + z);
        if (!isOnGeoData()) {
            log.debug("[{}] Not on geodata", getId());
            return false;
        }

        if(!canMove) {
            return false;
        }

        moveData = new MoveData();

        /* find path using pathfinder */
        if (moveData.path == null || moveData.path.size() == 0) {
            if(Config.PATHFINDER_ENABLED) {

                moveData.path = PathFinding.getInstance().findPath(getPosition().getWorldPosition(), destination, stopAtRange);
                if(Config.PRINT_PATHFINDER_LOGS) {
                    log.debug("[{}] Found path length: {}", getId(), moveData.path.size());
                }
            } else {
                moveData.path =  new ArrayList<>();
                moveData.path.add(new Point3D(destination));
            }
        }

        /* check if path was found */
        if (moveData.path == null || moveData.path.size() == 0) {
            return false;
        }

        if(Config.PRINT_PATHFINDER_LOGS) {
            log.debug("[{}] Move to {} reason {}", getId(),destination, getAi().getMovingReason());
        }

        moveToNextRoutePoint();
        GameTimeControllerService.getInstance().addMovingObject(this);

        moving = true;
        return true;
    }

    // calculate how many ticks do we need to move to destination
    public boolean moveToNextRoutePoint() {
        float speed;

        if(!canMove() || getAi() == null) {
            return false;
        }

        /* safety */
        if (moveData == null || moveData.path == null || moveData.path.size() == 0) {
            return false;
        }

        if(getAi().getMovingReason() == EntityMovingReason.Walking) {
            speed = getTemplate().getBaseWalkSpd();
        } else {
            speed = getTemplate().getBaseRunSpd();
        }

        getStatus().setMoveSpeed(speed);

        if (speed <= 0) {
            return false;
        }

        /* cancel the move action if not on geodata */
        if (!isOnGeoData()) {
            moveData = null;
            return false;
        }

        updateMoveData(speed);

        Point3D destination = new Point3D(moveData.destination);

        /* send destination to known players */
        ObjectMoveToPacket packet = new ObjectMoveToPacket(
                getId(),
                destination,
                getStatus().getMoveSpeed(),
                getAi().getMovingReason() == EntityMovingReason.Walking);
        broadcastPacket(packet);

        return true;
    }

    private void updateMoveData(float moveSpeed) {
        Point3D destination = new Point3D(moveData.path.get(0));
        float distance = VectorUtils.calcDistance(getPos(), destination);
        Point3D delta = new Point3D(destination.getX() - getPosX(),
                destination.getY() - getPosY(),
                destination.getZ() - getPosZ());
        Point3D deltaT = new Point3D(delta.getX() / distance,
                delta.getY() / distance,
                delta.getZ() / distance);

        float ticksPerSecond = GameTimeControllerService.getInstance().getTicksPerSecond();
        // calculate the number of ticks between the current position and the destination
        moveData.ticksToMove = 1 + (int) ((ticksPerSecond * distance) / moveSpeed);

        // calculate the distance to travel for each tick
        moveData.xSpeedTicks = (deltaT.getX() * moveSpeed) / ticksPerSecond;
        moveData.ySpeedTicks = (deltaT.getY() * moveSpeed) / ticksPerSecond;
        moveData.zSpeedTicks = (deltaT.getZ() * moveSpeed) / ticksPerSecond;

        moveData.startPosition = new Point3D(getPos());
        moveData.destination = destination;
        moveData.moveStartTime = GameTimeControllerService.getInstance().getGameTicks();
        moveData.path.remove(0);
    }

    public boolean isOnGeoData() {
        try {
            Geodata.getInstance().getNodeAt(getPos());
            return true;
        } catch (Exception e) {
//            log.debug("[{}] Not at a valid position: {}", getId(), getPos());
            return false;
        }
    }

    public boolean updatePosition(long gameTicks) {
        if (moveData == null) {
            return true;
        }

        if (moveData.moveTimestamp == gameTicks) {
            return false;
        }

        // calculate the time since started moving
        long elapsed = gameTicks - moveData.moveStartTime;

        // lerp entity position between the start position and destination based on server ticks elapsed
        Point3D lerpPosition = VectorUtils.lerpPosition(
                moveData.startPosition,
                moveData.destination,
                (float) elapsed / moveData.ticksToMove);
        setPosition(lerpPosition);

        if (elapsed >= moveData.ticksToMove) {
            moveData.moveTimestamp = gameTicks;

            /* share new position with known players */
            ObjectPositionPacket packet = new ObjectPositionPacket(getId(), getPos());
            broadcastPacket(packet);

            if (moveData.path.size() > 0) {
                moveToNextRoutePoint();
                return false;
            }

            if (ai != null) {
                ai.notifyEvent(Event.ARRIVED);
            }

            return true;
        }

        return false;
    }

    public void broadcastPacket(ServerPacket packet) {
        for (PlayerInstance player : getKnownList().getKnownPlayers().values()) {
            sendPacketToPlayer(player, packet);
        }
    }

    public boolean shareCurrentAction(PlayerInstance player) {
        if(getAi() == null) {
            return false;
        }

        sendPacketToPlayer(player, new ObjectPositionPacket(getId(), getPosition().getWorldPosition()));

        // Share current target with player
        if(getAi().getTarget() != null) {
            sendPacketToPlayer(player, new EntitySetTargetPacket(getId(), getAi().getTarget().getId()));
        }
        return true;
    }

    public void sendPacketToPlayer(PlayerInstance player, ServerPacket packet) {
        if(!player.sendPacket(packet)) {
            log.warn("Packet could not be sent to player");
            getKnownList().removeKnownObject(player);
        }
    }

    protected static class ScheduleDestroyTask implements Runnable {
        private final Entity entity;

        public ScheduleDestroyTask(Entity entity){
            this.entity = entity;
        }

        @Override
        public void run() {
            log.debug("Execute schedule destroy object");
            if (entity != null) {
                entity.destroy();
            }
        }
    }

    private class ScheduleHitTask implements Runnable {
        private final Entity hitTarget;
        private final int damage;
        private final boolean criticalHit;

        public ScheduleHitTask(Entity hitTarget, int damage, boolean criticalHit) {
            this.hitTarget = hitTarget;
            this.damage = damage;
            this.criticalHit = criticalHit;
        }

        @Override
        public void run() {
            try {
                onHitTimer(hitTarget, damage, criticalHit);
            } catch (Throwable e) {
                log.error(e);
            }
        }
    }
}
