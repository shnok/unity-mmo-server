ALTER TABLE PUBLIC.SPAWNLIST MODIFY COLUMN LOCX DECIMAL(10,2);
ALTER TABLE PUBLIC.SPAWNLIST MODIFY COLUMN LOCY DECIMAL(10,2);
ALTER TABLE PUBLIC.SPAWNLIST MODIFY COLUMN LOCZ DECIMAL(10,2);
ALTER TABLE PUBLIC.SPAWNLIST MODIFY COLUMN RANDOMX DECIMAL(10,2);
ALTER TABLE PUBLIC.SPAWNLIST MODIFY COLUMN RANDOMY DECIMAL(10,2);
ALTER TABLE PUBLIC.SPAWNLIST MODIFY COLUMN HEADING DECIMAL(10,2);

UPDATE PUBLIC.SPAWNLIST
SET 
LOCX=LOCY/52.5, 
LOCY=LOCZ/52.5, 
LOCZ=LOCX/52.5, 
RANDOMX=RANDOMY/52.5, 
RANDOMY=RANDOMX/52.5,
HEADING=HEADING/182.04,
RESPAWN_DELAY=RESPAWN_DELAY*1000.0;
