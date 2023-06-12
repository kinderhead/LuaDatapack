![Logo](https://raw.githubusercontent.com/kinderhead/LuaDatapack/master/logo.png)

[![Releases](https://img.shields.io/github/v/release/kinderhead/LuaDatapack?include_prereleases&sort=semver)](https://github.com/kinderhead/LuaDatapack/releases)
[![Downloads](https://img.shields.io/modrinth/dt/bgUHPLzW)](https://modrinth.com/mod/luadatapack)

# LuaDatapack

Allows lua scripts to be ran in Minecraft.

Note: LuaDatapack is in beta, so the api is subject to change.

## Usage

Create a new datapack with the `lua` subfolder. For example: `data/foo/lua/bar.lua`.

## Features

* Uses [Cobalt](https://github.com/SquidDev/Cobalt) under the hood, which gives fast execution times
* Scripts can be included from datapacks using `require` and use Minecraft's namespaced id system
* [TypeScriptToLua](https://typescripttolua.github.io/) support
* Supports a few common utility libraries
    * [json.lua](https://github.com/rxi/json.lua)
    * [class.lua](https://github.com/jonstoler/class.lua)

## Commands

### `/lua`

Usage: `/lua <name> [<args>]`

Example, to run `data/foo/lua/bar.lua`: `/lua foo:bar`

Arguments can be accessed via an array called `args` in the lua script.

## Api

The api reference can be found [here](https://kinderhead.github.io/LuaDatapack/).

## Standard library

A few utility libraries are included by default.

|Library|Path|
|---|---|
|[json.lua](https://github.com/rxi/json.lua)|`std:json`|
|[class.lua](https://github.com/jonstoler/class.lua)|`std:class`|
|Standard lua library|`std:math`|
|Standard lua library|`std:string`|
|Standard lua library|`std:bit32`|
|Standard lua library|`std:utf8`|
|Commands library|`std:commands`|
|Storage library|`std:storage`|
|Configuration utilities library|`std:config`|

Note: If you prefer a different OOP library, you can simply add one to your datapack.

## Project system

There is a simple project system which allows for customization of which scripts can be executed and imported by external projects (scripts not in the same namespace).

To customize your project, add a `_project.lua` file to your namespace. For example if the namespace was `foo` then you would make a `foo:_project.lua`:

``` lua
return {
    scripts = {"foo:bar"},
    exports = {"foo:baz"},
    depends = {"hello"}
}
```

In the above example `foo:bar` is a script to be ran via `/lua`, `foo:baz` is a script that any file can `require`, and it requires the `hello` project to function. Scripts can import any script in the same namespace, even if it is not included in the `exports` array. If a project does not include a `_project.lua`, then the default `std:_default_datapack` is used instead:
``` lua
local config = require("std:config")

return {
    scripts = config.without_underscore(),
    exports = {}
}
```

Note: not all fields need to be explicitly defined.

If a project fails to load, all scripts in it are unable to load.

## Contributing

Building this repo is the same as any other Fabric mod. There may be issues with libraries (Cobalt, Apache commons-io, etc) not loading when starting the game, but running the game though the VSCode debugger works for some reason.

### Things to do

* Optimize lua api
* Add more api features
* Use commands less internally

## Planned features

* More commands
* Development tools with [TypeScriptToLua](https://typescripttolua.github.io/)

## Links

Source: https://github.com/kinderhead/LuaDatapack

Modrinth: https://modrinth.com/mod/luadatapack

Api reference: https://kinderhead.github.io/LuaDatapack
