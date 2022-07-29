/** @noSelfInFile */

/// <reference path='vec3d.d.ts'/>
/// <reference path='entity.d.ts'/>

interface Commands {
    /**
     * Runs the `say` command
     * @param msg Message
     */
    say(msg : string | number | boolean) : void;

    /**
     * Teleports the current entity
     * @param location Location
     */
    tp(location : Vec3d) : void;

    /**
     * Teleports an entity
     * @param entity Entity
     * @param location Location
     */
    tp(entity : Entity, location : Vec3d) : void;

    /**
     * Teleports the current entity
     * @param location Location
     */
    teleport(location : Vec3d) : void;

    /**
     * Teleports an entity
     * @param entity Entity
     * @param location Location
     */
    teleport(entity : Entity, location : Vec3d) : void;
}
