package org.schabi.newpipe.extractor.services.youtube.extractors;

import static org.schabi.newpipe.extractor.services.youtube.YoutubeParsingHelper.getJsonPostResponse;
import static org.schabi.newpipe.extractor.services.youtube.YoutubeParsingHelper.getTextAtKey;
import static org.schabi.newpipe.extractor.services.youtube.YoutubeParsingHelper.prepareDesktopJsonBuilder;
import static org.schabi.newpipe.extractor.utils.Utils.UTF_8;
import static org.schabi.newpipe.extractor.utils.Utils.isNullOrEmpty;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonWriter;

import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.Page;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.downloader.Downloader;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.kiosk.KioskExtractor;
import org.schabi.newpipe.extractor.linkhandler.ListLinkHandler;
import org.schabi.newpipe.extractor.localization.TimeAgoParser;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.schabi.newpipe.extractor.stream.StreamInfoItemsCollector;

import java.io.IOException;

import javax.annotation.Nonnull;

/**
 * created by lijinping on 2021/10/9 10:55
 * desc:点赞视频列表
 */
public class YoutubeLikeExtractor extends KioskExtractor<StreamInfoItem> {
    private JsonObject initialData;

    public YoutubeLikeExtractor(final StreamingService service,
                                    final ListLinkHandler linkHandler,
                                    final String kioskId) {
        super(service, linkHandler, kioskId);
    }

    @Override
    public void onFetchPage(@Nonnull final Downloader downloader) throws IOException, ExtractionException {
        // @formatter:off
        final byte[] body = JsonWriter.string(prepareDesktopJsonBuilder(getExtractorLocalization(),
                getExtractorContentCountry())
                .value("browseId", "VLLL")
                .done())
                .getBytes(UTF_8);
        // @formatter:on

        initialData = getJsonPostResponse("browse", body, getExtractorLocalization());

        NewPipe.parseLoginStatus(initialData);
    }

    @Override
    public InfoItemsPage<StreamInfoItem> getPage(final Page page) {
        return InfoItemsPage.emptyPage();
    }

    @Nonnull
    @Override
    public String getName() throws ParsingException {
        final JsonObject header = initialData.getObject("header");
        String name = "like";
        if (header.has("feedTabbedHeaderRenderer")) {
            name = getTextAtKey(header.getObject("feedTabbedHeaderRenderer"), "title");
        } else if (header.has("c4TabbedHeaderRenderer")) {
            name = getTextAtKey(header.getObject("c4TabbedHeaderRenderer"), "title");
        }

        if (isNullOrEmpty(name)) {
            throw new ParsingException("Could not get Home name");
        }
        return name;
    }

    @Nonnull
    @Override
    public InfoItemsPage<StreamInfoItem> getInitialPage() {
        StreamInfoItemsCollector collector = new StreamInfoItemsCollector(getServiceId());
        final TimeAgoParser timeAgoParser = getTimeAgoParser();
        JsonArray sectionListRenderers = initialData.getObject("contents")
                .getObject("twoColumnBrowseResultsRenderer").getArray("tabs").getObject(0)
                .getObject("tabRenderer").getObject("content").getObject("sectionListRenderer")
                .getArray("contents");

        for (final Object itemSectionRenderer : sectionListRenderers) {
            JsonArray itemRenderers = ((JsonObject) itemSectionRenderer)
                    .getObject("itemSectionRenderer").getArray("contents");
            for (final Object itemRenderer : itemRenderers) {
                JsonObject playlist = ((JsonObject) itemRenderer)
                        .getObject("playlistVideoListRenderer");
                for (final Object playlistRenderer : playlist.getArray("contents")) {
                    final JsonObject videoInfo = ((JsonObject) playlistRenderer).getObject("playlistVideoRenderer");
                    collector.commit(new YoutubeStreamInfoItemExtractor(videoInfo, timeAgoParser));
                }
            }
        }

        return new InfoItemsPage<>(collector, null);
    }
}
