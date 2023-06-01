local config = require("std:config")

return {
    scripts = config.without_underscore(),
    exports = {}
}
