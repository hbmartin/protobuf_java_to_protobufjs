/*
 * Copyright (c) Harold Martin
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package me.haroldmartin.protobufjavatoprotobufjs.adapter

import com.google.protobuf.DescriptorProtos
import com.google.protobuf.Descriptors
import me.haroldmartin.protobufjavatoprotobufjs.model.Field
import me.haroldmartin.protobufjavatoprotobufjs.model.ReflectedDescriptor
import me.haroldmartin.protobufjavatoprotobufjs.model.RootFullNameAndDescriptors

internal class ProtobufGeneratedMessageToDescriptors(
    private val primaryDescriptor: Descriptors.Descriptor,
    private val reflectedTypes: ReflectedTypes
) {
    private val queuedMessageClasses = mutableListOf<Class<*>>()
    private val messages = mutableMapOf<String, ReflectedDescriptor>()

    fun convert(): RootFullNameAndDescriptors {
        convert(primaryDescriptor)
        while (queuedMessageClasses.size > 0) {
            ClassToRootAndDescriptors(queuedMessageClasses.removeAt(0))?.descriptorMap?.let {
                messages.putAll(it)
            }
        }
        return RootFullNameAndDescriptors(
            rootFullName = primaryDescriptor.fullName,
            descriptorMap = messages
        )
    }

    private fun convert(descriptor: Descriptors.Descriptor) {
        if (descriptor.fullName in messages) return

        messages.put(
            descriptor.fullName to ReflectedDescriptor(
                fields = getInternalFields(descriptor),
                oneOfs = getOneOfs(descriptor),
                enumValues = emptyMap()
            )
        )
    }

    private fun getOneOfs(descriptor: Descriptors.Descriptor): Map<String, List<String>> =
        descriptor.realOneofs.map { oneOfDescriptor ->
            oneOfDescriptor.name to oneOfDescriptor.fields.map { it.name }
        }.toMap()

    private fun getInternalFields(descriptor: Descriptors.Descriptor) =
        descriptor.fields.map {
            val fieldDescriptor = it.toProto()
            Field(
                name = fieldDescriptor.name,
                type = getTypeStringAndQueueUnknown(descriptor, fieldDescriptor),
                id = fieldDescriptor.number,
                label = fieldDescriptor.label.jsString
            )
        }

    private fun getTypeStringAndQueueUnknown(
        parentDescriptor: Descriptors.Descriptor,
        fieldDescriptor: DescriptorProtos.FieldDescriptorProto
    ): String {
        return when {
            fieldDescriptor.hasTypeName() -> {
                // TODO: handle repeated -> list<T>

                reflectedTypes[fieldDescriptor.number]?.let {
                    queuedMessageClasses.add(it)
                }
                fieldDescriptor.typeName
            }
            // TODO: handle groups
            else -> {
                fieldDescriptor.type.jsString
            }
        }
    }

    companion object {
        operator fun invoke(clazz: Class<*>): RootFullNameAndDescriptors? {
            return (clazz.getMethod("getDescriptor").invoke(null) as? Descriptors.Descriptor)?.let {
                val reflectedTypes = GeneratedMessageToReflectedTypes(clazz)
                ProtobufGeneratedMessageToDescriptors(it, reflectedTypes).convert()
            }
        }
    }
}

private fun <K, V> MutableMap<K, V>.put(pair: Pair<K, V>) {
    put(pair.first, pair.second)
}

private val DescriptorProtos.FieldDescriptorProto.Type.jsString: String
    get() = toString().removePrefix("TYPE_").toLowerCase()

private val DescriptorProtos.FieldDescriptorProto.Label.jsString: String
    get() = toString().removePrefix("LABEL_").toLowerCase()
