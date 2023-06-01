local config = require("std:config")

return {
    scripts = {},
    exports = config.without_underscore()
}
