package airhacks.dynamodb;

import org.jetbrains.annotations.NotNull;

import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.BillingMode;
import software.amazon.awscdk.services.dynamodb.Table;
import software.amazon.awscdk.services.iam.IGrantable;
import software.constructs.Construct;

public class DynamoDbTable extends Construct{

    private Table table;

    public DynamoDbTable(DynamoDBFunctionStack dynamoDBFunctionStack, String TABLE_NAME) {
        this(dynamoDBFunctionStack, TABLE_NAME, true);
    }

    public DynamoDbTable(Construct scope, String id, Boolean deleteOnDestroy) {
        super(scope, id);
        this.table = Table.Builder.create(this, id)
                .partitionKey(Attribute.builder().name("name").type(AttributeType.STRING).build())
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .removalPolicy(deleteOnDestroy ? RemovalPolicy.DESTROY : RemovalPolicy.RETAIN)
                .build();
    }

    public String getTableName() {
        return table.getTableName();
    }

    public String getTableArn() {
        return table.getTableArn();
    }

}
