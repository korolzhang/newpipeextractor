package org.schabi.newpipe.extractor.services.youtube;

import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.channel.ChannelExtractor;
import org.schabi.newpipe.extractor.comments.CommentsExtractor;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.exceptions.ReCaptchaException;
import org.schabi.newpipe.extractor.feed.FeedExtractor;
import org.schabi.newpipe.extractor.handler.dislike.DislikeHandlerExtractor;
import org.schabi.newpipe.extractor.handler.dislike.YoutubeDislikeHandlerExtractor;
import org.schabi.newpipe.extractor.handler.like.LikeHandlerExtractor;
import org.schabi.newpipe.extractor.handler.like.RemoveLikeHandlerExtractor;
import org.schabi.newpipe.extractor.handler.like.YoutubeLikeHandlerExtractor;
import org.schabi.newpipe.extractor.handler.removehistory.RemoveHistoryRecordHandlerExtractor;
import org.schabi.newpipe.extractor.handler.removehistory.YoutubeRemoveHistoryRecordHandlerExtractor;
import org.schabi.newpipe.extractor.handler.removelike.YoutubeRemoveLikeHandlerExtractor;
import org.schabi.newpipe.extractor.handler.subscribe.SubscribeHandlerExtractor;
import org.schabi.newpipe.extractor.handler.subscribe.YoutubeSubcribeHandlerExtractor;
import org.schabi.newpipe.extractor.handler.unsubscribe.UnsubscribeHandlerExtractor;
import org.schabi.newpipe.extractor.handler.unsubscribe.YoutubeUnsubcribeHandlerExtractor;
import org.schabi.newpipe.extractor.kiosk.KioskExtractor;
import org.schabi.newpipe.extractor.kiosk.KioskList;
import org.schabi.newpipe.extractor.linkhandler.LinkHandler;
import org.schabi.newpipe.extractor.linkhandler.LinkHandlerFactory;
import org.schabi.newpipe.extractor.linkhandler.ListLinkHandler;
import org.schabi.newpipe.extractor.linkhandler.ListLinkHandlerFactory;
import org.schabi.newpipe.extractor.linkhandler.SearchQueryHandler;
import org.schabi.newpipe.extractor.linkhandler.SearchQueryHandlerFactory;
import org.schabi.newpipe.extractor.localization.ContentCountry;
import org.schabi.newpipe.extractor.localization.Localization;
import org.schabi.newpipe.extractor.playlist.PlaylistExtractor;
import org.schabi.newpipe.extractor.search.SearchExtractor;
import org.schabi.newpipe.extractor.services.youtube.extractors.YoutubeChannelExtractor;
import org.schabi.newpipe.extractor.services.youtube.extractors.YoutubeChannelListExtractor;
import org.schabi.newpipe.extractor.services.youtube.extractors.YoutubeCommentsExtractor;
import org.schabi.newpipe.extractor.services.youtube.extractors.YoutubeFeedExtractor;
import org.schabi.newpipe.extractor.services.youtube.extractors.YoutubeGamesExtractor;
import org.schabi.newpipe.extractor.services.youtube.extractors.YoutubeHistoryExtractor;
import org.schabi.newpipe.extractor.services.youtube.extractors.YoutubeHomepageExtractor;
import org.schabi.newpipe.extractor.services.youtube.extractors.YoutubeLikeExtractor;
import org.schabi.newpipe.extractor.services.youtube.extractors.YoutubeLiveSubscriptionExtractor;
import org.schabi.newpipe.extractor.services.youtube.extractors.YoutubeMixPlaylistExtractor;
import org.schabi.newpipe.extractor.services.youtube.extractors.YoutubeMoviesExtractor;
import org.schabi.newpipe.extractor.services.youtube.extractors.YoutubeMusicExtractor;
import org.schabi.newpipe.extractor.services.youtube.extractors.YoutubeMusicSearchExtractor;
import org.schabi.newpipe.extractor.services.youtube.extractors.YoutubePlaylistExtractor;
import org.schabi.newpipe.extractor.services.youtube.extractors.YoutubeSearchExtractor;
import org.schabi.newpipe.extractor.services.youtube.extractors.YoutubeStreamExtractor;
import org.schabi.newpipe.extractor.services.youtube.extractors.YoutubeSubscriptionExtractor;
import org.schabi.newpipe.extractor.services.youtube.extractors.YoutubeSuggestionExtractor;
import org.schabi.newpipe.extractor.services.youtube.extractors.YoutubeTrendingExtractor;
import org.schabi.newpipe.extractor.services.youtube.extractors.YoutubeAccountExtractor;
import org.schabi.newpipe.extractor.services.youtube.linkHandler.YoutubeChannelLinkHandlerFactory;
import org.schabi.newpipe.extractor.services.youtube.linkHandler.YoutubeChannelListLinkHandlerFactory;
import org.schabi.newpipe.extractor.services.youtube.linkHandler.YoutubeCommentsLinkHandlerFactory;
import org.schabi.newpipe.extractor.services.youtube.linkHandler.YoutubeGamesLinkHandlerFactory;
import org.schabi.newpipe.extractor.services.youtube.linkHandler.YoutubeHistoryLinkHandlerFactory;
import org.schabi.newpipe.extractor.services.youtube.linkHandler.YoutubeHomepageLinkHandlerFactory;
import org.schabi.newpipe.extractor.services.youtube.linkHandler.YoutubeLikeListLinkHandlerFactory;
import org.schabi.newpipe.extractor.services.youtube.linkHandler.YoutubeLiveSubscriptionLinkHandlerFactory;
import org.schabi.newpipe.extractor.services.youtube.linkHandler.YoutubeMoviesLinkHandlerFactory;
import org.schabi.newpipe.extractor.services.youtube.linkHandler.YoutubeMusicLinkHandlerFactory;
import org.schabi.newpipe.extractor.services.youtube.linkHandler.YoutubePlaylistLinkHandlerFactory;
import org.schabi.newpipe.extractor.services.youtube.linkHandler.YoutubeSearchQueryHandlerFactory;
import org.schabi.newpipe.extractor.services.youtube.linkHandler.YoutubeStreamLinkHandlerFactory;
import org.schabi.newpipe.extractor.services.youtube.linkHandler.YoutubeTrendingLinkHandlerFactory;
import org.schabi.newpipe.extractor.stream.StreamExtractor;
import org.schabi.newpipe.extractor.subscription.SubscriptionExtractor;
import org.schabi.newpipe.extractor.suggestion.SuggestionExtractor;
import org.schabi.newpipe.extractor.account.AccountExtractor;
import org.schabi.newpipe.extractor.utils.StringUtils;

import java.io.IOException;
import java.util.List;

import javax.annotation.Nonnull;

import static java.util.Arrays.asList;
import static org.schabi.newpipe.extractor.StreamingService.ServiceInfo.MediaCapability.AUDIO;
import static org.schabi.newpipe.extractor.StreamingService.ServiceInfo.MediaCapability.COMMENTS;
import static org.schabi.newpipe.extractor.StreamingService.ServiceInfo.MediaCapability.LIVE;
import static org.schabi.newpipe.extractor.StreamingService.ServiceInfo.MediaCapability.VIDEO;

/*
 * Created by Christian Schabesberger on 23.08.15.
 *
 * Copyright (C) Christian Schabesberger 2018 <chris.schabesberger@mailbox.org>
 * YoutubeService.java is part of NewPipe.
 *
 * NewPipe is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NewPipe is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with NewPipe.  If not, see <http://www.gnu.org/licenses/>.
 */

public class YoutubeService extends StreamingService {
    public static final String KIOSK_TRENDING = "Trending";
    public static final String KIOSK_HOMEPAGE = "Homepage";
    public static final String KIOSK_SUBSCRIPTION = "Subscription";
    public static final String KIOSK_HISTORY = "History";
    public static final String KIOSK_MUSIC = "Music";
    public static final String KIOSK_GAMES = "Games";
    public static final String KIOSK_MOVIES = "Movies";
    public static final String KIOSK_CHANNEL_LIST = "ChannelList";
    public static final String KIOSK_LIKE_LIST = "LikeList";

    public YoutubeService(int id) {
        super(id, "YouTube", asList(AUDIO, VIDEO, LIVE, COMMENTS));
    }

    @Override
    public String getBaseUrl() {
        return "https://youtube.com";
    }

    @Override
    public LinkHandlerFactory getStreamLHFactory() {
        return YoutubeStreamLinkHandlerFactory.getInstance();
    }

    @Override
    public ListLinkHandlerFactory getChannelLHFactory() {
        return YoutubeChannelLinkHandlerFactory.getInstance();
    }

    @Override
    public ListLinkHandlerFactory getPlaylistLHFactory() {
        return YoutubePlaylistLinkHandlerFactory.getInstance();
    }

    @Override
    public SearchQueryHandlerFactory getSearchQHFactory() {
        return YoutubeSearchQueryHandlerFactory.getInstance();
    }

    @Override
    public StreamExtractor getStreamExtractor(LinkHandler linkHandler) {
        return new YoutubeStreamExtractor(this, linkHandler);
    }

    @Override
    public AccountExtractor getAccountExtractor() {
        return new YoutubeAccountExtractor(this);
    }

    @Override
    public SubscribeHandlerExtractor getSubscribeHandlerExtractor() {
        return new YoutubeSubcribeHandlerExtractor(this);
    }

    @Override
    public UnsubscribeHandlerExtractor getUnsubscribeHandlerExtractor() {
        return new YoutubeUnsubcribeHandlerExtractor(this);
    }

    @Override
    public LikeHandlerExtractor getLikeHandlerExtractor() {
        return new YoutubeLikeHandlerExtractor(this);
    }

    @Override
    public DislikeHandlerExtractor getDislikeHandlerExtractor() {
        return new YoutubeDislikeHandlerExtractor(this);
    }

    @Override
    public RemoveLikeHandlerExtractor getRemoveLikeHandlerExtractor() {
        return new YoutubeRemoveLikeHandlerExtractor(this);
    }

    @Override
    public RemoveHistoryRecordHandlerExtractor getRemoveHistoryRecordHandlerExtractor() {
        return new YoutubeRemoveHistoryRecordHandlerExtractor(this);
    }

    @Override
    public ChannelExtractor getChannelExtractor(ListLinkHandler linkHandler) {
        return new YoutubeChannelExtractor(this, linkHandler);
    }

    @Override
    public PlaylistExtractor getPlaylistExtractor(final ListLinkHandler linkHandler) {
        if (YoutubeParsingHelper.isYoutubeMixId(linkHandler.getId())
                && !YoutubeParsingHelper.isYoutubeMusicMixId(linkHandler.getId())) {
            return new YoutubeMixPlaylistExtractor(this, linkHandler);
        } else {
            return new YoutubePlaylistExtractor(this, linkHandler);
        }
    }

    @Override
    public SearchExtractor getSearchExtractor(SearchQueryHandler query) {
        final List<String> contentFilters = query.getContentFilters();

        if (!contentFilters.isEmpty() && contentFilters.get(0).startsWith("music_")) {
            return new YoutubeMusicSearchExtractor(this, query);
        } else {
            return new YoutubeSearchExtractor(this, query);
        }
    }

    @Override
    public SuggestionExtractor getSuggestionExtractor() {
        return new YoutubeSuggestionExtractor(this);
    }

    @Override
    public KioskList getKioskList() throws ExtractionException {
        KioskList list = new KioskList(this);

        // add kiosks here e.g.:
        try {
            list.addKioskEntry(new KioskList.KioskExtractorFactory() {
                @Override
                public KioskExtractor createNewKiosk(StreamingService streamingService,
                                                     String url,
                                                     String id)
                        throws ExtractionException {
                    return new YoutubeTrendingExtractor(YoutubeService.this,
                            new YoutubeTrendingLinkHandlerFactory().fromUrl(url), id);
                }
            }, new YoutubeTrendingLinkHandlerFactory(), KIOSK_TRENDING);

            list.addKioskEntry(new KioskList.KioskExtractorFactory() {
                @Override
                public KioskExtractor createNewKiosk(StreamingService streamingService,
                                                     String url,
                                                     String id)
                        throws ExtractionException {
                    return new YoutubeHomepageExtractor(YoutubeService.this,
                            new YoutubeHomepageLinkHandlerFactory().fromUrl(url), id);
                }
            }, new YoutubeHomepageLinkHandlerFactory(), KIOSK_HOMEPAGE);

            list.addKioskEntry(new KioskList.KioskExtractorFactory() {
                @Override
                public KioskExtractor createNewKiosk(StreamingService streamingService,
                                                     String url,
                                                     String id)
                        throws ExtractionException {
                    return new YoutubeLiveSubscriptionExtractor(YoutubeService.this,
                            new YoutubeLiveSubscriptionLinkHandlerFactory().fromUrl(url), id);
                }
            }, new YoutubeLiveSubscriptionLinkHandlerFactory(), KIOSK_SUBSCRIPTION);

            list.addKioskEntry(new KioskList.KioskExtractorFactory() {
                @Override
                public KioskExtractor createNewKiosk(StreamingService streamingService,
                                                     String url,
                                                     String id)
                        throws ExtractionException {
                    return new YoutubeHistoryExtractor(YoutubeService.this,
                            new YoutubeHistoryLinkHandlerFactory().fromUrl(url), id);
                }
            }, new YoutubeHistoryLinkHandlerFactory(), KIOSK_HISTORY);

            list.addKioskEntry(new KioskList.KioskExtractorFactory() {
                @Override
                public KioskExtractor createNewKiosk(StreamingService streamingService,
                                                     String url,
                                                     String id)
                        throws ExtractionException {
                    return new YoutubeMusicExtractor(YoutubeService.this,
                            new YoutubeMusicLinkHandlerFactory().fromUrl(url), id);
                }
            }, new YoutubeMusicLinkHandlerFactory(), KIOSK_MUSIC);

            list.addKioskEntry(new KioskList.KioskExtractorFactory() {
                @Override
                public KioskExtractor createNewKiosk(StreamingService streamingService,
                                                     String url,
                                                     String id)
                        throws ExtractionException {
                    return new YoutubeGamesExtractor(YoutubeService.this,
                            new YoutubeGamesLinkHandlerFactory().fromUrl(url), id);
                }
            }, new YoutubeGamesLinkHandlerFactory(), KIOSK_GAMES);

            list.addKioskEntry(new KioskList.KioskExtractorFactory() {
                @Override
                public KioskExtractor createNewKiosk(StreamingService streamingService,
                                                     String url,
                                                     String id)
                        throws ExtractionException {
                    return new YoutubeMoviesExtractor(YoutubeService.this,
                            new YoutubeMoviesLinkHandlerFactory().fromUrl(url), id);
                }
            }, new YoutubeMoviesLinkHandlerFactory(), KIOSK_MOVIES);

            list.addKioskEntry(new KioskList.KioskExtractorFactory() {
                @Override
                public KioskExtractor createNewKiosk(StreamingService streamingService,
                                                     String url,
                                                     String id)
                        throws ExtractionException {
                    return new YoutubeChannelListExtractor(YoutubeService.this,
                            new YoutubeChannelListLinkHandlerFactory().fromUrl(url), id);
                }
            }, new YoutubeChannelListLinkHandlerFactory(), KIOSK_CHANNEL_LIST);

            list.addKioskEntry(new KioskList.KioskExtractorFactory() {
                @Override
                public KioskExtractor createNewKiosk(StreamingService streamingService,
                                                     String url,
                                                     String id)
                        throws ExtractionException {
                    return new YoutubeLikeExtractor(YoutubeService.this,
                            new YoutubeLikeListLinkHandlerFactory().fromUrl(url), id);
                }
            }, new YoutubeLikeListLinkHandlerFactory(), KIOSK_LIKE_LIST);

            list.setDefaultKiosk(KIOSK_HOMEPAGE);

        } catch (Exception e) {
            throw new ExtractionException(e);
        }

        return list;
    }

    @Override
    public SubscriptionExtractor getSubscriptionExtractor() {
        return new YoutubeSubscriptionExtractor(this);
    }

    @Nonnull
    @Override
    public FeedExtractor getFeedExtractor(final String channelUrl) throws ExtractionException {
        return new YoutubeFeedExtractor(this, getChannelLHFactory().fromUrl(channelUrl));
    }

    @Override
    public ListLinkHandlerFactory getCommentsLHFactory() {
        return YoutubeCommentsLinkHandlerFactory.getInstance();
    }

    @Override
    public CommentsExtractor getCommentsExtractor(ListLinkHandler urlIdHandler)
            throws ExtractionException {
        return new YoutubeCommentsExtractor(this, urlIdHandler);
    }

    public Boolean uploadPlayerback(String url) {
        url = url + "&ver=2" + "&cpn=" + StringUtils.getRandomString(16);
        try {
            NewPipe.getDownloader().get(url, Localization.DEFAULT).responseBody();
        } catch (Exception e) {

        }

        return true;
    }

    /*//////////////////////////////////////////////////////////////////////////
    // Localization
    //////////////////////////////////////////////////////////////////////////*/

    // https://www.youtube.com/picker_ajax?action_language_json=1
    private static final List<Localization> SUPPORTED_LANGUAGES = Localization.listFrom(
            "af", "am", "ar", "az", "be", "bg", "bn", "bs", "ca", "cs", "da", "de",
            "el", "en", "en-GB", "es", "es-419", "es-US", "et", "eu", "fa", "fi", "fil", "fr",
            "fr-CA", "gl", "gu", "hi", "hr", "hu", "hy", "id", "is", "it", "iw", "ja",
            "ka", "kk", "km", "kn", "ko", "ky", "lo", "lt", "lv", "mk", "ml", "mn",
            "mr", "ms", "my", "ne", "nl", "no", "pa", "pl", "pt", "pt-PT", "ro", "ru",
            "si", "sk", "sl", "sq", "sr", "sr-Latn", "sv", "sw", "ta", "te", "th", "tr",
            "uk", "ur", "uz", "vi", "zh-CN", "zh-HK", "zh-TW", "zu"
    );

    // https://www.youtube.com/picker_ajax?action_country_json=1
    private static final List<ContentCountry> SUPPORTED_COUNTRIES = ContentCountry.listFrom(
            "DZ", "AR", "AU", "AT", "AZ", "BH", "BD", "BY", "BE", "BO", "BA", "BR", "BG", "CA", "CL",
            "CO", "CR", "HR", "CY", "CZ", "DK", "DO", "EC", "EG", "SV", "EE", "FI", "FR", "GE", "DE",
            "GH", "GR", "GT", "HN", "HK", "HU", "IS", "IN", "ID", "IQ", "IE", "IL", "IT", "JM", "JP",
            "JO", "KZ", "KE", "KW", "LV", "LB", "LY", "LI", "LT", "LU", "MY", "MT", "MX", "ME", "MA",
            "NP", "NL", "NZ", "NI", "NG", "MK", "NO", "OM", "PK", "PA", "PG", "PY", "PE", "PH", "PL",
            "PT", "PR", "QA", "RO", "RU", "SA", "SN", "RS", "SG", "SK", "SI", "ZA", "KR", "ES", "LK",
            "SE", "CH", "TW", "TZ", "TH", "TN", "TR", "UG", "UA", "AE", "GB", "US", "UY", "VE", "VN",
            "YE", "ZW"
    );

    @Override
    public List<Localization> getSupportedLocalizations() {
        return SUPPORTED_LANGUAGES;
    }

    @Override
    public List<ContentCountry> getSupportedCountries() {
        return SUPPORTED_COUNTRIES;
    }
}
