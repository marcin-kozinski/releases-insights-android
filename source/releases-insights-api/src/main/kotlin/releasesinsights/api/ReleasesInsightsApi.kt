package releasesinsights.api

import dev.zacsweers.metro.Inject
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.plugins.resources.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.resources.Resource
import io.ktor.serialization.kotlinx.json.json
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.char
import kotlinx.datetime.serializers.FormattedInstantSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

sealed interface ApiResponse<Content> {
    data class Success<Content>(val content: Content) : ApiResponse<Content>

    data class HttpError<Content>(val status: Int) : ApiResponse<Content>

    data class Error<Content>(val throwable: Throwable) : ApiResponse<Content>
}

interface ReleasesInsightsApi {
    suspend fun getReleaseSchedule(version: Version): ApiResponse<ReleaseSchedule>

    suspend fun getEsrReleases(): ApiResponse<Map<Version, LocalDate>>

    @Serializable
    @JvmInline
    value class Version(val name: String) {
        companion object {
            val Release = Version("release")
            val Beta = Version("beta")
            val Nightly = Version("nightly")
        }
    }

    @OptIn(ExperimentalTime::class)
    @Serializable
    data class ReleaseSchedule(val version: String, val release: ReleaseInstant)
}

private suspend inline fun <reified Content> response(
    apiCall: () -> HttpResponse
): ApiResponse<Content> {
    return try {
        val httpResponse = apiCall()
        if (httpResponse.status == HttpStatusCode.OK) {
            ApiResponse.Success(httpResponse.body())
        } else {
            ApiResponse.HttpError(httpResponse.status.value)
        }
    } catch (t: Throwable) {
        ApiResponse.Error(t)
    }
}

@Inject
class KtorApi(baseClient: HttpClient, url: String) : ReleasesInsightsApi {
    private val client =
        baseClient.config {
            install(Resources)
            defaultRequest { url(url) }
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }

    private object Api {
        object Release {
            @Resource("/release/schedule/")
            data class Schedule(val version: ReleasesInsightsApi.Version)
        }

        object Esr {
            @Resource("/esr/releases/") class Releases
        }
    }

    override suspend fun getReleaseSchedule(
        version: ReleasesInsightsApi.Version
    ): ApiResponse<ReleasesInsightsApi.ReleaseSchedule> {
        return response { client.get(Api.Release.Schedule(version)) }
    }

    override suspend fun getEsrReleases():
        ApiResponse<Map<ReleasesInsightsApi.Version, LocalDate>> {
        return response { client.get(Api.Esr.Releases()) }
    }
}

@OptIn(ExperimentalTime::class)
typealias ReleaseInstant = @Serializable(with = ReleaseInstantSerializer::class) Instant

val ReleaseInstantFormat =
    DateTimeComponents.Format {
        date(LocalDate.Formats.ISO)
        char(' ')
        time(LocalTime.Formats.ISO)
        offset(UtcOffset.Formats.ISO)
    }

private object ReleaseInstantSerializer :
    FormattedInstantSerializer("ReleaseInstant", ReleaseInstantFormat)
