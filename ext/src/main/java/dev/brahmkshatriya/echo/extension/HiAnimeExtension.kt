package dev.brahmkshatriya.echo.extension

import dev.brahmkshatriya.echo.common.clients.AlbumClient
import dev.brahmkshatriya.echo.common.clients.ExtensionClient
import dev.brahmkshatriya.echo.common.clients.HomeFeedClient
import dev.brahmkshatriya.echo.common.clients.SearchFeedClient
import dev.brahmkshatriya.echo.common.clients.TrackClient
import dev.brahmkshatriya.echo.common.helpers.PagedData
import dev.brahmkshatriya.echo.common.models.Album
import dev.brahmkshatriya.echo.common.models.EchoMediaItem
import dev.brahmkshatriya.echo.common.models.EchoMediaItem.Companion.toMediaItem
import dev.brahmkshatriya.echo.common.models.ImageHolder.Companion.toImageHolder
import dev.brahmkshatriya.echo.common.models.QuickSearchItem
import dev.brahmkshatriya.echo.common.models.Shelf
import dev.brahmkshatriya.echo.common.models.Streamable
import dev.brahmkshatriya.echo.common.models.Streamable.Companion.toAudioVideoMedia
import dev.brahmkshatriya.echo.common.models.Streamable.Companion.toSubtitleMedia
import dev.brahmkshatriya.echo.common.models.Tab
import dev.brahmkshatriya.echo.common.models.Track
import dev.brahmkshatriya.echo.common.settings.Setting
import dev.brahmkshatriya.echo.common.settings.Settings
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.Locale

class HiAnimeExtension : ExtensionClient, SearchFeedClient, AlbumClient, TrackClient, HomeFeedClient {
    private val hostUrl = "https://hianime.to"

    override suspend fun onExtensionSelected() {}

    override val settingItems: List<Setting> = emptyList()

    private lateinit var setting: Settings
    override fun setSettings(settings: Settings) {
        setting = settings
    }

    override suspend fun deleteSearchHistory(query: QuickSearchItem.SearchQueryItem) {
        // do nothing
    }

    override fun getShelves(album: Album): PagedData<Shelf> {
        return PagedData.Single { listOf() }
    }

    override fun getShelves(track: Track): PagedData<Shelf> {
        return PagedData.Single { listOf() }
    }

    override suspend fun quickSearch(query: String?): List<QuickSearchItem> {
        // Basic implementation - return empty list for now
        return listOf()
    }

    override fun searchFeed(query: String?, tab: Tab?) = PagedData.Single<Shelf> {
        query ?: return@Single listOf()
        // Basic implementation - return empty list for now
        listOf()
    }

    override suspend fun searchTabs(query: String?): List<Tab> {
        // not needed
        return listOf()
    }

    // get related stuff
    override fun getMediaItems(album: Album): PagedData<Shelf> {
        return PagedData.Single { listOf() }
    }

    // load full album data
    // for example description
    override suspend fun loadAlbum(album: Album): Album {
        // Basic implementation - return album as-is
        return album
    }

    override fun loadTracks(album: Album) = PagedData.Single<Track> {
        // Basic implementation - return empty list for now
        listOf()
    }

    // get related media
    override fun getMediaItems(track: Track): PagedData<Shelf> {
        return PagedData.Single { listOf() }
    }

    override suspend fun getStreamableMedia(streamable: Streamable): Streamable.Media {
        return when (streamable.type) {
            Streamable.MediaType.AudioVideo -> streamable.id.toAudioVideoMedia()
            Streamable.MediaType.Subtitle -> streamable.id.toSubtitleMedia(Streamable.SubtitleType.VTT)
            else -> throw IllegalStateException()
        }
    }

    override suspend fun loadTrack(track: Track): Track {
        // Basic implementation - return track as-is
        return track
    }

    override fun getHomeFeed(tab: Tab?): PagedData<Shelf> = PagedData.Single {
        // Basic implementation - return empty list for now
        listOf()
    }

    override suspend fun getHomeTabs(): List<Tab> {
        return listOf()
    }
}

@Serializable
data class HiAnimeAjaxResponse(
    val status: Boolean,
    val html: String,
    val totalItems: Int? = null,
    val continueWatch: String? = null
)

@Serializable
data class HiAnimeSource(
    val type: String,
    val link: String,
    val server: Int,
    val sources: List<String>? = null,
    val tracks: List<String>? = null,
    val htmlGuide: String
)

@Serializable
data class MegaCloudSource(
    val sources: List<MegaCloudStream>,
    val tracks: List<MegaCloudSubtitle>,
    val encrypted: Boolean,
    val intro: MegaCloudSkip,
    val outro: MegaCloudSkip,
    val server: Int
)

@Serializable
data class MegaCloudStream(
    val file: String,
    val type: String,
)

@Serializable
data class MegaCloudSubtitle(
    val file: String,
    val label: String = "",
    val kind: String,
    val default: Boolean? = null
)

@Serializable
class MegaCloudSkip(
    val start: Int,
    val end: Int
)