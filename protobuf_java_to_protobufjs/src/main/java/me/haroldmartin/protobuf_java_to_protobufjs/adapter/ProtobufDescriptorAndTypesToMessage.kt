/*
 * Copyright (c) Harold Martin
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package me.haroldmartin.protobuf_java_to_protobufjs.adapter

import com.google.protobuf.DescriptorProtos
import com.google.protobuf.Descriptors
import me.haroldmartin.protobuf_java_to_protobufjs.ProtobufGeneratedJavaToProtobufJs
import me.haroldmartin.protobuf_java_to_protobufjs.model.Field
import me.haroldmartin.protobuf_java_to_protobufjs.model.Message
import me.haroldmartin.protobuf_java_to_protobufjs.model.RootFullNameAndMessages

internal class ProtobufDescriptorToMessageModel(
    private val primaryDescriptor: Descriptors.Descriptor,
    private val reflectedTypes: Map<Int, Class<*>>
) {
    private val queuedMessageClasses = mutableListOf<Class<*>>()
    private val messages = mutableMapOf<String, Message>()

    fun convert(): RootFullNameAndMessages {
        convert(primaryDescriptor)
        while (queuedMessageClasses.size > 0) {
            ProtobufGeneratedJavaToProtobufJs(queuedMessageClasses.removeAt(0))?.messages?.let {
                messages.putAll(it)
            }
        }
        return RootFullNameAndMessages(
            rootFullName = primaryDescriptor.fullName,
            messages = messages
        )
    }

    private fun convert(descriptor: Descriptors.Descriptor) {
        if (descriptor.fullName in messages) return
        println("convert: ${descriptor.fullName}")
        val message = descriptor.fields.map {
            val fieldDescriptor = it.toProto()
            Field(
                name = fieldDescriptor.name,
                type = getTypeStringAndQueueUnknown(descriptor, fieldDescriptor),
                id = fieldDescriptor.number,
                label = fieldDescriptor.label.jsString
            )
        }

        messages.put(descriptor.fullName to message)
    }

    private fun getTypeStringAndQueueUnknown(
        parentDescriptor: Descriptors.Descriptor,
        fieldDescriptor: DescriptorProtos.FieldDescriptorProto
    ): String {
        return when {
            fieldDescriptor.hasTypeName() -> {
                // TODO: handle enums
                // TODO: handle repeated -> list<T>
                println("hasTypeName :: ${fieldDescriptor.typeName} -> ${parentDescriptor.findFieldByName(fieldDescriptor.name)}")
                println("${fieldDescriptor.number} -> ${reflectedTypes[fieldDescriptor.number]}")
//                    queuedDescriptors.add(parentDescriptor.findFieldByName(fieldDescriptor.name))
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
}

private fun <K, V> MutableMap<K, V>.put(pair: Pair<K, V>) {
    put(pair.first, pair.second)
}

private val DescriptorProtos.FieldDescriptorProto.Type.jsString: String
    get() = toString().removePrefix("TYPE_").toLowerCase()

private val DescriptorProtos.FieldDescriptorProto.Label.jsString: String
    get() = toString().removePrefix("LABEL_").toLowerCase()
