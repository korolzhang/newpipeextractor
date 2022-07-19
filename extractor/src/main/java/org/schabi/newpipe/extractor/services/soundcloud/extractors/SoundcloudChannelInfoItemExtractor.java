package org.schabi.newpipe.extractor.services.soundcloud.extractors;

import com.grack.nanojson.JsonObject;
import org.schabi.newpipe.extractor.channel.ChannelInfoItemExtractor;
import org.schabi.newpipe.extractor.exceptions.ParsingException;

import static org.schabi.newpipe.extractor.utils.Utils.EMPTY_STRING;
import static org.schabi.newpipe.extractor.utils.Utils.replaceHttpWithHttps;

public class SoundcloudChannelInfoItemExtractor implements ChannelInfoItemExtractor {
    private final JsonObject itemObject;

    public SoundcloudChannelInfoItemExtractor(final JsonObject itemObject) {
        this.itemObject = itemObject;
    }

    @Override
    public String getName() {
        return itemObject.getString("username");
    }

    @Override
    public String getUrl() {
        return replaceHttpWithHttps(itemObject.getString("permalink_url"));
    }

    @Override
    public String getThumbnailUrl() {
        String avatarUrl = itemObject.getString("avatar_url", EMPTY_STRING);
        // An avatar URL with a better resolution
        return avatarUrl.replace("large.jpg", "crop.jpg");
    }

    @Override
    public long getSubscriberCount() {
        return itemObject.getLong("followers_count");
    }

    @Override
    public long getStreamCount() {
        return itemObject.getLong("track_count");
    }

    @Override
    public boolean isVerified() {
        return itemObject.getBoolean("verified");
    }

    @Override
    public boolean isSubscribed() throws ParsingException {
        return false;
    }

    @Override
    public String getChannelId() throws ParsingException {
        return "";
    }

    @Override
    public String getDescription() {
        return itemObject.getString("description", EMPTY_STRING);
    }
}
