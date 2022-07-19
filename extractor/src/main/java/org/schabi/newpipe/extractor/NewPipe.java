package org.schabi.newpipe.extractor;

/*
 * Created by Christian Schabesberger on 23.08.15.
 *
 * Copyright (C) Christian Schabesberger 2015 <chris.schabesberger@mailbox.org>
 * NewPipe.java is part of NewPipe.
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

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;

import org.schabi.newpipe.extractor.downloader.Downloader;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.exceptions.ReCaptchaException;
import org.schabi.newpipe.extractor.localization.ContentCountry;
import org.schabi.newpipe.extractor.localization.Localization;
import org.schabi.newpipe.extractor.utils.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.io.IOException;
import java.util.List;

/**
 * Provides access to streaming services supported by NewPipe.
 */
public class NewPipe {
    private static Downloader downloader;
    private static Localization preferredLocalization;
    private static ContentCountry preferredContentCountry;
    private static String visitorData;
    private static boolean isLogined;
    private static String feedbackToken;

    private static LoginStatusCallback loginStatusCallback;

    private NewPipe() {
    }

    public static void init(Downloader d) {
        loginStatusCallback = null;
        isLogined = true;
        downloader = d;
        preferredLocalization = Localization.DEFAULT;
        preferredContentCountry = ContentCountry.DEFAULT;
    }

    public static void init(Downloader d, Localization l) {
        loginStatusCallback = null;
        isLogined = true;
        downloader = d;
        preferredLocalization = l;
        preferredContentCountry = l.getCountryCode().isEmpty() ? ContentCountry.DEFAULT : new ContentCountry(l.getCountryCode());
    }

    public static void init(Downloader d, Localization l, ContentCountry c) {
        loginStatusCallback = null;
        isLogined = true;
        downloader = d;
        preferredLocalization = l;
        preferredContentCountry = c;
    }

    public static Downloader getDownloader() {
        return downloader;
    }

    /*//////////////////////////////////////////////////////////////////////////
    // Utils
    //////////////////////////////////////////////////////////////////////////*/

    public static List<StreamingService> getServices() {
        return ServiceList.all();
    }

    public static StreamingService getService(int serviceId) throws ExtractionException {
        for (StreamingService service : ServiceList.all()) {
            if (service.getServiceId() == serviceId) {
                return service;
            }
        }
        throw new ExtractionException("There's no service with the id = \"" + serviceId + "\"");
    }

    public static StreamingService getService(String serviceName) throws ExtractionException {
        for (StreamingService service : ServiceList.all()) {
            if (service.getServiceInfo().getName().equals(serviceName)) {
                return service;
            }
        }
        throw new ExtractionException("There's no service with the name = \"" + serviceName + "\"");
    }

    public static StreamingService getServiceByUrl(String url) throws ExtractionException {
        for (StreamingService service : ServiceList.all()) {
            if (service.getLinkTypeByUrl(url) != StreamingService.LinkType.NONE) {
                return service;
            }
        }
        throw new ExtractionException("No service can handle the url = \"" + url + "\"");
    }

    public static int getIdOfService(String serviceName) {
        try {
            return getService(serviceName).getServiceId();
        } catch (ExtractionException ignored) {
            return -1;
        }
    }

    public static String getNameOfService(int id) {
        try {
            return getService(id).getServiceInfo().getName();
        } catch (Exception e) {
            System.err.println("Service id not known");
            e.printStackTrace();
            return "<unknown>";
        }
    }

    /*//////////////////////////////////////////////////////////////////////////
    // Localization
    //////////////////////////////////////////////////////////////////////////*/

    public static void setupLocalization(Localization preferredLocalization) {
        setupLocalization(preferredLocalization, null);
    }

    public static void setupLocalization(Localization preferredLocalization, @Nullable ContentCountry preferredContentCountry) {
        NewPipe.preferredLocalization = preferredLocalization;

        if (preferredContentCountry != null) {
            NewPipe.preferredContentCountry = preferredContentCountry;
        } else {
            NewPipe.preferredContentCountry = preferredLocalization.getCountryCode().isEmpty()
                    ? ContentCountry.DEFAULT
                    : new ContentCountry(preferredLocalization.getCountryCode());
        }
    }

    @Nonnull
    public static Localization getPreferredLocalization() {
        return preferredLocalization == null ? Localization.DEFAULT : preferredLocalization;
    }

    public static void setPreferredLocalization(Localization preferredLocalization) {
        NewPipe.preferredLocalization = preferredLocalization;
    }

    @Nonnull
    public static ContentCountry getPreferredContentCountry() {
        return preferredContentCountry == null ? ContentCountry.DEFAULT : preferredContentCountry;
    }

    public static void setPreferredContentCountry(ContentCountry preferredContentCountry) {
        NewPipe.preferredContentCountry = preferredContentCountry;
    }

    public static void setVisitorData(String visitorData) {
        NewPipe.visitorData = visitorData;
    }

    public static String getVisitorData() {
        return NewPipe.visitorData;
    }

    public static void parseLoginStatus(JsonObject responseObject) {
        //判断是否已登录
        if ((responseObject != null) && responseObject.has("responseContext")) {
            JsonObject responseContext = responseObject.getObject("responseContext");
            JsonArray serviceTrackingParams = responseContext.getArray("serviceTrackingParams");
            boolean findedLoginKey = false;
            for (final Object serviceTrackingParam: serviceTrackingParams) {
                if (findedLoginKey) {
                    break;
                }

                if (((JsonObject)serviceTrackingParam).has("params")) {
                    JsonArray params = ((JsonObject)serviceTrackingParam).getArray("params");
                    for (final Object param: params) {
                        String key = ((JsonObject)param).getString("key");
                        if (key.equalsIgnoreCase("logged_in")) {
                            findedLoginKey = true;
                            String value = ((JsonObject)param).getString("value");
                            if (value.equals("1")) {
                                NewPipe.setIsLogined(true);
                            } else {
                                NewPipe.setIsLogined(false);
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    public static void setIsLogined(boolean isLogined) {
        if ((NewPipe.isLogined != isLogined) && (NewPipe.loginStatusCallback != null)) {
            if (isLogined) {
                NewPipe.loginStatusCallback.login();
            } else {
                NewPipe.loginStatusCallback.logout();
            }
        }

        NewPipe.isLogined = isLogined;
    }

    public static boolean isLogined() {
        return isLogined;
    }

    public static void setLoginStatusCallback(LoginStatusCallback callback) {
        NewPipe.loginStatusCallback = callback;
    }

    public interface LoginStatusCallback {
        void login();
        void logout();
    }

    public static void setFeedbackToken(String feedbackToken) {
        NewPipe.feedbackToken = feedbackToken;
    }

    public static String getFeedbackToken() {
        return feedbackToken;
    }
}
