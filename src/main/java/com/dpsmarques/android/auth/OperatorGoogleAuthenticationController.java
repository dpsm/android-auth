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

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;

import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.GooglePlayServicesUtil;

import rx.Observable;
import rx.Subscriber;

/**
 * This abstract class implements an Observable.Operator that launches an authentication screen
 * when user permission is required while attempting to obtain a Google OAuth token through an
 * instance of the GoogleOauthTokenObservable class.
 * <br/>
 * When the GoogleOauthTokenObservable fails to emit an OAuth token, this operator handles the
 * recoverable errors by launching a user authentication screen within the Context of an Android
 * component such as an Activity or Fragment.
 * <br/>
 * Concrete subclasses of this class target the respective components mentioned earlier.
 *
 * @see com.dpsmarques.android.auth.GoogleOauthTokenObservable
 * @see com.dpsmarques.android.auth.activity.OperatorGoogleAuthenticationActivityController
 * @see com.dpsmarques.android.auth.fragment.OperatorGoogleAuthenticationFragmentController
 * @see com.dpsmarques.android.auth.fragment.OperatorGoogleAuthenticationSupportFragmentController
 *
 */
public abstract class OperatorGoogleAuthenticationController<T>
        implements Observable.Operator<String, String> {

    /**
     * This interface defines the API for Android components to implement in order to
     * handle the Google authentication flow.
     */
    public static interface GoogleAuthenticationListener {

        /**
         * A non recoverable error happened while attempting to acquire an OAuth token.
         * @param throwable the unrecoverable error.
         */
        void onAuthenticationError(Throwable throwable);

        /**
         * An OAuth token was successfully acquired.
         *
         * @param token the acquired token.
         */
        void onAuthenticationSucceeded(String token);

        /**
         * An attempt to re-authenticate might succeed.
         */
        void onRetryAuthentication();
    }

    private final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR;

    private final T mAndroidComponent;

    /**
     * Creates an instance of this class bound to the specified Android component ans starts the
     * authentication Activity using the specified request code as a base number.
     *
     * @param androidComponent an Android component capable of launching Activities.
     * @param requestCodeBase base request code.
     */
    public OperatorGoogleAuthenticationController(final T androidComponent,
                                                  final int requestCodeBase) {
        if (androidComponent == null) {
            throw new IllegalArgumentException("Android component can not be null.");
        }

        if (!(androidComponent instanceof GoogleAuthenticationListener)) {
            throw new IllegalArgumentException(androidComponent.getClass() + " must implement "
                    + GoogleAuthenticationListener.class.getName());
        }
        mAndroidComponent = androidComponent;
        REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = requestCodeBase + 1;
    }

    @Override
    public Subscriber<? super String> call(final Subscriber<? super String> subscriber) {
        return new ActivityDispatchSubscriber<T>(this, subscriber);
    }

    /**
     * Handles the Activity result on behalf of the caller.
     *
     * @param requestCode The integer request code originally supplied to startActivityForResult(),
     *                    allowing you to identify who this result came from.
     * @param resultCode The integer result code returned by the child activity through its
     *                   setResult().
     * @param data An Intent, which can return result data to the caller (various data can be
     *             attached to Intent "extras").
     * @return true if handled, false otherwise.
     */
    public boolean handleActivityResult(final int requestCode, final int resultCode,
                                        final Intent data) {
        if (requestCode == REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR) {
            if (resultCode == Activity.RESULT_OK) {
                // Receiving a result that follows a GoogleAuthException, try auth again
                asListener(mAndroidComponent).onRetryAuthentication();
            }
            return true;
        }
        return false;
    }

    /**
     * Concrete subclasses implement this method in order to launch authentication activities
     * within the component's scope.
     *
     * @see android.app.Activity#startActivityForResult(android.content.Intent, int)
     * @see android.app.Fragment#startActivityForResult(android.content.Intent, int)
     *
     * @param androidComponent The android component to launch the activity.
     * @param intent the intent targeting the authentication activity.
     * @param requestCode the request code.
     */
    protected abstract void startActivityForResult(final T androidComponent,
                                                   final Intent intent,
                                                   final int requestCode);

    /**
     * Get the Activity bound to the specified Android component.
     *
     * @param androidComponent target Android component.
     * @return component's Activity.
     */
    protected abstract Activity getActivity(final T androidComponent);

    private static final class ActivityDispatchSubscriber<T> extends Subscriber<String> {

        private final OperatorGoogleAuthenticationController<T> mController;

        private final Subscriber<? super String> mChild;

        private ActivityDispatchSubscriber(final OperatorGoogleAuthenticationController<T> controller,
                                           final Subscriber<? super String> subscriber) {
            if (controller == null) {
                throw new IllegalArgumentException("OperatorGoogleAuthenticationController can not" +
                        "be null.");
            }
            mController = controller;
            mChild = subscriber;
        }

        @Override
        public void onCompleted() {
            mChild.onCompleted();
        }

        @Override
        public void onError(final Throwable throwable) {
            final T androidComponent = mController.mAndroidComponent;
            if (throwable instanceof GooglePlayServicesAvailabilityException) {
                // The Google Play services APK is old, disabled, or not present.
                // Show a dialog created by Google Play services that allows
                // the user to update the APK
                int statusCode = ((GooglePlayServicesAvailabilityException) throwable)
                        .getConnectionStatusCode();
                final Dialog dialog = GooglePlayServicesUtil.getErrorDialog(statusCode,
                        mController.getActivity(androidComponent),
                        mController.REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                dialog.show();
            } else if (throwable instanceof UserRecoverableAuthException) {
                // Unable to authenticate, such as when the user has not yet granted
                // the app access to the account, but the user can fix this.
                // Forward the user to an activity in Google Play services.
                Intent intent = ((UserRecoverableAuthException) throwable).getIntent();
                mController.startActivityForResult(mController.mAndroidComponent,
                        intent,
                        mController.REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
            } else {
                mController.asListener(mController.mAndroidComponent).onAuthenticationError(throwable);
                mChild.onError(throwable);
            }
        }

        @Override
        public void onNext(final String token) {
            mController.asListener(mController.mAndroidComponent).onAuthenticationSucceeded(token);
            mChild.onNext(token);
        }
    }

    private GoogleAuthenticationListener asListener(final T mAndroidComponent) {
        return (GoogleAuthenticationListener) mAndroidComponent;
    }
}
