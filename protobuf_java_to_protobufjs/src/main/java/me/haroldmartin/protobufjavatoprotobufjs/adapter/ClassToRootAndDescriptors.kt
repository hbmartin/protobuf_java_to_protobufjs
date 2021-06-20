/*
 * Copyright (c) Harold Martin
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package me.haroldmartin.protobufjavatoprotobufjs.adapter

import com.google.protobuf.GeneratedMessageV3
import com.google.protobuf.MapField
import com.google.protobuf.ProtocolMessageEnum
import me.haroldmartin.protobufjavatoprotobufjs.model.RootFullNameAndDescriptors

internal object ClassToRootAndDescriptors {
    operator fun invoke(clazz: Class<*>): RootFullNameAndDescriptors? {
        return when {
            clazz.isGeneratedMessageV3Subclass -> ProtobufGeneratedMessageToDescriptors(clazz)
            clazz.isMessageEnumSubclass -> ProtobufEnumToDescriptors(clazz)
            clazz.isMapField -> {
                println(clazz)
                ProtobufMapFieldToDescriptors(clazz)
            }
            else -> null
        }
    }
}

private val Class<*>.isMapField: Boolean
    get() = MapField::class.java.isAssignableFrom(this)

internal val Class<*>.isGeneratedMessageV3Subclass: Boolean
    get() = GeneratedMessageV3::class.java.isAssignableFrom(this)

internal val Class<*>.isMessageEnumSubclass: Boolean
    get() = ProtocolMessageEnum::class.java.isAssignableFrom(this)
