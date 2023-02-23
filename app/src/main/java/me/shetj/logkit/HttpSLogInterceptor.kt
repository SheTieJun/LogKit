package me.shetj.logkit

import java.io.IOException
import java.net.URLDecoder
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.TimeUnit.NANOSECONDS
import okhttp3.Connection
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.MediaType
import okhttp3.Protocol.HTTP_1_1
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.internal.http.HttpHeaders
import okio.Buffer

/**
 *
 * 描述：设置日志拦截器
 * 提供了详细、易懂的日志打印<br></br>
 */
class HttpSLogInterceptor : Interceptor {
    private fun log(message: String?) {
        message?.let { SLog.i("okhttp", it) }
    }

    @Throws(IOException::class)
    override fun intercept(chain: Chain): Response {
        val request = chain.request()
        //请求日志拦截
        logForRequest(request, chain.connection())

        //执行请求，计算请求时间
        val startNs = System.nanoTime()
        val response: Response = try {
            chain.proceed(request)
        } catch (e: Exception) {
            log("<-- HTTP FAILED: $e")
            throw e
        }
        val tookMs = NANOSECONDS.toMillis(System.nanoTime() - startNs)

        //响应日志拦截
        return logForResponse(response, tookMs)
    }

    @Throws(IOException::class)
    private fun logForRequest(request: Request, connection: Connection?) {
        val requestBody = request.body()
        val hasRequestBody = requestBody != null
        val protocol = if (connection != null) connection.protocol() else HTTP_1_1
        try {
            val requestStartMessage = "--> " + request.method() + ' ' + URLDecoder.decode(
                request.url().url().toString(),
                UTF8.name()
            ) + ' ' + protocol
            val headers = request.headers()
            if (hasRequestBody) {
                if (isPlaintext(requestBody!!.contentType())) {
                    val bodyToString = bodyToString(request)
                    log("$requestStartMessage\n Header:$headers$bodyToString")
                } else {
                    log("$requestStartMessage\n Header:$headers\tbody: maybe [file part] , too large too print , ignored!")
                }
            } else {
                log(requestStartMessage)
            }
        } catch (e: Exception) {
            e(e)
        }
    }

    private fun logForResponse(response: Response, tookMs: Long): Response {
        val builder = response.newBuilder()
        val clone = builder.build()
        var responseBody = clone.body()
        try {
            val request = "<-- " + clone.code() + ' ' + clone.message() + ' ' + URLDecoder.decode(
                clone.request().url().url().toString(), UTF8.name()
            ) + " (" + tookMs + "ms）\n\n"
            if (HttpHeaders.hasBody(clone)) {
                if (isPlaintext(responseBody!!.contentType())) {
                    val body = responseBody.string()
                    log("$request\tbody:$body")
                    responseBody = ResponseBody.create(responseBody.contentType(), body)
                    return response.newBuilder().body(responseBody).build()
                } else {
                    log("$request\tbody: maybe [file part] , too large too print , ignored!")
                }
            }
        } catch (e: Exception) {
            e(e)
        }
        return response
    }

    private fun bodyToString(request: Request): String {
        return try {
            val copy = request.newBuilder().build()
            val buffer = Buffer()
            copy.body()!!.writeTo(buffer)
            var charset = UTF8
            val contentType = copy.body()!!.contentType()
            if (contentType != null) {
                charset = contentType.charset(UTF8)
            }
            "\tbody:" + URLDecoder.decode(buffer.readString(charset), UTF8.name())
        } catch (e: Exception) {
            e.printStackTrace()
            e.stackTraceToString()
        }
    }

    fun e(t: Throwable) {
        SLog.e("okhttp", t.stackTraceToString())
    }

    companion object {
        private val UTF8 = Charset.forName("UTF-8")

        /**
         * Returns true if the body in question probably contains human readable text. Uses a small sample
         * of code points to detect unicode control characters commonly used in binary file signatures.
         */
        fun isPlaintext(mediaType: MediaType?): Boolean {
            if (mediaType == null) return false
            if (mediaType.type() == "text") {
                return true
            }
            var subtype = mediaType.subtype()
            subtype = subtype.lowercase(Locale.getDefault())
            if (subtype.contains("x-www-form-urlencoded") ||
                subtype.contains("json") ||
                subtype.contains("xml") ||
                subtype.contains("html")
            ) {
                return true
            }
            return false
        }
    }
}