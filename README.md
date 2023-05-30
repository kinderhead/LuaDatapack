![Logo](https://raw.githubusercontent.com/kinderhead/LuaDatapack/master/logo.png)

[![Releases](https://img.shields.io/github/v/release/kinderhead/LuaDatapack?include_prereleases&sort=semver)](https://github.com/kinderhead/LuaDatapack/releases)

# LuaDatapack

Allows lua scripts to be ran in Minecraft

Note: LuaDatapack is in beta, so the api is subject to change

## Usage

Create a new datapack with the `lua` subfolder. For example: `data/foo/lua/bar.lua`

## Features

* Uses [Cobalt](https://github.com/SquidDev/Cobalt) under the hood, which gives fast execution times
* Scripts can be included from datapacks using `require` and use Minecraft's namespaced id system
* [TypeScriptToLua](https://typescripttolua.github.io/) supprt
* Supports a few common utility libraries
    * [json.lua](https://github.com/rxi/json.lua)
    * [class.lua](https://github.com/jonstoler/class.lua)

## Commands

### `/lua`

Usage: `/lua <name> [<args>]`

Example, to run `data/foo/lua/bar.lua`: `/lua foo:bar`

Arguments can be accessed via an array named `args`

## Api

The api reference can be found [here](https://kinderhead.github.io/LuaDatapack/)

## Standard library

A few utility libraries are included by default

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

Note: If you prefer a different OOP library, you can simply add one to the datapack

## Contributing

Building this repo is the same as any other Fabric mod. There may be issues with libraries not loading when starting the game, but running the game though the VSCode debugger works for some reason

### Things to do

* Optimize lua api
* Add more api features
* Use commands less internally (except `say`, as it is faster for some reason)

## Planned features

* More commands
* Development tools with [TypeScriptToLua](https://typescripttolua.github.io/)

## Links

Source: https://github.com/kinderhead/LuaDatapack

Modrinth: https://modrinth.com/mod/luadatapack

Api reference: https://kinderhead.github.io/LuaDatapack
