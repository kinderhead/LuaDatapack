local string = require("std:string")

local config = {}

function config.without_underscore()
    local ret = {}

    for idex, i in ipairs(files) do
        if not string.find(i, "_") then
            table.insert(ret, files[idex])
        end
    end

    return ret
end

return config
