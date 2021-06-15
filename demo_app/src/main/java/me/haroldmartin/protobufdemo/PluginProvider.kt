/*
 * Copyright (c) Harold Martin
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package me.haroldmartin.protobufdemo

import com.facebook.flipper.plugins.network.NetworkFlipperPlugin

object PluginProvider {
    val networkFlipperPlugin = NetworkFlipperPlugin()

    val all = listOf(networkFlipperPlugin)
}
