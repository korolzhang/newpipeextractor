package org.schabi.newpipe.extractor.handler.subscribe;

import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.localization.ContentCountry;
import org.schabi.newpipe.extractor.localization.Localization;

import java.io.IOException;
import java.util.ArrayList;

import javax.annotation.Nonnull;

/**
 * created by lijinping on 2021/10/11 15:33
 * desc:
 */
public abstract class SubscribeHandlerExtractor  {
    private final StreamingService service;

    public SubscribeHandlerExtractor(StreamingService service) {
        this.service = service;
    }

    public abstract Boolean subscribe(final String channelId) throws IOException, ExtractionException;
    public abstract Boolean subscribe(final ArrayList<String> channelIds) throws IOException, ExtractionException;

    public int getServiceId() {
        return service.getServiceId();
    }

    public StreamingService getService() {
        return service;
    }

    @Nonnull
    public Localization getExtractorLocalization() {
        return  getService().getLocalization();
    }

    @Nonnull
    public ContentCountry getExtractorContentCountry() {
        return getService().getContentCountry();
    }
}
