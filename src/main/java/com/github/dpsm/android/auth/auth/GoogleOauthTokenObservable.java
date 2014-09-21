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
package com.dpsmarques.android.auth;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.dpsmarques.android.auth.activity.OperatorGoogleAuthenticationActivityController;
import com.dpsmarques.android.auth.fragment.OperatorGoogleAuthenticationFragmentController;
import com.dpsmarques.android.auth.fragment.OperatorGoogleAuthenticationSupportFragmentController;

import rx.Observable;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * This class implements an Observable that emits access tokens to access the Google APIs under
 * a specific Google account.
 */
public class GoogleOauthTokenObservable extends Observable<String> {

    /**
     * Creates an instance of a GoogleOauthTokenObservable bound to the specified Context
     * to emit access tokens for the specified account.
     *
     * @param context the context to use to interact with the Android system.
     * @param accountName the target Google account name.
     * @param scope OAuth token scope.
     * @return an instance of the Observable.
     */
    public static GoogleOauthTokenObservable create(final Context context,
                                                    final String accountName,
                                                    final String scope) {
        if (context == null) {
            throw new IllegalArgumentException("Context can not be null!");
        }

        if (TextUtils.isEmpty(accountName)) {
            throw new IllegalArgumentException("Account name can not be null!");
        }

        return new GoogleOauthTokenObservable(context, accountName, scope);
    }

    /**
     * Creates an instance of a GoogleOauthTokenObservable bound to the specified Fragment's
     * Activity context to emit access tokens for the specified account.
     *
     * @param fragment the Fragment from which Activity context to use to interact with the
     *                 Android system.
     * @param accountName the target Google account name.
     * @param scope OAuth token scope.
     * @return an instance of the Observable.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static GoogleOauthTokenObservable create(final Fragment fragment,
                                                    final String accountName,
                                                    final String scope) {
        if (fragment == null) {
            throw new IllegalArgumentException("Fragment can not be null!");
        }

        return create(fragment.getActivity(), accountName, scope);
    }

    /**
     * Creates an instance of a GoogleOauthTokenObservable bound to the specified Fragment's
     * Activity context to emit access tokens for the specified account.
     *
     * @param fragment the Fragment from which Activity context to use to interact with the
     *                 Android system.
     * @param accountName the target Google account name.
     * @param scope OAuth token scope.
     * @return an instance of the Observable.
     */
    public static GoogleOauthTokenObservable create(final android.support.v4.app.Fragment fragment,
                                                    final String accountName,
                                                    final String scope) {

        if (fragment == null) {
            throw new IllegalArgumentException("Fragment can not be null!");
        }

        return create(fragment.getActivity(), accountName, scope);
    }

    /**
     * Creates an instance of a GoogleOauthTokenObservable bound to the specified Context
     * to emit access tokens for the specified account.
     *
     * @param context the context to use to interact with the Android system.
     * @param accountName the target Google account name.
     * @param scope OAuth toke scope.
     */
    public GoogleOauthTokenObservable(final Context context, final String accountName,
                                      final String scope) {
        super(new GoogleOAuthTokenOnSubscribe(context, accountName, scope));
    }

    /**
     * Creates an instance of a GoogleOauthTokenObservable invoking the specified
     * GoogleOAuthTokenOnSubscribe instance.
     *
     * @param onSubscribe target GoogleOAuthTokenOnSubscribe instance.
     */
    public GoogleOauthTokenObservable(final GoogleOAuthTokenOnSubscribe onSubscribe) {
        super(onSubscribe);
    }

    /**
     * Authenticates the user when needed using the specified Activity to launch the authentication
     * Activity and using request codes starting with the specified base request code.
     *
     * @see android.app.Activity#startActivityForResult(android.content.Intent, int)
     *
     * @param activity the Activity to use when needed to launch the authentication Activity.
     * @param requestCodeBase the base request code to use when launching the authentication Activity.
     * @return an Observable which will launch an authentication Activity from the specified Activity
     * when user authentication is required.
     */
    public Observable<String> authenticateUsing(final Activity activity, final int requestCodeBase) {
        if (activity == null) {
            throw new IllegalArgumentException("Activity can not be null.");
        }

        return AndroidObservable.bindActivity(activity, this)
                .lift(new OperatorGoogleAuthenticationActivityController(activity, requestCodeBase));
    }

    /**
     * Authenticates the user when needed using the specified Fragment to launch the authentication
     * Activity and using request codes starting with the specified base request code.
     *
     * @see android.app.Fragment#startActivityForResult(android.content.Intent, int)
     *
     * @param fragment the Fragment to use when needed to launch the authentication Activity.
     * @param requestCodeBase the base request code to use when launching the authentication Activity.
     * @return an Observable which will launch an authentication Activity from the specified Fragment
     * when user authentication is required.
     */
    public Observable<String> authenticateUsing(final Fragment fragment, final int requestCodeBase) {
        if (fragment == null) {
            throw new IllegalArgumentException("Fragment can not be null.");
        }

        return AndroidObservable.bindFragment(fragment, this)
                .lift(new OperatorGoogleAuthenticationFragmentController(fragment, requestCodeBase));
    }

    /**
     * Authenticates the user when needed using the specified Fragment to launch the authentication
     * Activity and using request codes starting with the specified base request code.
     *
     * @see android.app.Fragment#startActivityForResult(android.content.Intent, int)
     *
     * @param fragment the Fragment to use when needed to launch the authentication Activity.
     * @param requestCodeBase the base request code to use when launching the authentication Activity.
     * @return an Observable which will launch an authentication Activity from the specified Fragment
     * when user authentication is required.
     */
    public Observable<String> authenticateUsing(final android.support.v4.app.Fragment fragment,
                                                final int requestCodeBase) {
        if (fragment == null) {
            throw new IllegalArgumentException("Support fragment can not be null.");
        }

        return AndroidObservable.bindFragment(fragment, this)
                .lift(new OperatorGoogleAuthenticationSupportFragmentController(fragment,
                        requestCodeBase));
    }
}
