package airhacks.lambda.control;

import java.util.HashMap;
import java.util.Map;

import airhacks.functionurl.boundary.FunctionURLStack;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.services.lambda.Architecture;
import software.amazon.awscdk.services.lambda.CfnFunction;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.IFunction;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.lambda.Version;
import software.constructs.Construct;

public class QuarkusLambda extends Construct {

    static Map<String, String> configuration = Map.of(
            "message", "hello, quarkus as AWS Lambda",
            "JAVA_TOOL_OPTIONS", "-XX:+TieredCompilation -XX:TieredStopAtLevel=1");
    static String lambdaHandler = "io.quarkus.amazon.lambda.runtime.QuarkusStreamHandler::handleRequest";
    static int memory = 1024; // ~0.5 vCPU
    static int timeout = 10;
    IFunction function;

    public QuarkusLambda(Construct scope, String functionName) {
        this(scope,functionName,"", false);
    }

    public QuarkusLambda(Construct scope, String functionName, String dynamoDbTableName){
        this(scope,functionName,dynamoDbTableName, false);
    }

    public QuarkusLambda(Construct scope, String functionName, String dynamoDbTableName, boolean snapStart) {
        super(scope, "QuarkusLambda");
        var mConfig = new HashMap<String, String>(configuration);
        mConfig.put("tableName", dynamoDbTableName);
        
        this.function = createFunction(functionName, lambdaHandler, mConfig, memory, timeout);
        if (snapStart)
            this.function = setupSnapStart(this.function);
    }

    Version setupSnapStart(IFunction function) {
        var defaultChild = this.function.getNode().getDefaultChild();
        if (defaultChild instanceof CfnFunction) {
            var cfnFunction = (CfnFunction) defaultChild;
            cfnFunction.addPropertyOverride("SnapStart", Map.of("ApplyOn", "PublishedVersions"));
        }
        return Version.Builder.create(this, "SnapStartVersion")
                .lambda(this.function)
                .description("SnapStart")
                .build();
    }

    IFunction createFunction(String functionName, String functionHandler, Map<String, String> configuration, int memory,
            int timeout) {
        return Function.Builder.create(this, functionName)
                .runtime(Runtime.JAVA_11)
                .architecture(Architecture.ARM_64)
                .code(Code.fromAsset("../lambda/target/function.zip"))
                .handler(functionHandler)
                .memorySize(memory)
                .functionName(functionName)
                .environment(configuration)
                .timeout(Duration.seconds(timeout))
                .build();
    }

    public IFunction getFunction() {
        return this.function;
    }
}