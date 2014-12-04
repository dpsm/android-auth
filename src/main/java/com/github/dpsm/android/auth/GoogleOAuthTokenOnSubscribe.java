/*
 * Copyright (C) 2014 David Marques.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.dpsm.android.auth;

import android.content.Context;
import android.text.TextUtils;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;

import java.io.IOException;

import rx.Observable;
import rx.Subscriber;

/**
 * This class implements Observable.OnSubscribe in order to emit Google OAuth access tokens.
 *
 * @see com.google.android.gms.auth.GoogleAuthUtil#getToken(android.content.Context, String, String)
 */
public class GoogleOAuthTokenOnSubscribe implements Observable.OnSubscribe<String> {

    private final Context mContext;

    private final String mAccountName;

    private final String mScope;

    /**
     * Creates an instance of a GoogleOAuthTokenOnSubscribe bound to the specified Context
     * to emit access tokens for the specified account.
     *
     * @param context the context to use to interact with the Android system.
     * @param accountName the target Google account name.
     * @param scope the OAuth token scope.
     */
    public GoogleOAuthTokenOnSubscribe(final Context context, final String accountName,
                                       final String scope) {
        if (context == null) {
            throw new IllegalArgumentException("Context can not be null.");
        }

        if (TextUtils.isEmpty(accountName)) {
            throw new IllegalArgumentException("Account name can not be null or empty.");
        }

        if (TextUtils.isEmpty(scope)) {
            throw new IllegalArgumentException("Token scope can not be null or empty.");
        }

        mContext = context;
        mAccountName = accountName;
        mScope = scope;
    }

    @Override
    public void call(final Subscriber<? super String> subscriber) {
        try {
            final String accessToken = getToken();
            subscriber.onNext(accessToken);
            subscriber.onCompleted();
        } catch (IOException e) {
            // network or server error, the call is expected to succeed if you try again later.
            // Don't attempt to call again immediately - the request is likely to
            // fail, you'll hit quotas or back-off.
            subscriber.onError(e);
        } catch (UserRecoverableAuthException e) {
            // Recover
            subscriber.onError(e);
        } catch (GoogleAuthException e) {
            // Failure. The call is not expected to ever succeed so it should not be
            // retried.
            subscriber.onError(e);
        } catch (Exception e) {
            subscriber.onError(e);
        }
    }

    public String getToken() throws GoogleAuthException, IOException {
        return GoogleAuthUtil.getToken(mContext, mAccountName, mScope);
    }
}
