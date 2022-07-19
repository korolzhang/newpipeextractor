package org.schabi.newpipe.extractor.handler.removehistory;

import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.localization.ContentCountry;
import org.schabi.newpipe.extractor.localization.Localization;

import java.io.IOException;
import java.util.ArrayList;

import javax.annotation.Nonnull;

/**
 * created by lijinping on 2021/10/25 15:10
 * desc:删除指定历史记录
 */
public abstract class RemoveHistoryRecordHandlerExtractor  {
    private final StreamingService service;

    public RemoveHistoryRecordHandlerExtractor(StreamingService service) {
        this.service = service;
    }

    public abstract Boolean remove(final String feedbackToken) throws IOException, ExtractionException;

    public abstract Boolean removeAll() throws IOException, ExtractionException;

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
