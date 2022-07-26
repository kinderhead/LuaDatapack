## 0.7.0-beta

* Separated a couple of standard lua libraries. They are no longer instantiated every time `/lua` is ran, improving performance
    * `std:math`
    * `std:string`
    * `std:bit32`

## 0.6.0-beta

* New standard library: `class.lua`. If you prefer a different OOP library, you can add one to the datapack
* Top level return with `run()`
* Api features
    * `entity.add_effect`
    * `entity.clear_effects`
* Bug fixes
    * Certain api calls displayed output in the chat. This is no longer the case

## 0.5.0-beta

* Api features
    * Nbt types for tstl
        * `NbtElement`
        * `NbtCompound`
        * `NbtList`
    * Inventory api
    * `entity.inventory`
    * `get_blockentity`
    * `Inventory`
    * `ItemStack`
    * `BlockEntity`

## 0.4.1-beta

* Quick fix to include icon in mod for compatibility with mod menus
    * Also fixed a couple of config settings

## 0.4.0-beta

* Beta release
* Script arguments
* Api features
    * `args`
* Bug fixes
    * Fixed errors not displaying in chat
    * Many minor things

## 0.3.1-alpha

* Command autocomplete
* Player nbt editing

## 0.3.0-alpha

* NBT!
* Api features
    * `entity.get_nbt`
    * `entity.merge_nbt`

## 0.2.0-alpha

* Api features
    * `entity.get_armor`
    * `entity.get_age`
    * `entity.set_age`
    * `get_block`
    * `set_block`

## 0.1.0-alpha

* First release!
* `/lua` command to run scripts
* A few api features, viewable [here](https://kinderhead.github.io/LuaDatapack/)
* Standard library includes [json.lua](https://github.com/rxi/json.lua)
