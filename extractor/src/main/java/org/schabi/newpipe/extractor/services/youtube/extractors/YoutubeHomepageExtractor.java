package org.schabi.newpipe.extractor.services.youtube.extractors;

import static org.schabi.newpipe.extractor.NewPipe.getDownloader;
import static org.schabi.newpipe.extractor.services.youtube.YoutubeParsingHelper.getClientVersion;
import static org.schabi.newpipe.extractor.services.youtube.YoutubeParsingHelper.getJsonPostResponse;
import static org.schabi.newpipe.extractor.services.youtube.YoutubeParsingHelper.getTextAtKey;
import static org.schabi.newpipe.extractor.services.youtube.YoutubeParsingHelper.prepareDesktopJsonBuilder;
import static org.schabi.newpipe.extractor.services.youtube.YoutubeParsingHelper.prepareDesktopNextPageJsonBuilder;
import static org.schabi.newpipe.extractor.utils.Utils.UTF_8;
import static org.schabi.newpipe.extractor.utils.Utils.isNullOrEmpty;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonBuilder;
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
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

public class YoutubeHomepageExtractor extends KioskExtractor<StreamInfoItem> {
    private JsonObject initialData;
    private String visitorData;

    public YoutubeHomepageExtractor(final StreamingService service,
                                    final ListLinkHandler linkHandler,
                                    final String kioskId) {
        super(service, linkHandler, kioskId);
        visitorData = NewPipe.getVisitorData();
    }

    @Override
    public void onFetchPage(@Nonnull final Downloader downloader) throws IOException, ExtractionException {
        // @formatter:off
        final byte[] body = JsonWriter.string(prepareDesktopNextPageJsonBuilder(getExtractorLocalization(),
                getExtractorContentCountry(), "", visitorData)
                .value("browseId", "FEwhat_to_watch")
                .done())
                .getBytes(UTF_8);
        // @formatter:on

        initialData = getJsonPostResponse("browse", body, getExtractorLocalization());
    }

    @Override
    public InfoItemsPage<StreamInfoItem> getPage(final Page page)  throws IOException, ExtractionException {
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
    }

    @Nonnull
    @Override
    public String getName() throws ParsingException {
        String name = "homepage";
        final JsonObject header = initialData.getObject("header");
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

        NewPipe.parseLoginStatus(initialData);

        //首页
        JsonArray itemSectionRenderers = data.getObject("contents")
                .getObject("twoColumnBrowseResultsRenderer").getArray("tabs").getObject(0)
                .getObject("tabRenderer").getObject("content").getObject("richGridRenderer")
                .getArray("contents");

        //下拉页
        if (itemSectionRenderers.size() == 0) {
            itemSectionRenderers = initialData.getArray("onResponseReceivedActions").getObject(0)
                    .getObject("appendContinuationItemsAction").getArray("continuationItems");

            for (int i=0; i<itemSectionRenderers.size(); i++) {
                JsonObject itemSectionRenderer = (JsonObject)itemSectionRenderers.get(i);
                JsonObject content = itemSectionRenderer
                        .getObject("richItemRenderer").getObject("content");
                final JsonObject videoInfo = ((JsonObject) content).getObject("videoRenderer");
                if (videoInfo.size() != 0) {
                    collector.commit(new YoutubeStreamInfoItemExtractor(videoInfo, timeAgoParser));
                } else {
                    JsonObject nextPageObject = itemSectionRenderer
                            .getObject("continuationItemRenderer").getObject("continuationEndpoint");
                    if (nextPageObject != null && (nextPageObject.size() > 0)) {
                        String clickTrackingParams = nextPageObject.getString("clickTrackingParams");
                        String token = ((JsonObject) nextPageObject.get("continuationCommand")).getString("token");

                        Map<String, String> map = new HashMap<>();
                        map.put("clickTrackingParams", clickTrackingParams);
                        map.put("token", token);
                        map.put("visitorData", visitorData);

                        nextPage = new Page("/", map);
                    }
                }
            }
        } else {
            for (int i=itemSectionRenderers.size()-1; i>=0; i--) {
                JsonObject itemSectionRenderer = (JsonObject)itemSectionRenderers.get(i);
                JsonObject content = itemSectionRenderer
                        .getObject("richItemRenderer").getObject("content");
                final JsonObject videoInfo = ((JsonObject) content).getObject("videoRenderer");
                if (videoInfo.size() != 0) {
                    collector.commit(new YoutubeStreamInfoItemExtractor(videoInfo, timeAgoParser));
                } else {
                    JsonObject nextPageObject = itemSectionRenderer
                            .getObject("continuationItemRenderer").getObject("continuationEndpoint");
                    if (nextPageObject != null && (nextPageObject.size() > 0)) {
                        String clickTrackingParams = nextPageObject.getString("clickTrackingParams");
                        String token = ((JsonObject) nextPageObject.get("continuationCommand")).getString("token");

                        Map<String, String> map = new HashMap<>();
                        map.put("clickTrackingParams", clickTrackingParams);
                        map.put("token", token);
                        map.put("visitorData", visitorData);

                        nextPage = new Page("/", map);
                    }
                }
            }
        }

        return new InfoItemsPage<>(collector, nextPage);
    }
}
