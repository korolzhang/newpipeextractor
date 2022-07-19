package org.schabi.newpipe.extractor.handler.unsubscribe;

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
import java.util.ArrayList;

/**
 * created by lijinping on 2021/10/11 17:55
 * desc:取消订阅
 */
public class YoutubeUnsubcribeHandlerExtractor extends UnsubscribeHandlerExtractor {
    
    public YoutubeUnsubcribeHandlerExtractor(StreamingService service) {
        super(service);
    }

    public Boolean unsubscribe(final String channelId) throws IOException, ExtractionException {
        ArrayList<String> channelIds = new ArrayList<>();
        channelIds.add(channelId);
        return unsubscribe(channelIds);
    }

    public Boolean unsubscribe(final ArrayList<String> channelIds) throws IOException, ExtractionException {
        boolean success = false;

        JsonBuilder<JsonObject> jsonBuilder = prepareDesktopJsonBuilder(getExtractorLocalization(),
                getExtractorContentCountry());

        if (channelIds.size() > 0) {
            jsonBuilder = jsonBuilder.array("channelIds");

            for (final String channelId : channelIds) {
                jsonBuilder = jsonBuilder.value(channelId);
            }

            jsonBuilder = jsonBuilder.end();
        }

        final byte[] body = JsonWriter.string(jsonBuilder.done())
                .getBytes(UTF_8);

        JsonObject initialData = getJsonPostResponse("subscription/unsubscribe", body, getExtractorLocalization());

        if (initialData != null) {
            JsonArray actions = initialData.getArray("actions");
            if ((actions != null) && actions.size() > 0) {
                for (final Object action : actions) {
                    if (((JsonObject) action).has("updateSubscribeButtonAction")) {
                        JsonObject updateSubscribeButtonAction = ((JsonObject) action)
                                .getObject("updateSubscribeButtonAction");
                        if (updateSubscribeButtonAction.has("subscribed")) {
                            if (!updateSubscribeButtonAction.getBoolean("subscribed")) {
                                success = true;
                            }
                        }
                    }
                }
            }
        }

        return Boolean.valueOf(success);
    }
}
