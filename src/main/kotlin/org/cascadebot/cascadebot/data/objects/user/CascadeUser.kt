/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */
package org.cascadebot.cascadebot.data.objects.user

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import de.bild.codec.annotations.Id
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.cascadebot.cascadebot.CascadeBot
import org.cascadebot.cascadebot.data.Config
import org.cascadebot.cascadebot.data.objects.donation.Flag
import org.cascadebot.cascadebot.data.objects.donation.Tier

class CascadeUser(@field:Id val userId: Long) {

    private constructor() : this(0L)

    val tierName = "default"
    val tier: Tier
        get() = Tier.getTier(tierName)!!

    val blackList: MutableList<Long> = mutableListOf();
    val flags: MutableList<Flag> = mutableListOf();

    fun update(): Boolean {
        return if (Config.INS.patreonServiceUrl != null) {
            val request = Request.Builder()
                    .url(Config.INS.patreonServiceUrl + "/user/" + userId + "/update")
                    .method("POST", null)
                    .addHeader("Authorization", "Bearer " + Config.INS.patreonServiceKey)
                    .build()
            val response = CascadeBot.INS.httpClient.newCall(request).execute()
            response.code == 200
        } else {
            // Ignore for now
            false
        }
    }

    //region User Flags
    fun addFlag(flag: Flag): Boolean {
        return if (Config.INS.patreonServiceUrl != null) {
            val jsonObject = JsonObject()
            val addFlags = JsonArray()
            addFlags.add(flag.id)
            jsonObject.add("add", addFlags)
            val requestBody = CascadeBot.getGSON().toJson(jsonObject).toRequestBody()
            val request = Request.Builder()
                    .url(Config.INS.patreonServiceUrl + "/user/" + userId + "/flags")
                    .method("PATCH", requestBody)
                    .addHeader("Authorization", "Bearer " + Config.INS.patreonServiceKey)
                    .build()
            val response = CascadeBot.INS.httpClient.newCall(request).execute()
            response.code == 200
        } else {
            flags.add(flag)
        }
    }

    fun removeFlag(flag: Flag): Boolean {
        return if (Config.INS.patreonServiceUrl != null) {
            val jsonObject = JsonObject()
            val removeFlags = JsonArray()
            removeFlags.add(flag.id)
            jsonObject.add("remove", removeFlags)
            val requestBody = CascadeBot.getGSON().toJson(jsonObject).toRequestBody()
            val request = Request.Builder()
                    .url(Config.INS.patreonServiceUrl + "/user/" + userId + "/flags")
                    .method("PATCH", requestBody)
                    .addHeader("Authorization", "Bearer " + Config.INS.patreonServiceKey)
                    .build()
            val response = CascadeBot.INS.httpClient.newCall(request).execute()
            response.code == 200
        } else {
            flags.remove(flag)
        }
    }

    fun addFlags(flags: List<Flag>): Boolean {
        return if (Config.INS.patreonServiceUrl != null) {
            val jsonObject = JsonObject()
            val addFlags = CascadeBot.getGSON().toJsonTree(flags);
            jsonObject.add("add", addFlags)
            val requestBody = CascadeBot.getGSON().toJson(jsonObject).toRequestBody()
            val request = Request.Builder()
                    .url(Config.INS.patreonServiceUrl + "/user/" + userId + "/flags")
                    .method("PATCH", requestBody)
                    .addHeader("Authorization", "Bearer " + Config.INS.patreonServiceKey)
                    .build()
            val response = CascadeBot.INS.httpClient.newCall(request).execute()
            response.code == 200
        } else {
            this.flags.addAll(flags)
        }
    }

    fun removeFlags(flags: List<Flag>): Boolean {
        return if (Config.INS.patreonServiceUrl != null) {
            val jsonObject = JsonObject()
            val removeFlags = CascadeBot.getGSON().toJsonTree(flags);
            jsonObject.add("remove", removeFlags)
            val requestBody = CascadeBot.getGSON().toJson(jsonObject).toRequestBody()
            val request = Request.Builder()
                    .url(Config.INS.patreonServiceUrl + "/user/" + userId + "/flags")
                    .method("PATCH", requestBody)
                    .addHeader("Authorization", "Bearer " + Config.INS.patreonServiceKey)
                    .build()
            val response = CascadeBot.INS.httpClient.newCall(request).execute()
            response.code == 200
        } else {
            this.flags.addAll(flags)
        }
    }

    fun clearFlags(): Boolean {
        return if (Config.INS.patreonServiceUrl != null) {
            val jsonObject = JsonObject()
            jsonObject.add("clear", JsonPrimitive(true))
            val requestBody = CascadeBot.getGSON().toJson(jsonObject).toRequestBody()
            val request = Request.Builder()
                    .url(Config.INS.patreonServiceUrl + "/user/" + userId + "/flags")
                    .method("PATCH", requestBody)
                    .addHeader("Authorization", "Bearer " + Config.INS.patreonServiceKey)
                    .build()
            val response = CascadeBot.INS.httpClient.newCall(request).execute()
            response.code == 200
        } else {
            flags.clear();
            true
        }
    }
    //endregion

}