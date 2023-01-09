package airhacks.lambda.greetings.service;

import java.lang.System.Logger.Level;
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
import software.amazon.awssdk.services.dynamodb.model.ResourceInUseException;

@ApplicationScoped
public class FruitSyncService extends AbstractService {

    static System.Logger LOG = System.getLogger(FruitSyncService.class.getName());

    private DynamoDbTable<Fruit> fruitTable;

    @Inject
    DynamoDbEnhancedClient dynamoEnhancedClient;

    @PostConstruct
    void init() {
        try {
            fruitTable = dynamoEnhancedClient.table(getTableName(),
                    TableSchema.fromClass(Fruit.class));
            fruitTable.createTable();
        } catch (ResourceInUseException ex) {
            LOG.log(Level.WARNING, "Table " + getTableName() + " already exists. Full error is ", ex);
        }
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