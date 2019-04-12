# java-api-idex
[IDEX](https://idex.market) API JAVA

This library uses the [IDEX API](https://docs.idex.market/).

## Installation

### Using Maven

Add the following dependency to your project's Maven pom.xml:

```xml
<dependency>
	<groupId>com.codemonkeylab.idex</groupId>
	<artifactId>java-api-idex</artifactId>
	<version>0.0.2</version>
</dependency>
```

## Usage

You will need a Ethereum private key with some ether loaded to trade. Some interactions (read) does not require private key.

```java
import org.web3j.crypto.Credentials;
import com.cml.idex.IDexAPI;

final Credentials credentials = Credentials.create("WALLET_PRIVATE_KEY");
final IDexAPI idex = new IDexAPI();
try {
   // Your code here
}
finally {
   idex.shutdown();
}
```

When interacting with ETH trading pair use the IDexAPI.DEFAULT_ETH_ADR as the token address.

## Examples

### Balance

Returns available balances for an address(total deposited minus amount in open orders) indexed by token symbol.

```java
import com.cml.idex.IDexAPI;

final IDexAPI idex = new IDexAPI();
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

### IDEX Contract Address

Returns the contract address used for depositing, withdrawing, and posting orders.

```java
import com.cml.idex.IDexAPI;

final IDexAPI idex = new IDexAPI();
try {
   System.out.println("IDEX Contract Address : " + idex.returnContractAddress().get());
} catch (InterruptedException | ExecutionException e) {
   e.printStackTrace();
} finally {
   idex.shutdown();
}
```

### Trade History for Market

To get trading history for Token Pair. The Results object returned allows you paginate through the results.

```java
import com.cml.idex.IDexAPI;

final IDexAPI idex = new IDexAPI();
try {
   // Market History for ETH_ZCC
   Results<CompletableFuture<List<TradeHistory>>> tradeHistoryF = idex.returnTradeHistoryProducer("ETH_ZCC", null,
            LocalDateTime.of(2019, 1, 1, 1, 1), LocalDateTime.now(), SortOrder.ASC, 50);

   CompletableFuture<List<TradeHistory>> future = tradeHistoryF.next();
   future.join();

   while (future.get() != null && !future.get().isEmpty()) {
      System.out.println("Size = " + future.get().size());
      future = tradeHistoryF.next();
      future.join();
   }
} catch (InterruptedException | ExecutionException e) {
   e.printStackTrace();
} finally {
   idex.shutdown();
}
```