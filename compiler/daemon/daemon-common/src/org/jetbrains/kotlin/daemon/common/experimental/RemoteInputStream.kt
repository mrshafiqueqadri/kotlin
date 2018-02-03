/*
 * Copyright 2000-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.daemon.common.experimental

import java.rmi.Remote
import java.rmi.RemoteException
import org.jetbrains.kotlin.daemon.common.experimental.socketInfrastructure.Server
import org.jetbrains.kotlin.daemon.common.experimental.socketInfrastructure.Server.Message
import io.ktor.network.sockets.Socket
import org.jetbrains.kotlin.daemon.common.experimental.socketInfrastructure.ByteWriteChannelWrapper

interface RemoteInputStream : Server, Remote {

    @Throws(RemoteException::class)
    fun close()

    @Throws(RemoteException::class)
    fun read(length: Int): ByteArray

    @Throws(RemoteException::class)
    fun read(): Int

    // Query messages:
    class CloseMessage : Message<RemoteInputStream> {
        suspend override fun process(server: RemoteInputStream, output: ByteWriteChannelWrapper) =
            server.close()
    }

    class ReadMessage(val length: Int = -1) : Message<RemoteInputStream> {
        suspend override fun process(server: RemoteInputStream, output: ByteWriteChannelWrapper) =
            output.writeObject(if (length == -1) server.read() else server.read(length))
    }

}
