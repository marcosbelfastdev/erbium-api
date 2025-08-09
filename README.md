# RESTful API Testing Framework

## License

This project is licensed under the MIT License. See the LICENSE file for details.

## Trademark and Services

"Erbium Testing" is a registered trademark of Erbium Testing Ltda.

Official consulting, training, and support services for the Erbium Testing Framework are exclusively provided by Erbium Testing Ltda. Third-party services are not affiliated with or endorsed by the project maintainers.

## Introduction

This repository provides a comprehensive, Java-based framework for designing, importing, and automating tests for RESTful APIs. It is built to support modern development workflows, facilitate collaboration between developers and QA, and ensure robust, maintainable API test coverage.

---

## Main Goals

### 1. Structured API Test Modeling
- **Workspace Abstraction:** Organize API tests into logical workspaces, each containing collections of endpoints and associated environments.
- **Collection & Endpoint Management:** Model API endpoints as first-class objects, grouped into collections for modularity and reuse.

### 2. Seamless Postman Integration
- **Import Postman Collections:** Directly import Postman collections and environments, leveraging existing API definitions and test data.
- **Export/Sync Capabilities:** (Planned/Supported) Export test definitions back to Postman or other tools for round-trip collaboration.

### 3. Fluent, Readable Test Construction
- **Chainable API:** Build test scenarios using a fluent interface, making test code expressive and easy to maintain.

```java
workspace.addCollection("My Collection")
         .addEndpoint("Login")
         .setMethod(Method.POST)
         .setUrl("https://api.example.com/login")
         .getJsonRequest()
         .setBody("{ \"user\": \"test\", \"pass\": \"secret\" }")
         .backToEndpoint()
         .submit();
```

### 4. Advanced Environment & Variable Handling
- **Dynamic Variable Replacement:** Use placeholders in requests (e.g., `{{token}}`) that are automatically replaced from environment data.
- **Environment Import/Export:** Manage multiple environments (dev, staging, prod) with ease.

### 5. Extensible Scripting & Header Management
- **Custom Scripts:** Attach scripts to endpoints or collections for pre/post-processing, validation, or dynamic data generation.
- **Header Triggers:** Automate header management (e.g., authentication tokens) across requests.

### 6. Automation & CI/CD Ready
- **JUnit & Cucumber Integration:** Write and run tests using industry-standard frameworks.
- **Maven Build:** Easily integrate with CI/CD pipelines for automated test execution.

---

## Key Benefits

- **Reusability & Modularity:** Define collections, endpoints, and environments once; reuse across multiple tests and scenarios. Reduces duplication and maintenance overhead.
- **Readability & Maintainability:** Fluent interface and clear abstractions make test code self-documenting. New team members can quickly understand and extend tests.
- **Postman Compatibility:** Leverage existing Postman assets, reducing migration effort and enabling collaboration with non-developers.
- **Robust Variable & Environment Management:** Avoids hardcoding values; supports dynamic, data-driven testing.
- **Extensibility:** Easily add custom logic, scripts, or integrations as requirements evolve.
- **Automation-First:** Designed for headless, repeatable execution in CI/CD environments.

---

## Architectural Overview

- **Core Concepts:**
  - `Workspace`: Top-level container for collections and environments.
  - `Collection`: Group of related API endpoints.
  - `Endpoint`: Represents a single API operation (method, URL, headers, body, etc.).
  - `Environment`: Key-value store for variables used in requests.
  - `Script/Trigger`: Custom logic attached to requests or collections.

- **Key Classes:**
  - `Workspace`, `Collection`, `Endpoint` (in `base.components.core.user`)
  - `RequestManager`, `Headers`, `JsonRequest` (core request/response handling)
  - `CollectionFileImporter`, `PostmanCollectionFetcher` (Postman integration)
  - `HeadersTrigger`, `EndpointScript` (scripting and automation)

- **Testing Layer:**
  - JUnit-based test classes (e.g., `HelloPostman`)
  - Custom test utilities (e.g., `CreateTeamWorkspace`)

---

## Example: Building and Running a Test

```java
@Test
public void testLoginEndpoint() {
    Workspace workspace = new Workspace();
    workspace.addCollection("Auth")
             .addEndpoint("Login")
             .setMethod(Method.POST)
             .setUrl("https://api.example.com/login")
             .getJsonRequest()
             .setBody("{ \"username\": \"user\", \"password\": \"pass\" }")
             .backToEndpoint()
             .submit();
    // Add assertions or further processing as needed
}
```

---

## Comparison: With vs. Without This Framework

| With Framework                                 | Without Framework                        |
|------------------------------------------------|------------------------------------------|
| Reusable, modular test code                    | Duplicated, hard-to-maintain scripts     |
| Fluent, readable test construction             | Imperative, error-prone code             |
| Automated variable/environment management      | Manual, inconsistent substitutions       |
| Postman import/export                          | Manual recreation of endpoints           |
| Built-in scripting and header automation       | Ad-hoc, scattered logic                  |
| CI/CD integration (JUnit, Maven)               | Difficult to automate, fragile scripts   |
| Easy onboarding and collaboration              | Steep learning curve, tribal knowledge   |

---

## When Should You Use This Framework?

- When you need to automate RESTful API testing in Java.
- When you want to leverage existing Postman collections.
- When you require maintainable, scalable, and readable test code.
- When you need to support multiple environments and dynamic data.
- When you want to integrate API tests into your CI/CD pipeline.

---

## Conclusion

This framework brings structure, automation, and maintainability to RESTful API testing. It bridges the gap between manual tools like Postman and automated, code-driven testing, empowering teams to deliver higher quality APIs, faster.
