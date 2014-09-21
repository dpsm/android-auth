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
package com.dpsmarques.android.auth.activity;

import android.app.Activity;
import android.content.Intent;

import com.dpsmarques.android.auth.OperatorGoogleAuthenticationController;

/**
 * This class extends OperatorGoogleAuthenticationController binding the authentication flow
 * to an Android Activity managed life-cycle.
 */
public class OperatorGoogleAuthenticationActivityController extends
        OperatorGoogleAuthenticationController<Activity> {

    /**
     * Creates an instance of this class bound to the specified Android component ans starts the
     * authentication Activity using the specified request code as a base number.
     *
     * @param activity an Activity capable of launching Activities.
     * @param requestCodeBase base request code.
     */
    public OperatorGoogleAuthenticationActivityController(final Activity activity,
                                                          final int requestCodeBase) {
        super(activity, requestCodeBase);
    }

    @Override
    protected void startActivityForResult(final Activity androidComponent,
                                          final Intent intent,
                                          final int requestCode) {
        androidComponent.startActivityForResult(intent, requestCode);
    }

    @Override
    protected Activity getActivity(final Activity androidComponent) {
        return androidComponent;
    }
}
