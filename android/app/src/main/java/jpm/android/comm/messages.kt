package jpm.android.comm

import jpm.lib.comm.Message
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException

interface MessageWriter {
    @Throws(IOException::class)
    fun write(message: Message, outStream: DataOutputStream)
}

interface MessageReader {
    @Throws(IOException::class)
    fun read(inStream: DataInputStream): Message
}
