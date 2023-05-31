/// <reference path='vec3d.d.ts'/>
/// <reference path='entity.d.ts'/>

/**
 * Wrapper class for Minecraft's ServerCommandSource
 */
interface Source {
    readonly position : Vec3d;
    readonly name : string;
    readonly entity : Entity;
}
