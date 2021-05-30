/*
 * Copyright (c) Harold Martin
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package me.haroldmartin.protobufjavatoprotobufjs.adapter

import me.haroldmartin.protobufjavatoprotobufjs.model.NamedDescriptorMap
import me.haroldmartin.protobufjavatoprotobufjs.model.ReflectedDescriptor

typealias JsDescriptor = Map<String, Any>

object FullNamedMessagesToNestedMessages {
    operator fun invoke(descriptorMap: NamedDescriptorMap?): JsDescriptor {
        if (descriptorMap == null) return mutableMapOf()

        val nestedDefinitions = mutableMapOf<String, Any>()

        for ((fullName, fields) in descriptorMap.entries) {
            nestedDefinitions.putMessage(fullName, fields)
        }

        return nestedDefinitions
    }
}

private const val NESTED = "nested"

private fun MutableMap<String, Any>.putMessage(fullName: String, reflectedDescriptor: ReflectedDescriptor) {
    val splitName = fullName.split(".")
    val innerMost = nestPackages(splitName.dropLast(1))
    (innerMost[NESTED] as MutableMap<String, Any>).also { nested ->
        nested[splitName.last()] = mutableMapOf<String, Any>().also { messageMap ->
            if (reflectedDescriptor.fields.isNotEmpty()) {
                messageMap["fields"] = mutableMapOf<String, Any>().also { fieldsMap ->
                    reflectedDescriptor.fields.forEach { field ->
                        fieldsMap[field.name] = field.propertiesMap()
                    }
                }
            }
            if (reflectedDescriptor.oneOfs.isNotEmpty()) {
                messageMap["oneofs"] = mutableMapOf<String, Any>().also { oneOfsMap ->
                    reflectedDescriptor.oneOfs.forEach { oneOf ->
                        oneOfsMap[oneOf.key] = mapOf("oneof" to oneOf.value)
                    }
                }
            }
            if (reflectedDescriptor.enumValues.isNotEmpty()) {
                messageMap["values"] = reflectedDescriptor.enumValues
            }
        }
    }
}

private fun MutableMap<String, Any>.nestPackages(splitName: List<String>): MutableMap<String, Any> {
    if (NESTED !in this) {
        put(NESTED, mutableMapOf<String, Any>())
    }

    if (splitName.isEmpty()) return this

    return (this[NESTED] as MutableMap<String, Any>).let { nested ->
        if (splitName[0] !in nested) {
            nested[splitName[0]] = mutableMapOf<String, Any>()
        }
        (nested[splitName[0]] as MutableMap<String, Any>).let { inner ->
            inner.nestPackages(splitName.drop(1))
        }
    }
}
