---
name: Bug report
about: A report to describe something not working expectedly
title: ''
labels: bug
assignees: kinderhead

---

**Version**
1.2.3

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
