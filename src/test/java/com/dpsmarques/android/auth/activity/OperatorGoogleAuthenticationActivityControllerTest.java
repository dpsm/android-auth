package com.dpsmarques.android.auth.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import com.dpsmarques.android.auth.GoogleOAuthTokenOnSubscribe;
import com.dpsmarques.android.auth.GoogleOauthTokenObservable;
import com.dpsmarques.android.auth.OperatorGoogleAuthenticationController;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.UserRecoverableAuthException;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;

import rx.Observable;
import rx.Observer;
import rx.observers.Observers;

import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml", emulateSdk = 18)
public class OperatorGoogleAuthenticationActivityControllerTest extends TestCase {

    @Test(expected = IllegalArgumentException.class)
    public void givenNullActivityWhenInstantiatedThenThrows() {
        new OperatorGoogleAuthenticationActivityController(null, 0);
    }

    @Test
    public void givenObservableEmitsTokenWhenSubscribedThenListenerCalled() throws IOException, GoogleAuthException {
        final GoogleAuthenticationActivity activity = mock(GoogleAuthenticationActivity.class);
        final GoogleOAuthTokenOnSubscribe onSubscribe = spy(new GoogleOAuthTokenOnSubscribe(activity, "com.google"));
        doReturn("token_token").when(onSubscribe).getToken();

        final Observable<String> observable = Observable.create(onSubscribe)
                .lift(new OperatorGoogleAuthenticationActivityController(activity, 0));

        observable.subscribe();

        verify(activity, never()).onAuthenticationError(Matchers.any(Throwable.class));
        verify(activity, never()).onRetryAuthentication();
        verify(activity).onAuthenticationSucceeded("token_token");
    }

    @Test
    public void givenObservableThrowsExceptionWhenSubscribedListenerCalled() throws IOException, GoogleAuthException {
        final GoogleAuthenticationActivity activity = mock(GoogleAuthenticationActivity.class);
        final GoogleOAuthTokenOnSubscribe onSubscribe = spy(new GoogleOAuthTokenOnSubscribe(activity, "com.google"));

        final Throwable throwable = new IOException();
        doThrow(throwable).when(onSubscribe).getToken();

        final Observable<String> observable = Observable.create(onSubscribe)
                .lift(new OperatorGoogleAuthenticationActivityController(activity, 0));

        final Observer<String> observer = mock(StringObserver.class);
        observable.subscribe(observer);

        verify(observer).onError(throwable);
        verify(observer, never()).onNext(Matchers.anyString());
        verify(observer, never()).onCompleted();

        verify(activity).onAuthenticationError(throwable);
        verify(activity, never()).onRetryAuthentication();
        verify(activity, never()).onAuthenticationSucceeded(Matchers.anyString());
    }

    @Test
    public void givenObservableThrowsUserRecoverableAuthExceptionWhenSubscribedListenerCalled() throws Exception {
        final GoogleAuthenticationActivity activity = mock(GoogleAuthenticationActivity.class);
        final GoogleOAuthTokenOnSubscribe onSubscribe = spy(new GoogleOAuthTokenOnSubscribe(activity, "com.google"));

        final Intent intent = new Intent();
        final Throwable throwable = new UserRecoverableAuthException("", intent);
        doThrow(throwable).when(onSubscribe).getToken();

        final Observable<String> observable = Observable.create(onSubscribe)
                .lift(new OperatorGoogleAuthenticationActivityController(activity, 0));

        final Observer<String> observer = mock(StringObserver.class);
        observable.subscribe(observer);

        verify(activity, never()).onAuthenticationError(throwable);
        verify(activity, never()).onRetryAuthentication();
        verify(activity, never()).onAuthenticationSucceeded(Matchers.anyString());

        verify(activity).startActivityForResult(intent, 1);
    }

    @Test
    public void givenObservableThrowsUserRecoverableAuthExceptionWhenSubscribedAndUserResolvesThenListenerCalled() throws Exception {
        final GoogleAuthenticationActivity activity = mock(GoogleAuthenticationActivity.class);
        final GoogleOAuthTokenOnSubscribe onSubscribe = spy(new GoogleOAuthTokenOnSubscribe(activity, "com.google"));

        final Intent intent = new Intent();
        final Throwable throwable = new UserRecoverableAuthException("", intent);
        doThrow(throwable).when(onSubscribe).getToken();

        final OperatorGoogleAuthenticationActivityController controller
                = new OperatorGoogleAuthenticationActivityController(activity, 0);
        final Observable<String> observable = Observable.create(onSubscribe).lift(controller);

        final Observer<String> observer = mock(StringObserver.class);
        observable.subscribe(observer);

        verify(activity, never()).onAuthenticationError(throwable);
        verify(activity, never()).onRetryAuthentication();
        verify(activity, never()).onAuthenticationSucceeded(Matchers.anyString());

        final ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(activity).startActivityForResult(eq(intent), captor.capture());

        controller.handleActivityResult(captor.getValue(), Activity.RESULT_OK, null);

        verify(activity).onRetryAuthentication();
    }

    private static abstract class GoogleAuthenticationActivity extends Activity
            implements OperatorGoogleAuthenticationController.GoogleAuthenticationListener {

    }

    private static abstract class StringObserver implements Observer<String> {
    }
}