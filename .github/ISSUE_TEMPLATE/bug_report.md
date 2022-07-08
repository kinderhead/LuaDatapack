---
name: Bug report
about: A report to describe something not working expectedly
title: ''
labels: bug
assignees: kinderhead

---

**Problematic script or function**
```
say({foo="bar"})
```

**Error message or response**
`Cannot convert type "table" to type "string"`

**Expected outcome**
`[Server] foo="bar"}`

**Work around**
```
local json = require("std:json")
say(json.encode({foo="bar"}))
```
