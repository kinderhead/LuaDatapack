local config = require("std:config")

return {
    executables = {},
    imports = config.without_underscore()
}
