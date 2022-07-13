declare type NbtCompound = {[key: string]: NbtElement};
declare type NbtList<T extends NbtElement> = T[];
declare type NbtElement = string | number | boolean | NbtCompound | NbtList<any>;
