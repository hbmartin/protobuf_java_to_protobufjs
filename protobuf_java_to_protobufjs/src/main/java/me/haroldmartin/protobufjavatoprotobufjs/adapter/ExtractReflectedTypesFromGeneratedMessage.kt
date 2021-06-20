/*
 * Copyright (c) Harold Martin
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package me.haroldmartin.protobufjavatoprotobufjs.adapter

import com.google.protobuf.Internal
import com.google.protobuf.MapField
import com.google.protobuf.ProtocolMessageEnum
import java.lang.reflect.Modifier.isPrivate
import java.lang.reflect.Modifier.isStatic
import java.lang.reflect.ParameterizedType
import me.haroldmartin.protobufjavatoprotobufjs.model.ReflectedField
import me.haroldmartin.protobufjavatoprotobufjs.model.ReflectedFieldsList

internal object ExtractReflectedTypesFromGeneratedMessage {
    operator fun invoke(clazz: Class<*>): ReflectedFieldsList {
        val fields: MutableMap<String, ReflectedField> = mutableMapOf()

        addFields(clazz, fields)
        addOneOfs(clazz, fields)

        return fields.values
            .filter { (!it.type.isPrimitive || it.keyClass != null) && it.id != null }
    }

    private fun addFields(clazz: Class<*>, fields: MutableMap<String, ReflectedField>) {
        setFieldsFromClassPrivateFields(clazz, fields)
        setFieldIdsFromClassFieldNumber(clazz, fields)
    }

    private fun setFieldsFromClassPrivateFields(
        clazz: Class<*>,
        fields: MutableMap<String, ReflectedField>
    ) {
        for (field in clazz.declaredFields) {
            if (isPrivate(field.modifiers) && field.name.endsWith("_")) {
                if (field.type.isMapField) {
                    val parameterizedType = field.genericType as ParameterizedType
                    (parameterizedType.actualTypeArguments[1] as? Class<*>)?.let { valueClass ->
                        fields[field.name.removeSuffix("_")] = ReflectedField(
                            keyClass = parameterizedType.actualTypeArguments[0] as Class<*>,
                            type = valueClass
                        )
                    }
                } else {
                    fields[field.name.removeSuffix("_")] = ReflectedField(
                        type = field.type,
                    )
                }
            }
        }
    }

    private fun setFieldIdsFromClassFieldNumber(
        clazz: Class<*>,
        fields: MutableMap<String, ReflectedField>
    ) {
        for (field in clazz.declaredFields) {
            if (isStatic(field.modifiers) && field.type.isAssignableFrom(Integer.TYPE)) {
                val name = field.name.removeSuffix("_FIELD_NUMBER").toLowerCase().snakeToCamelCase()
                fields.update(name) { it.copy(id = field.getValue()) }
            }
        }
    }

    private fun addOneOfs(clazz: Class<*>, fields: MutableMap<String, ReflectedField>) {
        setFieldsMapFromEnums(clazz, fields)

        clazz.declaredClasses.filter { it.isEnum }.forEach { enumClass ->
            ifOneOfSetId(enumClass, fields)
            ifMessageEnumSetTypeOnExistingField(enumClass, fields)
        }
    }

    private fun setFieldsMapFromEnums(
        clazz: Class<*>,
        fields: MutableMap<String, ReflectedField>
    ) {
        clazz.declaredMethods.filter {
            it.name.startsWith("get") &&
                !it.name.endsWith("OrBuilder") &&
                !it.returnType.name.startsWith("com.google.protobuf") &&
                it.returnType.name != clazz.name &&
                (it.returnType.isGeneratedMessageV3Subclass || it.returnType.isMessageEnumSubclass)
        }.forEach {
            val key = it.returnType.name.split(".").last().camelToSnakeCase().toUpperCase()
            fields[key] = ReflectedField(type = it.returnType)
        }
    }

    private fun ifOneOfSetId(
        enumClass: Class<*>,
        fields: MutableMap<String, ReflectedField>
    ) {
        enumClass.enumConstants.filterClass(Internal.EnumLite::class.java).forEach { lite ->
            (lite as? Enum<*>)?.run {
                fields.update(name) { it.copy(id = lite.number) }
            }
        }
    }

    private fun ifMessageEnumSetTypeOnExistingField(
        enumClass: Class<*>,
        fields: MutableMap<String, ReflectedField>
    ) {
        if (ProtocolMessageEnum::class.java.isAssignableFrom(enumClass)) {
            val key = enumClass.simpleName.toLowerCase()
            fields.update(key) { it.copy(type = enumClass) }
        }
    }
}

private fun <K, V> MutableMap<K, V>.update(name: K, recipe: (V) -> V) {
    getOrElse(name, { null })?.let {
        put(name, recipe(it))
    }
}

private inline fun <T, reified FILTER> Array<T>.filterClass(clazz: Class<FILTER>): List<FILTER> =
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

private val Class<*>.isMapField: Boolean
    get() = MapField::class.java.isAssignableFrom(this)

private val camelRegex = "(?<=[a-zA-Z])[A-Z]".toRegex()

private fun String.camelToSnakeCase(): String {
    return camelRegex.replace(this) {
        "_${it.value}"
    }.toLowerCase()
}

private fun String.snakeToCamelCase() =
    split('_').joinToString("", transform = String::capitalize).decapitalize()
