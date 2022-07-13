![Logo](https://raw.githubusercontent.com/kinderhead/LuaDatapack/master/logo.png)

[![Build](https://img.shields.io/github/workflow/status/kinderhead/LuaDatapack/build)](https://github.com/kinderhead/LuaDatapack/actions)
[![Releases](https://img.shields.io/github/v/release/kinderhead/LuaDatapack?include_prereleases&sort=semver)](https://github.com/kinderhead/LuaDatapack/releases)

# LuaDatapack

Allows lua scripts to be ran in Minecraft

Note: LuaDatapack is in alpha, so the api is subject to change

## Usage

Create a new datapack with the `lua` subfolder. For example: `data/foo/lua/bar.lua`

## Features

* Uses [Cobalt](https://github.com/SquidDev/Cobalt) under the hood, which gives fast execution times
* Scripts can be included from datapacks using `require` and use Minecraft's namespaced id system
* Supports a few common utility libraries
    * [json.lua](https://github.com/rxi/json.lua)

## Commands

### `/lua`

Usage: `/lua <name>`

Example, to run `data/foo/lua/bar.lua`: `/lua foo:bar`

## Api

The api reference can be found [here](https://kinderhead.github.io/LuaDatapack/). Although it is for [TypeScriptToLua](https://typescripttolua.github.io/), it can be adapted for lua

## Standard library

A few utility libraries are included by default

|Library|Path|
|---|---|
|[json.lua](https://github.com/rxi/json.lua)|`std:json`|

## Contributing

Any help is appreciated. Issues and feature requests can be shared [here](https://github.com/kinderhead/LuaDatapack/issues)

### Things to do
* Optimize lua api
* Use commands less (except `say`, as it is faster for some reason)

## Planned features

* NBT manipulation
* More commands
* More standard libraries
* Development tools with [TypeScriptToLua](https://typescripttolua.github.io/)

## Links

Source: https://github.com/kinderhead/LuaDatapack

Modrinth: https://modrinth.com/mod/luadatapack

Api reference: https://kinderhead.github.io/LuaDatapack
