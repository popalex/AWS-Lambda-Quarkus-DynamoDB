package airhacks.lambda.greetings.service;

import java.lang.System.Logger.Level;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;

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

    @Inject
    @ConfigProperty(name = "enableTableCreate")
    Boolean enableTableCreate;

    @Inject
    Context context;

    @Inject
    APIGatewayV2HTTPEvent.RequestContext rc;

    @Inject
    APIGatewayV2HTTPEvent httpEvent;

    @PostConstruct
    void init() {
        try {
            LOG.log(Level.INFO, "Creating dynamoEnhancedClient for table name " + tableName);
            fruitTable = dynamoEnhancedClient.table(tableName,
                    TableSchema.fromClass(Fruit.class));
            if (enableTableCreate) {
                LOG.log(Level.INFO, "Tring to create table " + tableName);
                fruitTable.createTable();
            }
        } catch (ResourceInUseException ex) {
            LOG.log(Level.WARNING, "Table " + tableName + " already exists. Full error is ", ex);
        }
    }

    public List<Fruit> findAll() {
        String username = rc.getAuthorizer().getJwt().getClaims().get("cognito:username");
        LOG.log(Level.DEBUG, "Username is "+ username);
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