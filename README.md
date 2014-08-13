android-auth
============
An Android library to facilitate the **selection of user accounts** from the device and in **obtaining Google OAuth tokens** from the Google Play Services library.

Usage
---------

The sections below illustrate how to use the library APIs in more detail.

### AccountSelectionActivityHelper

This class is part of a family of helper classes that ease the integration with selecting a device account.

```
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mAccountSelectionHelper = new AccountSelectionActivityHelper(this, REQUEST_CODE_BASE);
    ...
}

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (mAccountSelectionHelper.handleActivityResult(requestCode, resultCode, data)) {
        return; // Handled by helper...
    }
    ...
}

// The methods below to be implemented by the Activity/Fragment

@Override
public void onAccountSelected(final String accountName) {
    // An account has been selected. Do something with it :)
}

@Override
public void onAccountSelectionCanceled() {
    // User cancelled selection...
}

```

> **Tip:** There are similar helper classes for the **android.app.Fragment** and **android.support.v4.app.Fragment** types.

### GoogleOauthTokenObservable
This class facilitates obtaining Google OAuth tokens for a specified scope. The API is based on [rxjava][1].
```
@Override
public void onAccountSelected(final String accountName) {
    GoogleOauthTokenObservable.create(this, accountName, GOOGLE_PRINT_SCOPE)
        .subscribeOn(Schedulers.io())
        .subscribe(new Action1<String>() {
            @Override
            public void call(final String token) {
                // Do something with the token!
            }
        });
    ...
}
```
Given that there might be a need for the user to authorize access to your application though a Google Play authorization screen, we provide an operator that hooks into a Fragment or Activity to ease the authorization flow.
```
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mAuthenticationHelper = new OperatorGoogleAuthenticationActivityController(this, REQUEST_CODE_BASE + 100);
    ...
}

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
   if (mAuthenticationHelper.handleActivityResult(requestCode, resultCode, data)) {
       return; // Handled by helper...
   }
   ...
}

@Override
public void onAccountSelected(final String accountName) {
    GoogleOauthTokenObservable.create(this, accountName, GOOGLE_PRINT_SCOPE)
        .authenticateUsing(this, REQUEST_CODE_BASE) // <= This registers the Fragment/Activity to the authorization flow!
        .subscribeOn(Schedulers.io())
        .subscribe(new Action1<String>() {
            @Override
            public void call(final String token) {
                // Do something with the token!
            }
        });
    ...
}

@Override
public void onAuthenticationError(final Throwable throwable) {
    Toast.makeText(this, "Unknown authentication error!", Toast.LENGTH_SHORT).show();
}

@Override
public void onAuthenticationSucceeded(final String token) {
    Toast.makeText(this, "Authenticated!", Toast.LENGTH_SHORT).show();
}

@Override
public void onRetryAuthentication() {
    Toast.makeText(this, "Retry Authentication!", Toast.LENGTH_SHORT).show();
}
```

[1]: https://github.com/Netflix/RxJava