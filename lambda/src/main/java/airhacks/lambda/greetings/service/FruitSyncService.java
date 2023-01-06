package airhacks.lambda.greetings.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import airhacks.lambda.greetings.model.Fruit;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@ApplicationScoped
public class FruitSyncService extends AbstractService {

    private DynamoDbTable<Fruit> fruitTable;

    @Inject
    DynamoDbEnhancedClient dynamoEnhancedClient;

    @PostConstruct
    void init() {
        fruitTable = dynamoEnhancedClient.table(getTableName(),
                TableSchema.fromClass(Fruit.class));
        fruitTable.createTable();
    }

    public List<Fruit> findAll() {
        return fruitTable.scan().items().stream().collect(Collectors.toList());
    }

    public List<Fruit> add(Fruit fruit) {
        fruitTable.putItem(fruit);
        return findAll();
    }

    public Fruit get(String name) {
        Key partitionKey = Key.builder().partitionValue(name).build();
        return fruitTable.getItem(partitionKey);
    }

    public void delete(Fruit fruit) {
        fruitTable.deleteItem(fruit);
    }
}