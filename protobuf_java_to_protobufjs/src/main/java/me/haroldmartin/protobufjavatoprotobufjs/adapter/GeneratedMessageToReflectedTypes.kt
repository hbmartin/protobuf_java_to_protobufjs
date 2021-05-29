/*
 * Copyright (c) Harold Martin
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package me.haroldmartin.protobufjavatoprotobufjs.adapter

import com.google.protobuf.GeneratedMessageV3
import com.google.protobuf.Internal
import me.haroldmartin.protobufjavatoprotobufjs.model.ReflectedField
import java.lang.reflect.Modifier.isPrivate
import java.lang.reflect.Modifier.isStatic

internal object GeneratedMessageToReflectedTypes {
    operator fun invoke(clazz: Class<*>): Map<Int, Class<*>> {
        val fields: MutableMap<String, ReflectedField> = mutableMapOf()

        addOneOfs(clazz, fields)
        addFields(clazz, fields)

        return fields.values.mapNotNull { field ->
            field.id?.let { it to field.type }
        }.toMap()
    }

    private fun addFields(clazz: Class<*>, fields: MutableMap<String, ReflectedField>) {
        for (field in clazz.declaredFields) {
            if (isPrivate(field.modifiers) && field.name.endsWith("_")) {
                fields[field.name.removeSuffix("_")] = ReflectedField(
                    type = field.type
                )
            }
        }

        for (field in clazz.declaredFields) {
            if (isStatic(field.modifiers) && field.type.isAssignableFrom(Integer.TYPE)) {
                val name = field.name.removeSuffix("_FIELD_NUMBER").toLowerCase()
                fields.update(name) { it.copy(id = field.getValue()) }
            }
        }
    }

    private fun addOneOfs(clazz: Class<*>, fields: MutableMap<String, ReflectedField>) {
        clazz.declaredMethods.filter {
            it.name.startsWith("get") &&
                !it.name.endsWith("OrBuilder") &&
                !it.returnType.name.startsWith("com.google.protobuf") &&
                it.returnType.name != clazz.name &&
                !it.returnType.isEnum &&
                it.returnType.superclass?.isAssignableFrom(GeneratedMessageV3::class.java) ?: false
        }.forEach {
            val key = it.returnType.name.split(".").last().camelToSnakeCase().toUpperCase()
            fields[key] = ReflectedField(type = it.returnType)
        }

        clazz.declaredClasses.filter { it.isEnum }.forEach { enumClass ->
            enumClass.enumConstants.filterClass(Internal.EnumLite::class.java).forEach { lite ->
                (lite as? Enum<*>)?.run {
                    fields.update(name) { it.copy(id = lite.number) }
                }
            }
        }
    }
}

private fun <K, V> MutableMap<K, V>.update(name: K, recipe: (V) -> V) {
    getOrElse(name, { null })?.let {
        put(name, recipe(it))
    }
}

private fun <T, FILTER> Array<T>.filterClass(clazz: Class<FILTER>): List<FILTER> =
    map { it as? FILTER }.filterNotNull()

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

private val camelRegex = "(?<=[a-zA-Z])[A-Z]".toRegex()

private fun String.camelToSnakeCase(): String {
    return camelRegex.replace(this) {
        "_${it.value}"
    }.toLowerCase()
}
