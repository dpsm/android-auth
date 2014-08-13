package com.dpsmarques.android.auth;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.support.v4.app.FragmentActivity;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml", emulateSdk = 18)
public class GoogleOauthTokenObservableTest extends TestCase {

    private static final String GOOGLE_PRINT_SCOPE = "oauth2:https://www.googleapis.com/auth/cloudprint";

    public static final String TOKEN = "token_token";

    @Test(expected = IllegalArgumentException.class)
    public void givenNullContextWhenCreateCalledThrowsException() throws Exception {
        GoogleOauthTokenObservable.create((Context) null, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenNullFragmentWhenCreateCalledThrowsException() throws Exception {
        GoogleOauthTokenObservable.create((Fragment) null, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenNullSupportFragmentWhenCreateCalledThrowsException() throws Exception {
        GoogleOauthTokenObservable.create((android.support.v4.app.Fragment) null, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenNullContextWithValidAccountWhenCreateCalledThrowsException() throws Exception {
        GoogleOauthTokenObservable.create((Context) null, "", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenNullFragmentWithValidAccountWhenCreateCalledThrowsException() throws Exception {
        GoogleOauthTokenObservable.create((Fragment) null, "", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenNullSupportFragmentWithValidAccountWhenCreateCalledThrowsException() throws Exception {
        GoogleOauthTokenObservable.create((android.support.v4.app.Fragment) null, "", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenContextWithNullAccountWhenCreateCalledThenThrows() throws Exception {
        GoogleOauthTokenObservable.create(Robolectric.application, null, null);
        fail("Should not create instance");
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenFragmentWithNullAccountWhenCreateCalledThenThrows() throws Exception {
        GoogleOauthTokenObservable.create(Mockito.mock(Fragment.class), null, null);
        fail("Should not create instance");
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenSupportFragmentWithNullAccountWhenCreateCalledThenThrows() throws Exception {
        GoogleOauthTokenObservable.create(Mockito.mock(android.support.v4.app.Fragment.class), null, null);
        fail("Should not create instance");
    }

    @Test
    public void givenContextWithAccountWhenCreateCalledThenSucceeds() throws Exception {
        final GoogleOauthTokenObservable observable = GoogleOauthTokenObservable
                .create(Robolectric.application, "com.google", GOOGLE_PRINT_SCOPE);
        assertNotNull(observable);
    }

    @Test
    public void givenFragmentWithAccountWhenCreateCalledThenSucceeds() throws Exception {
        final Fragment fragment = Mockito.mock(Fragment.class);
        Mockito.when(fragment.getActivity()).thenReturn(Mockito.mock(Activity.class));
        final GoogleOauthTokenObservable observable = GoogleOauthTokenObservable
                .create(fragment, "com.google", GOOGLE_PRINT_SCOPE);
        assertNotNull(observable);
    }

    @Test
    public void givenSupportFragmentWithAccountWhenCreateCalledThenSucceeds() throws Exception {
        final android.support.v4.app.Fragment fragment = Mockito.mock(android.support.v4.app.Fragment.class);
        Mockito.when(fragment.getActivity()).thenReturn(Mockito.mock(FragmentActivity.class));
        final GoogleOauthTokenObservable observable = GoogleOauthTokenObservable
                .create(fragment, "com.google", GOOGLE_PRINT_SCOPE);
        assertNotNull(observable);
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenObservableWhenAuthenticateWithNullActivityThenThrows() throws Exception {
        final GoogleOauthTokenObservable observable = GoogleOauthTokenObservable
                .create(Robolectric.application, "com.google", GOOGLE_PRINT_SCOPE);
        assertNotNull(observable);

        observable.authenticateUsing((Activity) null, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenObservableWhenAuthenticateWithNullFragmentThenThrows() throws Exception {
        final Fragment fragment = Mockito.mock(Fragment.class);
        Mockito.when(fragment.getActivity()).thenReturn(Mockito.mock(Activity.class));
        final GoogleOauthTokenObservable observable = GoogleOauthTokenObservable
                .create(fragment, "com.google", GOOGLE_PRINT_SCOPE);
        assertNotNull(observable);

        observable.authenticateUsing((Fragment) null, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenObservableWhenAuthenticateWithNullSupportFragmentThenThrows() throws Exception {
        final android.support.v4.app.Fragment fragment = Mockito.mock(android.support.v4.app.Fragment.class);
        Mockito.when(fragment.getActivity()).thenReturn(Mockito.mock(FragmentActivity.class));
        final GoogleOauthTokenObservable observable = GoogleOauthTokenObservable
                .create(fragment, "com.google", GOOGLE_PRINT_SCOPE);
        assertNotNull(observable);

        observable.authenticateUsing((android.support.v4.app.Fragment) null, 0);
    }

}