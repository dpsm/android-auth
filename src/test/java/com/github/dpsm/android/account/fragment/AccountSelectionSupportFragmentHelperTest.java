package com.github.dpsm.android.account.fragment;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.github.dpsm.android.account.fragment.AccountSelectionSupportFragmentHelper;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE, emulateSdk = 18)
public class AccountSelectionSupportFragmentHelperTest extends TestCase {

    @Test(expected = IllegalArgumentException.class)
    public void givenNullFragmentThenInstantiationFails() {
        new AccountSelectionSupportFragmentHelper(null, 1);
        fail("Instantiation should have failed.");
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenFragmentNotImplementAccountSelectionListenerInstantiationFails() {
        final Fragment fragment = Mockito.mock(Fragment.class);
        new AccountSelectionSupportFragmentHelper(fragment, 1);
        fail("Instantiation should have failed.");
    }

    @Test
    public void givenFragmentImplementsAccountSelectionListenerInstantiationSucceeds() {
        final AccountSelectionFragment fragment = Mockito.mock(AccountSelectionFragment.class);
        AccountSelectionSupportFragmentHelper helper = new AccountSelectionSupportFragmentHelper(fragment, 1);
        assertNotNull(helper);
    }

    @Test
    public void givenFragmentWhenAccountSelectedThenStartActivityForResultCalled() throws Exception {
        final AccountSelectionFragment fragment = Mockito.mock(AccountSelectionFragment.class);
        AccountSelectionSupportFragmentHelper helper = Mockito.spy(new AccountSelectionSupportFragmentHelper(fragment, 1));
        assertNotNull(helper);

        final Intent intent = Mockito.mock(Intent.class);
        Mockito.doReturn(intent).when(helper).createAccountSelectionIntent(Matchers.any(String[].class));

        helper.selectUserAccount(null);

        Mockito.verify(helper).startActivityForResult(fragment, intent, 2);
    }

    @Test
    public void givenFragmentWhenOnActivityResultCalledWithCancellationThenListenerInvoked() {
        final AccountSelectionFragment fragment = Mockito.mock(AccountSelectionFragment.class);
        AccountSelectionSupportFragmentHelper helper = Mockito.spy(new AccountSelectionSupportFragmentHelper(fragment, 1));
        assertNotNull(helper);

        final Intent intent = Mockito.mock(Intent.class);
        Mockito.doReturn(intent).when(helper).createAccountSelectionIntent(Matchers.any(String[].class));

        helper.handleActivityResult(2, Activity.RESULT_CANCELED, intent);

        Mockito.verify(fragment, Mockito.never()).onAccountSelected(Matchers.anyString());
        Mockito.verify(fragment).onAccountSelectionCanceled();
    }

    @Test
    public void givenFragmentWhenOnActivityResultCalledWithSelectionThenListenerInvoked() {
        final AccountSelectionFragment fragment = Mockito.mock(AccountSelectionFragment.class);
        AccountSelectionSupportFragmentHelper helper = Mockito.spy(new AccountSelectionSupportFragmentHelper(fragment, 1));
        assertNotNull(helper);

        final Intent intent = Mockito.mock(Intent.class);
        Mockito.doReturn(intent).when(helper).createAccountSelectionIntent(Matchers.any(String[].class));

        Mockito.when(intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)).thenReturn("account_name");
        helper.handleActivityResult(2, Activity.RESULT_OK, intent);

        Mockito.verify(fragment).onAccountSelected("account_name");
        Mockito.verify(fragment, Mockito.never()).onAccountSelectionCanceled();
    }

    @Test
    public void givenFragmentWhenOnActivityResultCalledWithAnotherResultCodeThenListenerNeverInvoked() {
        final AccountSelectionFragment fragment = Mockito.mock(AccountSelectionFragment.class);

        AccountSelectionSupportFragmentHelper helper = Mockito.spy(new AccountSelectionSupportFragmentHelper(fragment, 1));
        assertNotNull(helper);

        final Intent intent = Mockito.mock(Intent.class);
        Mockito.doReturn(intent).when(helper).createAccountSelectionIntent(Matchers.any(String[].class));
        
        helper.handleActivityResult(0, Activity.RESULT_OK, intent);
        helper.handleActivityResult(1, Activity.RESULT_OK, intent);

        Mockito.verify(fragment, Mockito.never()).onAccountSelected(Matchers.anyString());
        Mockito.verify(fragment, Mockito.never()).onAccountSelectionCanceled();
    }

    public static class AccountSelectionFragment extends Fragment
            implements AccountSelectionSupportFragmentHelper.AccountSelectionListener {

        @Override
        public void onAccountSelected(final String accountName) {

        }

        @Override
        public void onAccountSelectionCanceled() {

        }
    }

}