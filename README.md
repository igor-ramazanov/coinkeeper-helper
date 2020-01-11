# Coinkeper Helper
CLI application, improves existing [Coinkeeper](https://coinkeeper.me/) application. Helps being more mindful regarding finances.

After run it will connect to the Coinkeeper backend and extract data to calculate the 2 numbers:

1. Total amount available for spending today ignoring today's transactions.
2. Amount of available money for spending right at the moment.

Example output:
```bash
$ ./coinkeeper-fetcher-out
Total: 2223 Available: -3199
```

## Building
Dependencies:
1. libcurl3-dev or libcurl4-dev
2. libidn11-dev or libidn2-dev or libidn-2-0-dev
3. libre2-dev
4. maybe something else I missed

**TODO**: I couldn't make the project linking work on macOS due to the next issues:
1. https://stackoverflow.com/questions/16682156/ld-library-not-found-for-lgsl
2. https://stackoverflow.com/questions/54068035/linking-not-working-in-homebrews-cmake-since-mojave

Nothing helped me yet, if someone able to fix it - PRs are welcome!

```bash
sbt nativeLink
ls -lh target/scala-2.11/coinkeeper-helper-out
-rwxrwxr-x 1 igorramazanov igorramazanov 6.7M Jan 10 08:54 coinkeeper-fetcher-out
```

## Configuration
Configuration provided by environment variables:
1. `COINKEEPER_BUDGET` - Integer, how much you plan to spend in a month
2. `COINKEEPER_COOKIE` - String, cookie allowing querying `Coinkeeper` backend
3. `COINKEEPER_USER_ID` - String, user id allowing querying `Coinkeeper` backend

## Obtaining COINKEEPER_COOKIE and COINKEEPER_USER_ID

### COINKEEPER_COOKIE
The easiest way is to sniff `XHR` queries made by browser when working with the [web version of the Coinkeeper](https://coinkeeper.me)
![How to find out cookie](/cookie.png)

### COINKEEPER_USER_ID
The easiest way is to sniff `XHR` queries made by browser when working with the [web version of the Coinkeeper](https://coinkeeper.me)
![How to find out user id](/user_id.png)
