/*
 * Copyright (c) Harold Martin
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package me.haroldmartin.protobufjavatoprotobufjs.model

data class Descriptor(
    val fields: List<Field>,
    val oneOfs: Map<String, List<String>>
)
