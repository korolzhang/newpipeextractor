package org.schabi.newpipe.extractor.handler.like;

import static org.schabi.newpipe.extractor.services.youtube.YoutubeParsingHelper.getJsonPostResponse;
import static org.schabi.newpipe.extractor.services.youtube.YoutubeParsingHelper.prepareDesktopJsonBuilder;
import static org.schabi.newpipe.extractor.utils.Utils.UTF_8;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonBuilder;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonWriter;

import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;

import java.io.IOException;

/**
 * created by lijinping on 2021/10/12 10:01
 * desc:顶
 */
public class YoutubeLikeHandlerExtractor extends LikeHandlerExtractor {
    
    public YoutubeLikeHandlerExtractor(StreamingService service) {
        super(service);
    }

    public Boolean like(final String videoId) throws IOException, ExtractionException {
        boolean success = false;

        JsonBuilder<JsonObject> jsonBuilder = prepareDesktopJsonBuilder(getExtractorLocalization(),
                getExtractorContentCountry());

        jsonBuilder = jsonBuilder.object("target")
                                    .value("videoId", videoId)
                                .end();

        final byte[] body = JsonWriter.string(jsonBuilder.done())
                .getBytes(UTF_8);

        JsonObject initialData = getJsonPostResponse("like/like", body, getExtractorLocalization());

        if (initialData != null) {
            JsonArray actions = initialData.getArray("actions");
            if ((actions != null) && actions.size() > 0) {
                for (final Object action : actions) {
                    if (((JsonObject) action).has("openPopupAction")) {
                        success = true;
                        break;
                    }
                }
            }
        }

        return Boolean.valueOf(success);
    }
}
