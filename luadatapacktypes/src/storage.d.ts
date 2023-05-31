/** @noSelfInFile */

interface Storage {
    /**
     * Saves an object to the world storage.
     * 
     * @remarks
     * Use namespaced ids like `foo:bar/baz`, or any other string
     * 
     * @param path Namespaced ID
     * @param obj Object
     */
    save(path : string, obj : any) : void;

    /**
     * Gets an object in world storage.
     * 
     * @remarks
     * Use namespaced ids like `foo:bar/baz`, or any other string
     * 
     * @param path Namespaced ID
     */
    load(path : string) : any;
}
