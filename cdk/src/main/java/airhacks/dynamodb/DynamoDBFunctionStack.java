package airhacks.dynamodb;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import airhacks.lambda.control.QuarkusLambda;
import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.apigateway.LambdaRestApi;
import software.amazon.awscdk.services.apigatewayv2.alpha.HttpApi;
import software.amazon.awscdk.services.apigatewayv2.alpha.PayloadFormatVersion;
import software.amazon.awscdk.services.apigatewayv2.integrations.alpha.HttpLambdaIntegration;
import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.BillingMode;
import software.amazon.awscdk.services.dynamodb.Table;
import software.amazon.awscdk.services.iam.AnyPrincipal;
import software.amazon.awscdk.services.iam.PolicyStatement;
import software.amazon.awscdk.services.lambda.FunctionUrlAuthType;
import software.amazon.awscdk.services.lambda.FunctionUrlOptions;
import software.amazon.awscdk.services.lambda.IFunction;
import software.constructs.Construct;

public class DynamoDBFunctionStack extends Stack {

    static String FUNCTION_NAME = "airhacks_lambda_gretings_boundary_Greetings";
    static String TABLE_NAME = "fruit";

    public DynamoDBFunctionStack(Construct construct, String funcId, String id, boolean functionUrlIntegration, Boolean httpAPIGatewayIntegration) {
        super(construct, funcId + "-function-url-stack");

        var table = new DynamoDbTable(this, TABLE_NAME);

        CfnOutput.Builder.create(this, "DynamoDbTableName").value(table.getTableName()).build();

        var quarkusLambda = new QuarkusLambda(this, FUNCTION_NAME, table.getTableName());
        var function = quarkusLambda.getFunction();

        if (functionUrlIntegration) {
            var functionUrl = function.addFunctionUrl(FunctionUrlOptions.builder()
                    .authType(FunctionUrlAuthType.NONE)
                    .build());
            CfnOutput.Builder.create(this, "FunctionURLOutput").value(functionUrl.getUrl()).build();
        }

        function.addToRolePolicy(PolicyStatement.Builder.create()
                .actions(List.of("dynamodb:DescribeTable", "dynamodb:ListTables", "dynamodb:PutItem",
                        "dynamodb:GetItem", "dynamodb:DeleteItem", "dynamodb:Scan"))
                .resources(List.of(table.getTableArn()))
                .build());

        

        if (httpAPIGatewayIntegration != null) {
            if (httpAPIGatewayIntegration)
                integrateWithHTTPApiGateway(function);
            else
                integrateWithRestApiGateway(function);

                CfnOutput.Builder.create(this, "FunctionHttpApiIntegration").value(String.valueOf(httpAPIGatewayIntegration))
                .build();
        }

    }

    private void integrateWithHTTPApiGateway(IFunction function) {
        var lambdaIntegration = HttpLambdaIntegration.Builder.create("HttpApiGatewayIntegration", function)
                .payloadFormatVersion(PayloadFormatVersion.VERSION_2_0)
                .build();
        var httpApiGateway = HttpApi.Builder.create(this, "HttpApiGatewayIntegration")
                .defaultIntegration(lambdaIntegration).build();
        var url = httpApiGateway.getUrl();
        CfnOutput.Builder.create(this, "HttpApiGatewayUrlOutput").value(url).build();
        CfnOutput.Builder.create(this, "HttpApiGatewayCurlOutput").value("curl -i " + url + "hello").build();
    }

    private void integrateWithRestApiGateway(IFunction function) {
        var apiGateway = LambdaRestApi.Builder.create(this, "RestApiGateway").handler(function).build();
        CfnOutput.Builder.create(this, "RestApiGatewayUrlOutput").value(apiGateway.getUrl()).build();
    }

}
