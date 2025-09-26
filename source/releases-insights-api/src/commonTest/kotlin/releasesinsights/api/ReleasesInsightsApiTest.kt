package releasesinsights.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.MockRequestHandler
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpResponseData
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.append
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.format
import kotlinx.datetime.toInstant
import releasesinsights.api.ReleasesInsightsApi.Version

@OptIn(ExperimentalTime::class)
class ReleasesInsightsApiTest {
    fun api(handler: MockRequestHandler): ReleasesInsightsApi {
        return KtorApi(HttpClient(MockEngine(handler)), "https://releases-insights.api.test/")
    }

    @Test
    fun `handles actual release schedule`() = runTest {
        val api = api { request ->
            assertEquals("/release/schedule/", request.url.encodedPath)
            assertTrue(request.url.parameters.contains("version"))
            assertEquals("release", request.url.parameters["version"])

            respondJson(
                """{"version":"137.0","release":"2025-04-01 14:00:00+00:00","unexpected_field":null}"""
            )
        }

        expectingContent(api.getReleaseSchedule(Version.Release)) { content ->
            assertEquals(
                ReleasesInsightsApi.ReleaseSchedule(
                    "137.0",
                    LocalDate(2025, Month.APRIL, 1).atTime(14, 0).toInstant(TimeZone.UTC),
                ),
                content,
            )
        }
    }

    @Test
    fun `handles forecasted release schedule`() = runTest {
        val futureDate = Clock.System.now() + 14.days

        val api = api { request ->
            assertEquals("/release/schedule/", request.url.encodedPath)
            assertTrue(request.url.parameters.contains("version"))
            assertEquals("beta", request.url.parameters["version"])

            respondJson(
                """{"version":"999.0","release":"${futureDate.format(ReleaseInstantFormat)}","unexpected_field":null}"""
            )
        }

        expectingContent(api.getReleaseSchedule(Version.Beta)) { content ->
            assertEquals(ReleasesInsightsApi.ReleaseSchedule("999.0", futureDate), content)
        }
    }

    @Test
    fun `handles esr releases`() = runTest {
        val api = api { request ->
            assertEquals("/esr/releases/", request.url.encodedPath)

            respondJson(
                """{"115.21.1":"2025-03-27","128.8.1":"2025-03-27","115.22.0":"2025-04-01","128.9.0":"2025-04-01"}"""
            )
        }

        expectingContent(api.getEsrReleases()) { content ->
            assertEquals(
                buildMap {
                    put(Version("115.21.1"), LocalDate(2025, Month.MARCH, 27))
                    put(Version("128.8.1"), LocalDate(2025, Month.MARCH, 27))
                    put(Version("115.22.0"), LocalDate(2025, Month.APRIL, 1))
                    put(Version("128.9.0"), LocalDate(2025, Month.APRIL, 1))
                },
                content,
            )
        }
    }

    @Test
    fun `handles no esr releases`() = runTest {
        val api = api { request ->
            assertEquals("/esr/releases/", request.url.encodedPath)

            respondJson("{}")
        }

        expectingContent(api.getEsrReleases()) { content -> assertEquals(emptyMap(), content) }
    }
}

fun MockRequestHandleScope.respondJson(content: String): HttpResponseData {
    val headers = Headers.build { append(HttpHeaders.ContentType, ContentType.Application.Json) }
    return respond(content, headers = headers)
}

fun <Content> expectingContent(response: ApiResponse<Content>, block: (Content) -> Unit) {
    when (response) {
        is ApiResponse.Error<*> -> throw response.throwable
        is ApiResponse.HttpError<*> -> throw RuntimeException("HTTP ${response.status}")
        is ApiResponse.Success<Content> -> block(response.content)
    }
}
