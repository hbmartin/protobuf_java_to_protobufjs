/*
 * Copyright (c) Harold Martin
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package me.haroldmartin.protobufjavatoprotobufjs.adapter

import me.haroldmartin.protobufjavatoprotobufjs.model.Field
import me.haroldmartin.protobufjavatoprotobufjs.model.FullNamedMessages
import me.haroldmartin.protobufjavatoprotobufjs.model.NestedMessages

object FullNamedMessagesToNestedMessages {
    operator fun invoke(messages: FullNamedMessages?): NestedMessages {
        if (messages == null) return mutableMapOf()

        val nestedDefinitions = mutableMapOf<String, Any>()

        for ((fullName, fields) in messages.entries) {
            nestedDefinitions.putMessage(fullName, fields)
        }

        return nestedDefinitions
    }
}

private const val NESTED = "nested"

private fun MutableMap<String, Any>.putMessage(fullName: String, fields: List<Field>) {
    val splitName = fullName.split(".")
    val innerMost = nestPackages(splitName.dropLast(1))
    innerMost[NESTED] = mutableMapOf<String, Any>().also { nested ->
        nested[splitName.last()] = mutableMapOf<String, Any>().also { messageMap ->
            messageMap["fields"] = mutableMapOf<String, Any>().also { fieldsMap ->
                fields.forEach { field ->
                    fieldsMap[field.name] = field.propertiesMap()
                }
            }
        }
    }
}

private fun MutableMap<String, Any>.nestPackages(splitName: List<String>): MutableMap<String, Any> {
    if (splitName.isEmpty()) return this

    if (NESTED !in this) {
        put(NESTED, mutableMapOf<String, Any>())
    }

    return (this[NESTED] as MutableMap<String, Any>).let { nested ->
        if (splitName[0] !in nested) {
            nested[splitName[0]] = mutableMapOf<String, Any>()
        }
        (nested[splitName[0]] as MutableMap<String, Any>).let { inner ->
            inner.nestPackages(splitName.drop(1))
        }
    }
}
