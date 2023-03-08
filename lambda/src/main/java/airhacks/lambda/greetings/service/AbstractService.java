package airhacks.lambda.greetings.service;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;


import airhacks.lambda.greetings.model.Fruit;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;

public abstract class AbstractService {

    public final static String FRUIT_NAME_COL = "fruitName";
    public final static String FRUIT_DESC_COL = "fruitDescription";

    @Inject
    @ConfigProperty(name="tableName")
    String tableName;

    protected ScanRequest scanRequest() {
        return ScanRequest.builder().tableName(tableName)
                .attributesToGet(FRUIT_NAME_COL, FRUIT_DESC_COL).build();
    }

    protected PutItemRequest putRequest(Fruit fruit) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put(FRUIT_NAME_COL, AttributeValue.builder().s(fruit.getName()).build());
        item.put(FRUIT_DESC_COL, AttributeValue.builder().s(fruit.getDescription()).build());

        return PutItemRequest.builder()
                .tableName(tableName)
                .item(item)
                .build();
    }

    protected GetItemRequest getRequest(String name) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put(FRUIT_NAME_COL, AttributeValue.builder().s(name).build());

        return GetItemRequest.builder()
                .tableName(tableName)
                .key(key)
                .attributesToGet(FRUIT_NAME_COL, FRUIT_DESC_COL)
                .build();
    }
}