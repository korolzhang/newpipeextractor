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
 * created by nice on 2021/9/26 19:41
 * desc:
 */
public class YoutubeHistoryExtractor extends KioskExtractor<StreamInfoItem> {
    private JsonObject initialData;

    public YoutubeHistoryExtractor(final StreamingService service,
                                    final ListLinkHandler linkHandler,
                                    final String kioskId) {
        super(service, linkHandler, kioskId);
    }

    @Override
    public void onFetchPage(@Nonnull final Downloader downloader) throws IOException, ExtractionException {
        // @formatter:off
        final byte[] body = JsonWriter.string(prepareDesktopJsonBuilder(getExtractorLocalization(),
                getExtractorContentCountry())
                .value("browseId", "FEhistory")
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
        String name = "history";
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
            JsonObject item = ((JsonObject) itemSectionRenderer)
                    .getObject("itemSectionRenderer");
            for (final Object ul : item.getArray("contents")) {
                final JsonObject videoInfo = ((JsonObject) ul).getObject("videoRenderer");
                collector.commit(new YoutubeStreamInfoItemExtractor(videoInfo, timeAgoParser));
            }
        }

        JsonArray secondaryContents = initialData.getObject("contents")
                .getObject("twoColumnBrowseResultsRenderer").getObject("secondaryContents").getObject("browseFeedActionsRenderer").getArray("contents");
        for (final Object secondaryContent: secondaryContents) {
            if (((JsonObject)secondaryContent).has("buttonRenderer")) {
                JsonObject buttonRenderer = ((JsonObject)secondaryContent).getObject("buttonRenderer");
                String iconType = buttonRenderer.getObject("icon").getString("iconType");
                if (iconType.equals("DELETE")) {
                    String feedbackToken = buttonRenderer.getObject("navigationEndpoint").getObject("confirmDialogEndpoint").getObject("content").getObject("confirmDialogRenderer").getObject("confirmEndpoint").getObject("feedbackEndpoint").getString("feedbackToken");
                    NewPipe.setFeedbackToken(feedbackToken);
                    break;
                }
            }
        }

        return new InfoItemsPage<>(collector, null);
    }
}
