local config = require("std:config")

return {
    callables = config.without_underscore(),
    importables = {}
}
