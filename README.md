# java-api-idex
[IDEX](https://idex.market) API JAVA

This library uses the [IDEX API](https://docs.idex.market/).

## Installation

### Using Maven

Add the following dependency to your project's Maven pom.xml:

```xml
<dependency>
	<groupId>com.cml.idex</groupId>
	<artifactId>java-api-idex</artifactId>
	<version>1.10.0</version>
</dependency>
```

## Usage

You will need a Ethereum private key with some ether loaded to trade. Some interactions (read) does not require private key.

```java
import org.web3j.crypto.Credentials;
import com.cml.idex.IDexAPI;

final Credentials credentials = Credentials.create("WALLET_PRIVATE_KEY");
final IDexAPI idex = new IDexAPI();
```

When interacting with ETH trading pair use the IDexAPI.DEFAULT_ETH_ADR as the token address.

## Examples

### Balance

Returns available balances for an address(total deposited minus amount in open orders) indexed by token symbol.

```java
import com.cml.idex.IDexAPI;

final IDexAPI idex = new IDexAPI();
CompletableFuture<Map<String, BigDecimal>> balFuture = idex.returnBalances("ETH_ADR");
try {
   balFuture.get().entrySet()
      .forEach(entry -> System.out.println("ERC20: " + entry.getKey() + ", Balance: " + entry.getValue()));
} catch (InterruptedException | ExecutionException e) {
   e.printStackTrace();
}
```

### IDEX Contract Address

Returns the contract address used for depositing, withdrawing, and posting orders.

```java
import com.cml.idex.IDexAPI;

final IDexAPI idex = new IDexAPI();
idex.returnBalances("ETH_ADR");
```