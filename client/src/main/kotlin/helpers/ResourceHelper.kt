package cs346.whiteboard.client.helpers

import java.io.InputStream

fun getResource(name: String): InputStream = object {}.javaClass.getResourceAsStream(name)