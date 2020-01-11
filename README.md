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
1. libcurl-dev (a version seem to not matter at all, prefer the latest)
2. libidn-dev (the first version, not the second)
3. libre2-dev (exactly the second version)
4. maybe something else I missed

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
