/*
 * Copyright (c) Harold Martin
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package me.haroldmartin.protobufjavatoprotobufjs.model

internal data class ReflectedDescriptor(
    val fields: TypeFields,
    val oneOfs: OneOfs,
    val enumValues: EnumValues
)

internal typealias TypeFields = List<Field>

internal typealias OneOfs = Map<String, List<String>>

internal typealias EnumValues = Map<String, Int>

internal typealias NamedDescriptorMap = Map<String, ReflectedDescriptor>

internal data class RootFullNameAndDescriptors(
    val rootFullName: String,
    val descriptorMap: NamedDescriptorMap
)
