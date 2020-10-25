/*
 * Copyright (c) 2020 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */
const fs = require("fs")

const config = require("testerconfig.json")
const file = require(config.sourceFile)

function redact(object) {
    for (let key of Object.keys(object)) {
        if (object[key].constructor === Object) {
            redact(object[key])
        } else if (config.ignoreArrays && object[key].constructor === Array) {
            continue
        } else {
            if (!config.keyBlacklist.includes(key)) {
                object[key] = config.textToReplace
            }
        }
    }
    return object
}

fs.writeFileSync(config.destinationFile, JSON.stringify(redact(file), null, 2))