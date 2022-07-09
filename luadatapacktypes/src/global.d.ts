/** @noSelfInFile */

/// <reference path='source.d.ts'/>
/// <reference path='entity.d.ts'/>

/**
 * Data about the executor. Basically equivalent as `@s`
 */
declare let src : Source;

/**
 * Get entites from a target selector
 * @param tag Selector
 */
declare function selector(tag : string) : Array<Entity>;

/**
 * Runs a command using the current source
 * @param cmd Command
 */
declare function command(cmd : string) : void;
