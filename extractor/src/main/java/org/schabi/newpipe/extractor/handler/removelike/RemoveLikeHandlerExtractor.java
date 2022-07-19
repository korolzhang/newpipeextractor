package org.schabi.newpipe.extractor.handler.like;

import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.localization.ContentCountry;
import org.schabi.newpipe.extractor.localization.Localization;

import java.io.IOException;
import java.util.ArrayList;

import javax.annotation.Nonnull;

/**
 * created by lijinping on 2021/10/12 11:03
 * desc:删除顶/踩
 */
public abstract class RemoveLikeHandlerExtractor  {
    private final StreamingService service;

    public RemoveLikeHandlerExtractor(StreamingService service) {
        this.service = service;
    }

    public abstract Boolean removelike(final String videoId) throws IOException, ExtractionException;

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
