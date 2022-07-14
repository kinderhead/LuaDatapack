// This is copied to the run folder for testing

let json : Json = require("std:json");

get_blockentity({x:0, y:0, z:0}).inventory.set(2, {id: "dirt", count: 64, nbt:{}});
