/// <reference path='inventory.d.ts'/>

/**
 * Represents a block with an inventory
 */
interface BlockEntity {
    /**
     * Namespaced block id
     */
    readonly id : string;

    /**
     * Inventory, or null if no inventory is found
     */
    readonly inventory : Inventory;
}
