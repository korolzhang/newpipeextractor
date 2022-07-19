package org.schabi.newpipe.extractor.services.youtube.extractors;

import static org.schabi.newpipe.extractor.services.youtube.YoutubeParsingHelper.getJsonPostResponse;
import static org.schabi.newpipe.extractor.services.youtube.YoutubeParsingHelper.prepareDesktopJsonBuilder;
import static org.schabi.newpipe.extractor.services.youtube.YoutubeParsingHelper.prepareDesktopTrendingJsonBuilder;
import static org.schabi.newpipe.extractor.utils.Utils.UTF_8;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonWriter;

import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.account.AccountInfo;
import org.schabi.newpipe.extractor.downloader.Downloader;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.linkhandler.LinkHandler;
import org.schabi.newpipe.extractor.localization.ContentCountry;
import org.schabi.newpipe.extractor.localization.Localization;
import org.schabi.newpipe.extractor.account.AccountExtractor;

import java.io.IOException;

import javax.annotation.Nonnull;

/**
 * created by pgh on 2021/10/9 18:10
 * desc:
 */
public class YoutubeAccountExtractor extends AccountExtractor {
    
    public YoutubeAccountExtractor(StreamingService service) {
        super(service);
    }

    public  AccountInfo getAccountInfo() throws IOException, ExtractionException {
        boolean loggedOut = true;
        AccountInfo info = null;
        final byte[] body = JsonWriter.string(prepareDesktopJsonBuilder(getExtractorLocalization(),
                getExtractorContentCountry())
                .done())
                .getBytes(UTF_8);

        JsonObject jsonObject = getJsonPostResponse("account/account_menu", body, getExtractorLocalization());

        NewPipe.parseLoginStatus(jsonObject);

        if (jsonObject != null) {
            loggedOut = jsonObject.getObject("responseContext").getObject("mainAppWebResponseContext").getBoolean("loggedOut");
            if (!loggedOut) {
                JsonArray actions = jsonObject.getArray("actions");
                if ((actions != null) && actions.size() > 0) {
                    info = new AccountInfo();

                    for (final Object action : actions) {
                        JsonObject activeAccountHeaderRenderer = ((JsonObject) action)
                                .getObject("openPopupAction").getObject("popup").getObject("multiPageMenuRenderer")
                                .getObject("header").getObject("activeAccountHeaderRenderer");
                        if (activeAccountHeaderRenderer != null) {
                            info.setName(activeAccountHeaderRenderer.getObject("accountName").getString("simpleText"));
                            info.setEmail(activeAccountHeaderRenderer.getObject("email").getString("simpleText"));
                            info.setAvatar(activeAccountHeaderRenderer.getObject("accountPhoto").getArray("thumbnails").getObject(0).getString("url"));
                        }
                    }
                }
            }
        }

        if ((info != null) && (info.getEmail() == null)) {
            final byte[] body2 = JsonWriter.string(prepareDesktopJsonBuilder(getExtractorLocalization(),
                    getExtractorContentCountry())
                    .value("browseId", "SPaccount_overview")
                    .done())
                    .getBytes(UTF_8);
            jsonObject = getJsonPostResponse("browse", body2, getExtractorLocalization());
            if (jsonObject != null) {
                JsonArray sectionListRenderers = jsonObject.getObject("contents")
                        .getObject("twoColumnBrowseResultsRenderer").getArray("tabs").getObject(0)
                        .getObject("tabRenderer").getObject("content").getObject("sectionListRenderer")
                        .getArray("contents");

                for (final Object itemSectionRenderer : sectionListRenderers) {
                    JsonArray contents = ((JsonObject) itemSectionRenderer)
                            .getObject("itemSectionRenderer").getArray("contents");
                    for (final Object content : contents) {
                        if (((JsonObject) content).has("pageIntroductionRenderer")) {
                            JsonObject settingsOptionsRenderer = ((JsonObject) content).getObject("pageIntroductionRenderer");
                            JsonObject headerText = settingsOptionsRenderer.getObject("headerText");
                            if (headerText != null) {
                                JsonObject bodyText = settingsOptionsRenderer.getObject("bodyText");
                                if (bodyText != null) {
                                    JsonArray runs = bodyText.getArray("runs");
                                    if ((runs != null) && runs.size() > 1) {
                                        JsonObject run = runs.getObject(1);
                                        if (run != null) {
                                            info.setEmail(run.getString("text"));
                                            return info;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return info;
    }
}
