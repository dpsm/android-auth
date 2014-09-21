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
package com.dpsmarques.android.account.activity;

import android.app.Activity;
import android.content.Intent;

import com.dpsmarques.android.account.AccountSelectionHelper;

/**
 * This class extends AccountSelectionHelper in order to allow the account selection process to
 * be bound to the Activity managed lifecycle.
 */
public class AccountSelectionActivityHelper extends AccountSelectionHelper<Activity> {

    /**
     * Creates a helper instance bound to the specified Activity and uses the base request
     * code for starting the account selection Activity if needed.
     * <br/>
     *
     * The specified Activity must implement AccountSelectionListener and must forward calls
     * from it's onActivityResult(..) to this class.
     *
     * @param activity target Activity to bind to.
     * @param requestCodeBase base request code to start the account selection Activity.
     */
    public AccountSelectionActivityHelper(final Activity activity, final int requestCodeBase) {
        super(activity, requestCodeBase);
    }

    @Override
    protected void startActivityForResult(final Activity androidComponent,
                                          final Intent intent,
                                          final int requestCode) {
        androidComponent.startActivityForResult(intent, requestCode);
    }
}
