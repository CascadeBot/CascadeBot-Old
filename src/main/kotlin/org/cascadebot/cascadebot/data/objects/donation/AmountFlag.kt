/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */
package org.cascadebot.cascadebot.data.objects.donation

import com.google.gson.JsonObject
import lombok.Getter
import lombok.Setter
import org.cascadebot.cascadebot.data.language.Language.i18n
import org.cascadebot.cascadebot.data.language.Locale

class AmountFlag : DataFlag {
    var amount = 0

    private constructor() : super() {}
    constructor(id: String?, scope: FlagScope?) : super(id, scope) {}

    public override fun parseFlagData(flagDataObject: JsonObject): DataFlag {
        amount = flagDataObject["amount"].asInt
        return this
    }

    override fun toString(): String {
        return "AmountFlag(id=$id, amount=$amount)"
    }

    override fun getDescription(locale: Locale?): String? {
        return i18n(locale!!, "flags.$id.description", amount)
    }

    override fun compareTo(flag: DataFlag): Int {
        if (flag !is AmountFlag) {
            throw UnsupportedOperationException("Cannot compare different types of flags")
        }
        return amount.compareTo(flag.amount);
    }
}