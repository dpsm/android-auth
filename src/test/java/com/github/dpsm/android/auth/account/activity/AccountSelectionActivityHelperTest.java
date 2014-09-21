package com.dpsmarques.android.account.activity;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;

import com.dpsmarques.android.account.activity.AccountSelectionActivityHelper;
import com.google.android.gms.common.AccountPicker;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.robolectric.annotation.Config;

@RunWith(PowerMockRunner.class)
@Config(manifest = Config.NONE, emulateSdk = 18)
@PrepareForTest({AccountPicker.class})
public class AccountSelectionActivityHelperTest extends TestCase {

    private Intent mAccountSelectionIntent;

    @Before
    public void setupAccountPicker() {
        PowerMockito.mockStatic(AccountPicker.class);

        mAccountSelectionIntent = Mockito.mock(Intent.class);
        Mockito.when(AccountPicker.newChooseAccountIntent(null, null, null, false, null, null,
                null, null)).thenReturn(mAccountSelectionIntent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenNullActivityThenInstantiationFails() {
        new AccountSelectionActivityHelper(null, 1);
        fail("Instantiation should have failed.");
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenActivityNotImplementAccountSelectionListenerInstantiationFails() {
        final Activity activity = Mockito.mock(Activity.class);
        new AccountSelectionActivityHelper(activity, 1);
        fail("Instantiation should have failed.");
    }

    @Test
    public void givenActivityImplementsAccountSelectionListenerInstantiationSucceeds() {
        final AccountSelectionActivity activity = Mockito.mock(AccountSelectionActivity.class);
        AccountSelectionActivityHelper helper = new AccountSelectionActivityHelper(activity, 1);
        assertNotNull(helper);
    }

    @Test
    public void givenActivityWhenAccountSelectedThenStartActivityForResultCalled() {
        final AccountSelectionActivity activity = Mockito.mock(AccountSelectionActivity.class);
        AccountSelectionActivityHelper helper = new AccountSelectionActivityHelper(activity, 1);
        assertNotNull(helper);

        helper.selectUserAccount(null);

        Mockito.verify(activity).startActivityForResult(mAccountSelectionIntent, 2);
    }

    @Test
    public void givenActivityWhenOnActivityResultCalledWithCancellationThenListenerInvoked() {
        final AccountSelectionActivity activity = Mockito.mock(AccountSelectionActivity.class);
        AccountSelectionActivityHelper helper = new AccountSelectionActivityHelper(activity, 1);
        assertNotNull(helper);

        final Intent intent = Mockito.mock(Intent.class);
        helper.handleActivityResult(2, Activity.RESULT_CANCELED, intent);

        Mockito.verify(activity, Mockito.never()).onAccountSelected(Matchers.anyString());
        Mockito.verify(activity).onAccountSelectionCanceled();
    }

    @Test
    public void givenActivityWhenOnActivityResultCalledWithSelectionThenListenerInvoked() {
        final AccountSelectionActivity activity = Mockito.mock(AccountSelectionActivity.class);
        AccountSelectionActivityHelper helper = new AccountSelectionActivityHelper(activity, 1);
        assertNotNull(helper);

        final Intent intent = Mockito.mock(Intent.class);
        Mockito.when(intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)).thenReturn("account_name");
        helper.handleActivityResult(2, Activity.RESULT_OK, intent);

        Mockito.verify(activity).onAccountSelected("account_name");
        Mockito.verify(activity, Mockito.never()).onAccountSelectionCanceled();
    }

    @Test
    public void givenActivityWhenOnActivityResultCalledWithAnotherResultCodeThenListenerNeverInvoked() {
        final AccountSelectionActivity activity = Mockito.mock(AccountSelectionActivity.class);
        AccountSelectionActivityHelper helper = new AccountSelectionActivityHelper(activity, 1);
        assertNotNull(helper);

        final Intent intent = Mockito.mock(Intent.class);
        helper.handleActivityResult(0, Activity.RESULT_OK, intent);
        helper.handleActivityResult(1, Activity.RESULT_OK, intent);

        Mockito.verify(activity, Mockito.never()).onAccountSelected(Matchers.anyString());
        Mockito.verify(activity, Mockito.never()).onAccountSelectionCanceled();
    }

    private static abstract class AccountSelectionActivity extends Activity
            implements AccountSelectionActivityHelper.AccountSelectionListener {
    }

}