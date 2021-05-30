/*
 * Copyright (c) Harold Martin
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package me.haroldmartin.protobufjavatoprotobufjs.adapter

import com.google.protobuf.Descriptors
import me.haroldmartin.protobufjavatoprotobufjs.model.ReflectedDescriptor
import me.haroldmartin.protobufjavatoprotobufjs.model.RootFullNameAndDescriptors

internal object ProtobufEnumToDescriptors {
    operator fun invoke(clazz: Class<*>): RootFullNameAndDescriptors? {
        return (clazz.getMethod("getDescriptor").invoke(null) as? Descriptors.EnumDescriptor)?.let { enumDescriptor ->
            val descriptor = ReflectedDescriptor(
                fields = emptyList(),
                oneOfs = emptyMap(),
                enumValues = enumDescriptor.values.map { it.name to it.index }.toMap()
            )
            RootFullNameAndDescriptors(
                rootFullName = enumDescriptor.fullName,
                descriptorMap = mapOf(enumDescriptor.fullName to descriptor)
            )
        }
    }
}
