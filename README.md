# National Insurance DES Stub

The National Insurance DES Stub is a service to support stateful sandbox testing in the
External Test environment. It stubs the behaviour of downstream in order that an API microservice
is able to implement only a single set of routes regardless of whether it is being called
in a test or production environment.

It is a semi-stateful test service - in order to use it, you need to request it to set up test
data for a specific taxpayer and tax year. It will then generate pre-defined test data for that
taxpayer and tax year.

The POST endpoints for setting up test data are exposed on the API Platform as the National Insurance
Test Support API. The GET endpoint is called by the national insurance API microservice.

## What uses this service?

API microservices that make National Inusrance calls downstream which are deployed to the
External Test environment should be configured to connect to this stub instead of a real downstream.

## Developer API

https://developer.service.hmrc.gov.uk/api-documentation/docs/api/service/national-insurance-des-stub/1.0

## Viewing Documentation
### Locally
- Run National Insurance DES Stub and other required services with the script:

    ```bash
     ./run_local_preview_documentation.sh
    ```

- Navigate to the preview page at http://localhost:9680/api-documentation/docs/openapi/preview
- Enter the full URL path of the OpenAPI specification file with the appropriate port and version:

    ```bash
     http://localhost:9688/api/conf/1.0/application.yaml
    ```
- Ensure to uncomment the lines [here](https://github.com/hmrc/national-insurance-des-stub/blob/main/conf/application.conf#L32-L35) in case of CORS errors

## Running the tests
```bash
./run_all_tests.sh
```

## Running the service locally

To run the service locally on port `9688`:
```bash
sbt run
```

## Licence

This code is open source software licensed under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0.html)