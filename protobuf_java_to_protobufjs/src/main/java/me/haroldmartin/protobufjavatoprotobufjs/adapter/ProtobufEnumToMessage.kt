/*
 * Copyright (c) Harold Martin
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package me.haroldmartin.protobufjavatoprotobufjs.adapter

import com.google.protobuf.Descriptors
import me.haroldmartin.protobufjavatoprotobufjs.model.ReflectedDescriptor
import me.haroldmartin.protobufjavatoprotobufjs.model.RootFullNameAndMessages

object ProtobufEnumToMessage {
    operator fun invoke(clazz: Class<*>): RootFullNameAndMessages? {
        return (clazz.getMethod("getDescriptor").invoke(null) as? Descriptors.EnumDescriptor)?.let { enumDescriptor ->
            val descriptor = ReflectedDescriptor(
                fields = emptyList(),
                oneOfs = emptyMap(),
                enumValues = enumDescriptor.values.map { it.name to it.index }.toMap()
            )
            RootFullNameAndMessages(
                rootFullName = enumDescriptor.fullName,
                descriptorMap = mapOf(enumDescriptor.fullName to descriptor)
            )
        }
    }
}
