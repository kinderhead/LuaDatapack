/// <reference path='nbt.d.ts'/>

/**
 * Wrapper for Minecraft's Inventory class
 */
interface Inventory {
    readonly size : number;
}

interface ItemStack {
    id : string;
    count : number;
    nbt : NbtElement;
}
