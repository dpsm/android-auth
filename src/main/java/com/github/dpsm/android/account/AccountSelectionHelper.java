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
package com.github.dpsm.android.account;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;

import com.google.android.gms.common.AccountPicker;

/**
 * This abstract class acts as a helper in selecting accounts from the device. It's concrete
 * subclasses target specific Android components such as android.app.Activity and
 * android.app.Fragment.
 *
 * @see com.github.dpsm.android.account.activity.AccountSelectionActivityHelper
 * @see com.github.dpsm.android.account.fragment.AccountSelectionFragmentHelper
 * @see com.github.dpsm.android.account.fragment.AccountSelectionSupportFragmentHelper
 */
public abstract class AccountSelectionHelper<T> {

    /**
     * Specific Android components such as Activities or Fragments bound to the
     * subclasses of this class must implement this interface in order to interact
     * with the AccountSelectionHelper.
     */
    public static interface AccountSelectionListener {

        /**
         * The specified account was selected.
         *
         * @param accountName name of selected account.
         */
        void onAccountSelected(final String accountName);

        /**
         * Account selection was cancelled.
         */
        void onAccountSelectionCanceled();
    }

    private final int mRequestCodePickAccount;

    private final T mAndroidComponent;

    /**
     * Creates an instance of this class bound to the specified Android component. This class may
     * start Activity instances for account selection hence requires a base request code id.
     *
     * @see android.app.Activity#startActivityForResult(android.content.Intent, int)
     * @see android.app.Fragment#startActivityForResult(android.content.Intent, int)
     *
     * @param androidComponent the Android component to use in order to host account selection.
     * @param requestCodeBase the base numeric value for the request codes associated with this
     *                        helper.
     */
    public AccountSelectionHelper(final T androidComponent, final int requestCodeBase) {
        if (androidComponent == null) {
            throw new IllegalArgumentException("Android component can not be null.");
        }

        if (!(androidComponent instanceof AccountSelectionListener)) {
            throw new IllegalArgumentException(androidComponent.getClass() + " must implement "
                    + AccountSelectionListener.class.getName());
        }
        mRequestCodePickAccount = requestCodeBase + 1;
        mAndroidComponent = androidComponent;
    }

    /**
     * Prompts users to select one account amongst the accounts matching the given types or all
     * if no type is specified.
     *
     * @param accountTypes account types or null for all account types.
     */
    public void selectUserAccount(final String[] accountTypes) {
        final Intent intent = createAccountSelectionIntent(accountTypes);
        startActivityForResult(mAndroidComponent, intent, mRequestCodePickAccount);
    }

    public Intent createAccountSelectionIntent(final String[] accountTypes) {
        return AccountPicker.newChooseAccountIntent(null, null, accountTypes, false,
                null, null, null, null);
    }

    /**
     * Concrete subclasses implement this method in order to launch account selection activities
     * within the component's scope.
     *
     * @see android.app.Activity#startActivityForResult(android.content.Intent, int)
     * @see android.app.Fragment#startActivityForResult(android.content.Intent, int)
     *
     * @param androidComponent The android component to launch the activity.
     * @param intent the intent targeting the account selection activity.
     * @param requestCode the request code.
     */
    protected abstract void startActivityForResult(final T androidComponent,
                                                   final Intent intent,
                                                   final int requestCode);

    /**
     * The Android component bound to this class must call this method to forward the
     * result of the started activity so the correct method on the AccountSelectionListener
     * can be invoked.
     *
     * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
     * @see android.app.Fragment#onActivityResult(int, int, android.content.Intent)
     *
     * @param requestCode The integer request code originally supplied to startActivityForResult(),
     *                    allowing you to identify who this result came from.
     * @param resultCode The integer result code returned by the child activity through its setResult().
     * @param data An Intent, which can return result data to the caller (various data can be
     *             attached to Intent "extras").
     *
     * @return true if handled by the helper, false otherwise.
     */
    public boolean handleActivityResult(final int requestCode,
                                        final int resultCode,
                                        final Intent data) {
        if (requestCode != mRequestCodePickAccount) {
            return false;
        }

        // Receiving a result from the AccountPicker
        if (resultCode == Activity.RESULT_OK) {
            final String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            // With the account name acquired, go get the auth token
            asListener(mAndroidComponent).onAccountSelected(accountName);
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // The account picker dialog closed without selecting an account.
            // Notify users that they must pick an account to proceed.
            asListener(mAndroidComponent).onAccountSelectionCanceled();
        }
        return true;
    }

    private AccountSelectionListener asListener(final T t) {
        return (AccountSelectionListener) t;
    }
}
