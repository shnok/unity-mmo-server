ALTER TABLE PUBLIC.NPC MODIFY COLUMN ATTACKRANGE DECIMAL(10,2);
ALTER TABLE PUBLIC.NPC MODIFY COLUMN COLLISION_RADIUS DECIMAL(10,2);
ALTER TABLE PUBLIC.NPC MODIFY COLUMN COLLISION_HEIGHT DECIMAL(10,2);
ALTER TABLE PUBLIC.NPC MODIFY COLUMN AGGRO_RANGE DECIMAL(10,2);
ALTER TABLE PUBLIC.NPC MODIFY COLUMN FACTION_RANGE DECIMAL(10,2);

UPDATE PUBLIC.NPC
SET 
ATTACKRANGE=ATTACKRANGE/52.5,
COLLISION_RADIUS=COLLISION_RADIUS/52.5,
COLLISION_HEIGHT=COLLISION_HEIGHT/52.5,
ATTACKRANGE=ATTACKRANGE/52.5,
AGGRO_RANGE=AGGRO_RANGE/52.5,
FACTION_RANGE=FACTION_RANGE/52.5;