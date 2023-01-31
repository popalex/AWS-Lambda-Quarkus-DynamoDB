package airhacks.dynamodb;

import airhacks.lambda.control.QuarkusLambda;
import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.BillingMode;
import software.amazon.awscdk.services.dynamodb.Table;
import software.amazon.awscdk.services.lambda.FunctionUrlAuthType;
import software.amazon.awscdk.services.lambda.FunctionUrlOptions;
import software.constructs.Construct;

public class DynamoDBFunctionStack extends Stack {

    static String FUNCTION_NAME = "airhacks_lambda_gretings_boundary_Greetings";
    static String TABLE_NAME = "fruit";

    public DynamoDBFunctionStack(Construct construct, String funcId, String id) {
        super(construct, funcId + "-function-url-stack");
        var quarkusLambda = new QuarkusLambda(this, FUNCTION_NAME);
        var function = quarkusLambda.getFunction();
        var functionUrl = function.addFunctionUrl(FunctionUrlOptions.builder()
                .authType(FunctionUrlAuthType.NONE)
                .build());

        CfnOutput.Builder.create(this, "FunctionURLOutput").value(functionUrl.getUrl()).build();

        var table = new DynamoDbTable(this, TABLE_NAME);

        CfnOutput.Builder.create(this, "DynamoDbTableName").value(table.getTableName()).build();

    }

}
