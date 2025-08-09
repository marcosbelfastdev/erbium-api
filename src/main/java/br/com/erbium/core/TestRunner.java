/**
 * Provides a fluent API for running tests with multiple iterations and context management in ERBIUM.
 *
 * Author: Marcos Ghiraldelli (https://github.com/marcosbelfastdev/)
 * License: MIT
 *
 * Trademark Notice:
 * The name ERBIUM as it relates to software for testing RESTful APIs,
 * all associated logos, wordmarks, and visual representations of the ERBIUM brand,
 * and all related consultancy services, technical support, and training offerings
 * under the ERBIUM name are protected trademarks.
 */
package br.com.erbium.core;

import br.com.erbium.utils.StringUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.function.Consumer;

/**
 * Author: Marcos Ghiraldelli (https://github.com/marcosbelfastdev/)
 * Description: [Brief description of what this class does]
 *
 * License: MIT
 *
 * Trademark Notice:
 * The name ERBIUM as it relates to software for testing RESTful APIs,
 * all associated logos, wordmarks, and visual representations of the ERBIUM brand,
 * and all related consultancy services, technical support, and training offerings
 * under the ERBIUM name are protected trademarks.
 */

/**
 * Provides a fluent API for running tests with multiple iterations and context management in ERBIUM.
 */
public class TestRunner {
    private int maxIterations = 1;
    @Getter @Setter
    @Accessors(fluent = true)
    private Workspace workspace;
    @Getter @Setter
    @Accessors(fluent = true, chain = true)
    private String testName = "Unnamed Test";
    private Consumer<DefaultTestIteratorContext> testStep;


    /**
     * Sets the number of iterations for the test run.
     *
     * @param count The number of iterations.
     * @return This TestRunner instance for chaining.
     */
    public TestRunner withIterations(@NonNull Integer count) {
        this.maxIterations = count;
        return this;
    }

    /**
     * Executes the provided test step for the configured number of iterations.
     *
     * @param testStep The test step to execute, accepting a DefaultTestIteratorContext.
     * @return This TestRunner instance for chaining.
     */
    public TestRunner execute(Consumer<DefaultTestIteratorContext> testStep) {
        this.testStep = testStep;
        run();
        return this;
    }

    /**
     * Runs the test step for the specified number of iterations, creating a new context for each iteration.
     * <p>
     * This method is called internally by {@link #execute(Consumer)}. It ensures that each test iteration
     * has its own context and that the workspace is updated accordingly. After each iteration, the context is cleared.
     *
     * Related: {@link #withIterations(Integer)}, {@link #execute(Consumer)}
     */
    private void run() {
        for (int i = 1; i <= maxIterations; i++) {
            DefaultTestIteratorContext context = new DefaultTestIteratorContext(i);
            try {
                context.workspace(workspace);
                testStep.accept(context);
            } finally {
                context.clear();
            }
        }
    }

    /**
     * Prints the provided messages using StringUtil and returns this instance.
     *
     * @param messages The messages to print.
     * @return This TestRunner instance for chaining.
     */
    public TestRunner print(@NonNull String... messages) {
        StringUtil.print(messages);
        return this;
    }
}
