package org.schabi.newpipe.extractor.handler.removehistory;

import static org.schabi.newpipe.extractor.services.youtube.YoutubeParsingHelper.getJsonPostResponse;
import static org.schabi.newpipe.extractor.services.youtube.YoutubeParsingHelper.prepareDesktopJsonBuilder;
import static org.schabi.newpipe.extractor.utils.Utils.UTF_8;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonBuilder;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonWriter;

import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.handler.like.RemoveLikeHandlerExtractor;

import java.io.IOException;

/**
 * created by lijinping on 2021/10/25 15:11
 * desc:顶
 */
public class YoutubeRemoveHistoryRecordHandlerExtractor extends RemoveHistoryRecordHandlerExtractor {
    
    public YoutubeRemoveHistoryRecordHandlerExtractor(StreamingService service) {
        super(service);
    }

    public Boolean remove(final String feedbackToken) throws IOException, ExtractionException {
        boolean success = false;

        JsonBuilder<JsonObject> jsonBuilder = prepareDesktopJsonBuilder(getExtractorLocalization(),
                getExtractorContentCountry());

        jsonBuilder = jsonBuilder.array("feedbackTokens")
                                .value(feedbackToken)
                                .end();

        final byte[] body = JsonWriter.string(jsonBuilder.done())
                .getBytes(UTF_8);

        JsonObject initialData = getJsonPostResponse("feedback", body, getExtractorLocalization());

        if (initialData != null) {
            JsonArray feedbackResponses = initialData.getArray("feedbackResponses");
            if (feedbackResponses != null) {
                success = ((JsonObject)feedbackResponses.get(0)).getBoolean("isProcessed");
            }
        }

        return Boolean.valueOf(success);
    }

    public Boolean removeAll() throws IOException, ExtractionException {
        boolean success = false;

        JsonBuilder<JsonObject> jsonBuilder = prepareDesktopJsonBuilder(getExtractorLocalization(),
                getExtractorContentCountry());

        jsonBuilder = jsonBuilder.value("feedbackTokens", NewPipe.getFeedbackToken());

        final byte[] body = JsonWriter.string(jsonBuilder.done())
                .getBytes(UTF_8);

        JsonObject initialData = getJsonPostResponse("feedback", body, getExtractorLocalization());

        if (initialData != null) {
            JsonArray feedbackResponses = initialData.getArray("feedbackResponses");
            if (feedbackResponses != null) {
                success = ((JsonObject)feedbackResponses.get(0)).getBoolean("isProcessed");
            }
        }

        return Boolean.valueOf(success);
    }
}
