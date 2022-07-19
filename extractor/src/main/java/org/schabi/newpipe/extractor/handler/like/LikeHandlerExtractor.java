package org.schabi.newpipe.extractor.handler.like;

import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.localization.ContentCountry;
import org.schabi.newpipe.extractor.localization.Localization;

import java.io.IOException;
import java.util.ArrayList;

import javax.annotation.Nonnull;

/**
 * created by lijinping on 2021/10/12 10:00
 * desc:
 */
public abstract class LikeHandlerExtractor  {
    private final StreamingService service;

    public LikeHandlerExtractor(StreamingService service) {
        this.service = service;
    }

    public abstract Boolean like(final String videoId) throws IOException, ExtractionException;

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
