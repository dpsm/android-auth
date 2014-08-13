package com.dpsmarques.android.auth;

import android.app.Activity;
import android.content.Intent;

import com.google.android.gms.auth.UserRecoverableAuthException;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import rx.Observable;
import rx.Observer;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml", emulateSdk = 18)
public class GoogleOAuthTokenOnSubscribeTest extends TestCase {

    private static final String GOOGLE_PRINT_SCOPE = "oauth2:https://www.googleapis.com/auth/cloudprint";

    public static final String TOKEN = "token_token";

    @Test(expected = IllegalArgumentException.class)
    public void givenNullArgumentsThrows() {
        new GoogleOAuthTokenOnSubscribe(null, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenNullAccountNameThrows() {
        new GoogleOAuthTokenOnSubscribe(Robolectric.application, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenNullContextThrows() {
        new GoogleOAuthTokenOnSubscribe(null, "", "");
    }

    @Test
    public void givenValidArgumentsWhenCreatedCreates() {
        final GoogleOAuthTokenOnSubscribe onSubscribe =
                new GoogleOAuthTokenOnSubscribe(Robolectric.application, "someone", "scope");
        assertNotNull(onSubscribe);
    }

    @Test
    public void givenTokenAvailableWhenSubscribedTokenEmitted() throws Exception {
        final GoogleOAuthTokenOnSubscribe onSubscribe = Mockito.spy(
                new GoogleOAuthTokenOnSubscribe(Robolectric.application, "com.google", GOOGLE_PRINT_SCOPE));
        Mockito.doReturn(TOKEN).when(onSubscribe).getToken();

        final Observable<String> observable = Observable.create(onSubscribe);
        final Observer<String> observer = Mockito.mock(StringObserver.class);
        observable.subscribe(observer);

        Mockito.verify(observer).onNext(TOKEN);
        Mockito.verify(observer).onCompleted();
        Mockito.verify(observer, Mockito.never()).onError(Matchers.any(Throwable.class));
    }

    @Test
    public void givenTokenRetrievalFailsWhenSubscribedOnErrorCalled() throws Exception {
        final GoogleOAuthTokenOnSubscribe onSubscribe = Mockito.spy(
                new GoogleOAuthTokenOnSubscribe(Robolectric.application, "com.google", GOOGLE_PRINT_SCOPE));

        final Throwable throwable = new IllegalStateException();
        Mockito.doThrow(throwable).when(onSubscribe).getToken();

        final Observable<String> observable = Observable.create(onSubscribe);
        final Observer<String> observer = Mockito.mock(StringObserver.class);
        observable.subscribe(observer);

        Mockito.verify(observer, Mockito.never()).onNext(Matchers.anyString());
        Mockito.verify(observer, Mockito.never()).onCompleted();
        Mockito.verify(observer).onError(throwable);
    }

    private static abstract class StringObserver implements Observer<String> {
    }
}