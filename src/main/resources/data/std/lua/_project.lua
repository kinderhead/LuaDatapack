local config = require("std:config")

return {
    callables = {},
    importables = config.without_underscore()
}
