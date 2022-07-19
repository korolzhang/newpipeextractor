package org.schabi.newpipe.extractor.services.youtube.extractors;

import static org.schabi.newpipe.extractor.services.youtube.YoutubeParsingHelper.getJsonPostResponse;
import static org.schabi.newpipe.extractor.services.youtube.YoutubeParsingHelper.getTextAtKey;
import static org.schabi.newpipe.extractor.services.youtube.YoutubeParsingHelper.prepareDesktopJsonBuilder;
import static org.schabi.newpipe.extractor.services.youtube.YoutubeParsingHelper.prepareDesktopNextPageJsonBuilder;
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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.Nonnull;

public class YoutubeLiveSubscriptionExtractor extends KioskExtractor<StreamInfoItem> {
    private JsonObject initialData;
    private String visitorData;

    public YoutubeLiveSubscriptionExtractor(final StreamingService service,
                                    final ListLinkHandler linkHandler,
                                    final String kioskId) {
        super(service, linkHandler, kioskId);
    }

    @Override
    public void onFetchPage(@Nonnull final Downloader downloader) throws IOException, ExtractionException {
        // @formatter:off
        final String strBody = JsonWriter.string(prepareDesktopJsonBuilder(getExtractorLocalization(),
                getExtractorContentCountry())
                .value("browseId", "FEsubscriptions")
                .done());
        final byte[] body = strBody.getBytes(UTF_8);
        // @formatter:on

        initialData = getJsonPostResponse("browse", body, getExtractorLocalization());

        NewPipe.parseLoginStatus(initialData);
    }

    @Override
    public InfoItemsPage<StreamInfoItem> getPage(final Page page)   throws IOException, ExtractionException {
        String clickTrackingParams = "", token = "";

        if (page.getCookies() != null) {
            clickTrackingParams = page.getCookies().containsKey("clickTrackingParams")? page.getCookies().get("clickTrackingParams"):"";
            token = page.getCookies().containsKey("token")? page.getCookies().get("token"):"";
            visitorData = page.getCookies().containsKey("visitorData")? page.getCookies().get("visitorData"):"";
        }

        final byte[] body = JsonWriter.string(prepareDesktopNextPageJsonBuilder(getExtractorLocalization(),
                getExtractorContentCountry(), clickTrackingParams, visitorData)
                .value("continuation", token)
                .done())
                .getBytes(UTF_8);

        initialData = getJsonPostResponse("browse", body, getExtractorLocalization());

        return parsePage(initialData);
//        return InfoItemsPage.emptyPage();
    }

    @Nonnull
    @Override
    public String getName() throws ParsingException {
        final JsonObject header = initialData.getObject("header");
        String name = "subscriptions";
        if (header.has("feedTabbedHeaderRenderer")) {
            name = getTextAtKey(header.getObject("feedTabbedHeaderRenderer"), "title");
        } else if (header.has("c4TabbedHeaderRenderer")) {
            name = getTextAtKey(header.getObject("c4TabbedHeaderRenderer"), "title");
        }

        if (isNullOrEmpty(name)) {
            throw new ParsingException("Could not get Trending name");
        }
        return name;
    }

    @Nonnull
    @Override
    public InfoItemsPage<StreamInfoItem> getInitialPage() {
        return parsePage(initialData);
    }

    private InfoItemsPage<StreamInfoItem> parsePage(JsonObject data) {
        Page nextPage = null;

        StreamInfoItemsCollector collector = new StreamInfoItemsCollector(getServiceId());
        final TimeAgoParser timeAgoParser = getTimeAgoParser();

        if (initialData.getObject("responseContext").has("visitorData")) {
            visitorData = initialData.getObject("responseContext").getString("visitorData");
            NewPipe.setVisitorData(visitorData);
        }

        //首页
        JsonArray itemSectionRenderers = initialData.getObject("contents")
                .getObject("twoColumnBrowseResultsRenderer").getArray("tabs").getObject(0)
                .getObject("tabRenderer").getObject("content").getObject("sectionListRenderer")
                .getArray("contents");

        //下拉页
        if (itemSectionRenderers.size() == 0) {
            itemSectionRenderers = initialData.getArray("onResponseReceivedActions").getObject(0)
                    .getObject("appendContinuationItemsAction").getArray("continuationItems");
        }

        for (final Object itemSectionRenderer : itemSectionRenderers) {
            JsonObject expandedShelfContentsRenderer = ((JsonObject) itemSectionRenderer)
                    .getObject("itemSectionRenderer").getArray("contents").getObject(0)
                    .getObject("shelfRenderer").getObject("content")
                    .getObject("gridRenderer");
            for (final Object ul : expandedShelfContentsRenderer.getArray("items")) {
                final JsonObject videoInfo = ((JsonObject) ul).getObject("gridVideoRenderer");
                collector.commit(new YoutubeStreamInfoItemExtractor(videoInfo, timeAgoParser));
            }

            JsonObject nextPageObject = ((JsonObject) itemSectionRenderer)
                    .getObject("continuationItemRenderer").getObject("continuationEndpoint");
            if (nextPageObject != null && (nextPageObject.size() > 0)) {
                String clickTrackingParams = nextPageObject.getString("clickTrackingParams");
                String token = ((JsonObject)nextPageObject.get("continuationCommand")).getString("token");

                Map<String, String> map = new HashMap<>();
                map.put("clickTrackingParams", clickTrackingParams);
                map.put("token", token);
                map.put("visitorData", visitorData);

                nextPage = new Page("/", map);
            }
        }

        return new InfoItemsPage<>(collector, nextPage);
    }
}
