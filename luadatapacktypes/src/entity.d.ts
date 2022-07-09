/// <reference path='vec3d.d.ts'/>

/**
 * Wrapper for Minecraft's Entity class
 */
interface Entity {
    /**
     * Get position of an entity
     */
    get_pos() : Vec3d;

    /**
     * Set the position of an entity
     * 
     * @remarks
     * Uses `/tp` to set position
     * 
     * @param pos Position
     */
    set_pos(pos : Vec3d) : void;

    /**
     * Get remaining air
     */
    get_air() : number;

    /**
     * Set remaining air
     * @param air New air
     */
    set_air(air : number) : void;
    
    /**
     * Is the entity living (i.e. has health)
     */
    is_living_entity() : boolean;

    /**
     * Gets the current health of the entity
     * 
     * @remarks
     * Entity must be living
     * @see {@link is_living_entity}
     */
    get_hp() : number | null;

    /**
     * Sets the health of the entity
     * 
     * @remarks
     * Entity must be living
     * @see {@link is_living_entity}
     * 
     * @param amount Amount
     */
    set_hp(amount : number) : void;
}
