package model

/**
 * 2023/10/7 18:49:50
 * @author yue
 */
data class ProcessInfo(
    val name: String,
    val command: String,
    val pid: Int,
    val port: Int
)
