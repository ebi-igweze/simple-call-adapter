# Simple Call Adapter
A simple adapter for retrofit 2

## Introduction
This is a simple adapter for retrofit responses. It is made to compose both error and success response that will be passed into one callback and also provide cancellable subscriptions for asynchronous calls.


## Key Classes

| Class | Description |
| ------ | ------ |
| __Simple__ | This is a wrapper ``class`` that wraps the resulting response for the passed callback|
| __Subscription__ | This is the ``class`` that wraps disposable results for asynchronus calls |
| __SimpleCallAdapterFactory__ | This is a factory class for creating the call adapter for retrofit |
| __SimpleCallAdapter__ | This is the retrofit adapter responsible for transforming the retrofit's ``Call`` interface into a ``Simple`` response |


## Setup
First, add the following to your ``repositories`` block
``` groovy
    maven { url "http://dl.bintray.com/ebi-igweze/maven" }
```

then secondly, add this to your module level ``dependencies`` block
```groovy
    implementation "com.igweze.ebi:simple-call-adapter:1.0.0"
```

## Usage
You can use it as follows,

First you have to add the ``SimpleCallAdapterFactory`` to your ``retrofit`` container like so:

```kotlin
    Retrofit.Builder()
        .baseUrl(your_base_url)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(SimpleCallAdapterFactory.create())
        .build()
```

then you declare a ``Simple<T>`` as your return type in your HttpService interface, where ``T`` is the response value your are expecting from your api endpoint, for example:

__Kotlin__


```kotlin
interface IHttpService {
    @GET("users")
    fun getUsers(@Query("count") count: Int): Simple<List<User>>
}
````


__Java__


```java
interface IHttpService {
    @GET("users")
    Simple<List<User>> getUsers(@Query("count") int count)
}
````

finally you can extract your response using a ``java`` functional interface or ``kotlin`` lambda expression, for example:


__Kotlin__
```kotlin

    // run function asynchronously and get a disposable subscription
    val subscription = httpService.getUsers(5).process { users, throwable ->
        // check if error occurred
        if (throwable != null) {
            if (throwable is HttpException) {
                // handle http exception
            } else {
                // handle other errors
            }
        }

        // handle success response
        users?.also { usersList.add(it) }
    }
    // ...

    // dispose subscription manually
    if (!subscription.isDisposed())
        subscription.dispose()

    //...

    // dispose subscription by binding to a lifecycle component
    subscription.bind(this@MainActivity)

    httpService.getUsers(5).run { users, t ->
        // handle error
        if (t != null) { }

        // handle success
        users?.also { userList.add(it) }
    }
```


__Java__
The call back also applies to java but you do it using the ``SimpleHandler`` functional interface, like so:

```Java

    // asynchronous call
    // for java 8
    httpService.getUsers(5).process((users, throwable) -> {
        // handle error or success response
    }).bind(MainActivity.this);

    // synchronous call
    // or for java <= 7
    httpService.getUsers(4).run(new SimpleHandler<List<User>>() {
        @Override
        public void accept(List<User> users, Throwable throwable) {
            // handle error or success response
        }
    });
```

And that is it.