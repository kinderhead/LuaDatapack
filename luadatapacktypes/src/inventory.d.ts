/// <reference path='nbt.d.ts'/>

/**
 * Wrapper for Minecraft's Inventory class
 */
interface Inventory {
    /**
     * Size of the stack. Max 64
     */
    readonly size : number;

    /**
     * Gets an item at slot
     * @param i Index
     */
    get(i : number) : ItemStack;

    /**
     * Sets a slot to an item stack
     * @param i Index
     * @param stack ItemStack
     */
    set(i : number, stack : ItemStack) : void;

    /**
     * Clears the inventory
     */
    clear() : void;
}

interface ItemStack {
    id : string;
    count : number;
    nbt : NbtCompound;
}
