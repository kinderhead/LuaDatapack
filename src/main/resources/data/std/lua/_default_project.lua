local config = require("std:config")

return {
    executables = config.without_underscore(),
    imports = {}
}
