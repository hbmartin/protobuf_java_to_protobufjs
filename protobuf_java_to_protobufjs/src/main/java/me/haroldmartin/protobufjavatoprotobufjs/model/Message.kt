/*
 * Copyright (c) Harold Martin
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package me.haroldmartin.protobufjavatoprotobufjs.model

typealias NamedDescriptorMap = Map<String, ReflectedDescriptor>

data class RootFullNameAndDescriptors(
    val rootFullName: String,
    val descriptorMap: NamedDescriptorMap
)
