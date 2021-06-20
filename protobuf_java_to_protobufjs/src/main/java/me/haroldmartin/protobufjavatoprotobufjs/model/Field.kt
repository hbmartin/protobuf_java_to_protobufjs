/*
 * Copyright (c) Harold Martin
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package me.haroldmartin.protobufjavatoprotobufjs.model

internal data class Field(
    val name: String,
    val type: String,
    val id: Int,
    val label: String,
    val keyType: String? = null
) {
    fun propertiesMap(): Map<String, Any> {
        return mutableMapOf<String, Any>().apply {
            put("type", type)
            put("id", id)
            put("rule", label)
            keyType?.let { put("keyType", it) }
        }
    }
}
