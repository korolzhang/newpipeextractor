package org.schabi.newpipe.extractor.services.youtube.linkHandler;

import org.schabi.newpipe.extractor.linkhandler.ListLinkHandlerFactory;
import org.schabi.newpipe.extractor.services.youtube.YoutubeParsingHelper;
import org.schabi.newpipe.extractor.services.youtube.YoutubeService;
import org.schabi.newpipe.extractor.utils.Utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class YoutubeHomepageLinkHandlerFactory extends ListLinkHandlerFactory {

    public String getUrl(String id, List<String> contentFilters, String sortFilter) {
        return "https://www.youtube.com";
    }

    @Override
    public String getId(String url) {
        return YoutubeService.KIOSK_HOMEPAGE;
    }

    @Override
    public boolean onAcceptUrl(final String url) {
        URL urlObj;
        try {
            urlObj = Utils.stringToURL(url);
        } catch (MalformedURLException e) {
            return false;
        }

        String urlPath = urlObj.getPath();
        //return Utils.isHTTP(urlObj) && (YoutubeParsingHelper.isYoutubeURL(urlObj) || YoutubeParsingHelper.isInvidioURL(urlObj)) && urlPath.equals("/feed/trending");
        return Utils.isHTTP(urlObj) && (YoutubeParsingHelper.isYoutubeURL(urlObj) || YoutubeParsingHelper.isInvidioURL(urlObj)) && urlPath.isEmpty();
    }
}

