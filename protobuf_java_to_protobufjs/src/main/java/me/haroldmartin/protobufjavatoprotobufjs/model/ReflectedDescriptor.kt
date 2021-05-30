/*
 * Copyright (c) Harold Martin
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package me.haroldmartin.protobufjavatoprotobufjs.model

data class ReflectedDescriptor(
    val fields: TypeFields,
    val oneOfs: OneOfs,
    val enumValues: EnumValues
)

typealias TypeFields = List<Field>

typealias OneOfs = Map<String, List<String>>

typealias EnumValues = Map<String, Int>
