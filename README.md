# java-api-idex
[IDEX](https://idex.market) API JAVA

This library uses the [IDEX API](https://docs.idex.market/).

Currently implemented with changes up to [2019-08-05](https://docs.idex.market/#tag/Change-Log)

## Installation

### Using Maven

Add the following dependency to your project's Maven pom.xml:

```xml
<dependency>
	<groupId>com.codemonkeylab.idex</groupId>
	<artifactId>java-api-idex</artifactId>
	<version>0.3.0</version>
</dependency>
```

## Usage

You will need a Ethereum private key with some ether loaded to trade. Some interactions (read) does not require private key.

```java
import org.web3j.crypto.Credentials;
import com.cml.idex.IDexAPI;

final Credentials credentials = Credentials.create("WALLET_PRIVATE_KEY");
final IDexAPI idex = IDexAPI.create("API_KEY");
try {
   // Your code here
}
finally {
   idex.shutdown();
}
```

When interacting with ETH trading pair use the IDexAPI.DEFAULT_ETH_ADR as the token address.

## Examples

### HTTP API

Mostly used to interact (submit/cancel trade, withdraw/deposit, history, etc) with the Exchange.

#### Balance

Returns available balances for an address(total deposited minus amount in open orders) indexed by token symbol.

```java
import com.cml.idex.IDexAPI;

final IDexAPI idex = IDexAPI.create("API_KEY");
try {
   CompletableFuture<Map<String, BigDecimal>> balFuture = idex.returnBalances("ETH_ADR");
   balFuture.get().entrySet()
      .forEach(entry -> System.out.println("ERC20: " + entry.getKey() + ", Balance: " + entry.getValue()));
} catch (InterruptedException | ExecutionException e) {
   e.printStackTrace();
} finally {
   idex.shutdown();
}
```

#### IDEX Contract Address

Returns the contract address used for depositing, withdrawing, and posting orders.

```java
import com.cml.idex.IDexAPI;

final IDexAPI idex = IDexAPI.create("API_KEY");
try {
   System.out.println("IDEX Contract Address : " + idex.returnContractAddress().get());
} catch (InterruptedException | ExecutionException e) {
   e.printStackTrace();
} finally {
   idex.shutdown();
}
```

#### Trade History for Market

To get trading history for Token Pair. The Results object returned allows you paginate through the results.

```java
import com.cml.idex.IDexAPI;

final IDexAPI idex = IDexAPI.create("API_KEY");
try {
   // Market History for ETH_ZCC
   CompletableFuture<Page<List<TradeHistory>>> tradeHistoryF = idex.returnTradeHistoryPage("ETH_ZCC", null,
            LocalDateTime.of(2019, 1, 1, 1, 1), LocalDateTime.now(), SortOrder.ASC, 50);

   future.join();

   while (tradeHistoryF.get().getResults() != null && !tradeHistoryF.get().getResults().isEmpty()) {
      // Current page
      final Page<List<TradeHistory>> page = tradeHistoryF.get();
      // Results for this page
      final List<TradeHistory> trades = page.getResults();
      
      System.out.println("Size = " + trades.size());
      // Return the next Page results
      tradeHistoryF = page.nextPage();
      tradeHistoryF.join();
   }
} catch (InterruptedException | ExecutionException e) {
   e.printStackTrace();
} finally {
   idex.shutdown();
}
```

### Datastream Realtime API

The IDEX Datastream API allows you to subscribe to realtime events and receive the requested updates as quickly as possible.
