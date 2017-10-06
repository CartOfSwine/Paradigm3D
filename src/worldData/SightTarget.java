package worldData;
public enum SightTarget{
   ALLY,       //all creatures of the same species
   ENEMY,      //all creatures of other species
   ROBIT,   //all creatures in general
   ENERGY,       //all energy sources
   CORPSE,     //corpse obstruction blocks. (note, this finds corpses, they may or may not be looted already)
   OBSTRUCTION;//all obstructions. look at the ObstructionType enum in the World class file for all types
}