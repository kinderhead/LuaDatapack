/** @noSelfInFile */

/// <reference path='vec3d.d.ts'/>
/// <reference path='entity.d.ts'/>

/**
 * Runs the `say` command
 * @param msg Message
 */
declare function say(msg : string) : void;

/**
 * Teleports the current entity
 * @param location Location
 */
declare function tp(location : Vec3d) : void;

/**
 * Teleports an entity
 * @param entity Entity
 * @param location Location
 */
declare function tp(entity : Entity, location : Vec3d) : void;

declare let teleport : typeof tp;
