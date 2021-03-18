/*
 * Copyright (c) Harold Martin
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package me.haroldmartin.protobuf_java_to_protobufjs.adapter

import me.haroldmartin.protobuf_java_to_protobufjs.model.ReflectedField
import java.lang.reflect.Modifier

internal object GeneratedMessageToReflectedTypes {
    operator fun invoke(clazz: Class<*>): Map<Int, Class<*>> {
        val fields: MutableMap<String, ReflectedField> = mutableMapOf()

        for (field in clazz.declaredFields) {
            if (Modifier.isPrivate(field.modifiers) && field.name.endsWith("_")) {
                fields[field.name.removeSuffix("_")] = ReflectedField(
                    type = field.type
                )
            }
        }

        for (field in clazz.declaredFields) {
            if (Modifier.isStatic(field.modifiers)) {
                if (field.type.isAssignableFrom(Integer.TYPE)) {
                    val name = field.name.removeSuffix("_FIELD_NUMBER").toLowerCase()
                    fields[name]?.let {
                        fields[name] = it.copy(id = field.getValue())
                    }
                }
            }
        }

        return fields.values.mapNotNull { field ->
            field.id?.let { it to field.type }
        }.toMap()
    }
}

private fun java.lang.reflect.Field.getValue(): Int {
    // lite id fields are accessible but full/java are not
    return if (!isAccessible) {
        isAccessible = true
        val id = getInt(null)
        isAccessible = false
        id
    } else {
        getInt(null)
    }
}
