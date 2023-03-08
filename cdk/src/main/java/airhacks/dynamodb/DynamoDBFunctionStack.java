package airhacks.dynamodb;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import airhacks.lambda.control.QuarkusLambda;
import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.BillingMode;
import software.amazon.awscdk.services.dynamodb.Table;
import software.amazon.awscdk.services.iam.AnyPrincipal;
import software.amazon.awscdk.services.iam.PolicyStatement;
import software.amazon.awscdk.services.lambda.FunctionUrlAuthType;
import software.amazon.awscdk.services.lambda.FunctionUrlOptions;
import software.constructs.Construct;

public class DynamoDBFunctionStack extends Stack {

    static String FUNCTION_NAME = "airhacks_lambda_gretings_boundary_Greetings";
    static String TABLE_NAME = "fruit";

    public DynamoDBFunctionStack(Construct construct, String funcId, String id) {
        super(construct, funcId + "-function-url-stack");

        var table = new DynamoDbTable(this, TABLE_NAME);

        CfnOutput.Builder.create(this, "DynamoDbTableName").value(table.getTableName()).build();

        var quarkusLambda = new QuarkusLambda(this, FUNCTION_NAME, table.getTableName());
        var function = quarkusLambda.getFunction();
        var functionUrl = function.addFunctionUrl(FunctionUrlOptions.builder()
                .authType(FunctionUrlAuthType.NONE)
                .build());

        function.addToRolePolicy(PolicyStatement.Builder.create()
                .actions(List.of("dynamodb:DescribeTable", "dynamodb:ListTables", "dynamodb:PutItem", "dynamodb:GetItem", "dynamodb:DeleteItem", "dynamodb:Scan"))
                .resources(List.of(table.getTableArn()))
                .build());

        CfnOutput.Builder.create(this, "FunctionURLOutput").value(functionUrl.getUrl()).build();

        

    }

}
