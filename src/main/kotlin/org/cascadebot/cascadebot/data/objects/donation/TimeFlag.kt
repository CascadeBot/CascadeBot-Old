package org.cascadebot.cascadebot.data.objects.donation

import com.google.gson.JsonObject
import lombok.Getter
import org.cascadebot.cascadebot.data.language.Language.i18n
import org.cascadebot.cascadebot.data.language.Locale
import org.cascadebot.cascadebot.utils.FormatUtils

class TimeFlag : DataFlag {
    private var mills: Long = 0

    private constructor() : super() {}
    constructor(id: String?, scope: FlagScope?) : super(id, scope) {}

    override fun parseFlagData(flagDataObject: JsonObject): DataFlag {
        mills = flagDataObject["time"].asLong
        return this
    }

    override fun toString(): String {
        return "TimeFlag(id=$id, time=$mills)"
    }

    override fun getDescription(locale: Locale?): String? {
        return i18n(locale!!, "flags.$id.description", FormatUtils.formatLongTimeMills(mills))
    }

    override fun compareTo(flag: DataFlag): Int {
        if (flag !is TimeFlag) {
            throw UnsupportedOperationException("Cannot compare different types of flags")
        }
        return mills.compareTo(flag.mills);
    }
}