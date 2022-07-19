package org.schabi.newpipe.extractor.account;

import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.localization.ContentCountry;
import org.schabi.newpipe.extractor.localization.Localization;

import java.io.IOException;

import javax.annotation.Nonnull;

/**
 * created by pgh on 2021/10/9 18:07
 * desc:
 */
public abstract class AccountExtractor  {
    private final StreamingService service;

    public AccountExtractor(StreamingService service) {
        this.service = service;
    }

    public abstract AccountInfo getAccountInfo() throws IOException, ExtractionException;

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
