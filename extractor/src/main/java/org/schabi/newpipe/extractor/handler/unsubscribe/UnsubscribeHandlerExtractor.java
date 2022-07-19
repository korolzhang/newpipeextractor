package org.schabi.newpipe.extractor.handler.unsubscribe;

import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.account.AccountInfo;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.localization.ContentCountry;
import org.schabi.newpipe.extractor.localization.Localization;

import java.io.IOException;
import java.util.ArrayList;

import javax.annotation.Nonnull;

/**
 * created by lijinping on 2021/10/11 17:51
 * desc:
 */
public abstract class UnsubscribeHandlerExtractor  {
    private final StreamingService service;

    public UnsubscribeHandlerExtractor(StreamingService service) {
        this.service = service;
    }

    public abstract Boolean unsubscribe(final String channelId) throws IOException, ExtractionException;
    public abstract Boolean unsubscribe(final ArrayList<String> channelIds) throws IOException, ExtractionException;

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
