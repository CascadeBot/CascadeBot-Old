const file = require("./en-GB.json")
const fs = require("fs")

let string = "dave"

function redact(object) {
    for (let key of Object.keys(object)) {
        if (object[key].constructor === Object) {
            redact(object[key])
        } else if (object[key].constructor === Array) {
            continue
        } else {
            if (key === "command") continue
            object[key] = string
        }
    }
    return object
}

fs.writeFileSync("fr-FR.json", JSON.stringify(redact(file), null, 2))